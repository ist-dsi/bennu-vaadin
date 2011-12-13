/*
 * Copyright 2011 Instituto Superior Tecnico
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the vaadin-framework.
 *
 *   The vaadin-framework Infrastructure is free software: you can 
 *   redistribute it and/or modify it under the terms of the GNU Lesser General 
 *   Public License as published by the Free Software Foundation, either version 
 *   3 of the License, or (at your option) any later version.*
 *
 *   vaadin-framework is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with vaadin-framework. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package pt.ist.vaadinframework;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import pt.ist.vaadinframework.data.BufferedProperty;
import pt.ist.vaadinframework.domainMockups.Identification;

import com.vaadin.data.Property.ReadOnlyException;
import com.vaadin.data.Property.ReadOnlyStatusChangeEvent;
import com.vaadin.data.Property.ReadOnlyStatusChangeListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.ObjectProperty;

/**
 * @author Pedro Santos (pedro.miguel.santos@ist.utl.pt)
 * 
 */
public class DirectPropertyTests {
    private Identification id1;

    private Identification id2;

    private Identification id3;

    private boolean expectingNotify;

    private final ValueChangeListener changeListener = new ValueChangeListener() {
	@Override
	public void valueChange(ValueChangeEvent event) {
	    assertTrue(expectingNotify);
	    expectingNotify = false;
	}
    };

    private boolean expectingReadOnlyChange;

    private final ReadOnlyStatusChangeListener readOnlyLintener = new ReadOnlyStatusChangeListener() {
	@Override
	public void readOnlyStatusChange(ReadOnlyStatusChangeEvent event) {
	    assertTrue(expectingReadOnlyChange);
	    expectingReadOnlyChange = false;
	}
    };

    private ObjectProperty<Identification> wrapped;

    private final Validator mustBeId1 = new Validator() {
	@Override
	public void validate(Object value) throws InvalidValueException {
	    if (!id1.equals(value)) {
		throw new InvalidValueException("nope");
	    }
	}

	@Override
	public boolean isValid(Object value) {
	    return id1.equals(value);
	}
    };

    @Before
    public void setUp() throws Exception {
	id1 = new Identification(Identification.Type.CITIZEN_CARD, "id1");
	id2 = new Identification(Identification.Type.CITIZEN_CARD, "id2");
	id3 = new Identification(Identification.Type.CITIZEN_CARD, "id3");
	wrapped = new ObjectProperty<Identification>(id1, Identification.class);
	expectingNotify = false;
	expectingReadOnlyChange = false;
    }

    @Test
    public final void testBufferedPropertyOnConstruction() {
	BufferedProperty<Identification> identification = new BufferedProperty<Identification>(Identification.class);
	identification.addListener(changeListener);
	expectingNotify = true;
	identification.setValue(id1);
	assertFalse(expectingNotify);
	assertSame(id1, identification.getValue());
	identification.commit();
	assertSame(id1, identification.getValue());
    }

    @Test
    public final void testBufferedPropertyWithWrapper() {
	BufferedProperty<Identification> identification = new BufferedProperty<Identification>(wrapped);
	identification.addListener(changeListener);

	assertSame(id1, wrapped.getValue());
	assertSame(id1, identification.getValue());
	expectingNotify = true;
	identification.setValue(id2);
	assertFalse(expectingNotify);
	assertSame(id1, wrapped.getValue());
	assertSame(id2, identification.getValue());
	identification.commit();
	assertSame(id2, wrapped.getValue());
	assertSame(id2, identification.getValue());
	identification.setWriteThrough(true);
	expectingNotify = true;
	identification.setValue(id1);
	assertFalse(expectingNotify);
	assertSame(id1, wrapped.getValue());
	assertSame(id1, identification.getValue());
    }

    @Test
    public final void testBufferedPropertyReadOnly() {
	BufferedProperty<Identification> identification = new BufferedProperty<Identification>(wrapped);
	identification.addListener(readOnlyLintener);

	expectingReadOnlyChange = true;
	identification.setReadOnly(true);
	assertFalse(expectingReadOnlyChange);

	try {
	    identification.setValue(id2);
	    fail("writing read only property");
	} catch (ReadOnlyException e) {
	    assertSame(id1, identification.getValue());
	}

	expectingReadOnlyChange = true;
	identification.setReadOnly(false);
	assertFalse(expectingReadOnlyChange);

	identification.setValue(id2);
	assertSame(id2, identification.getValue());
    }

    @Test
    public final void testBufferedPropertyValidation() {
	BufferedProperty<Identification> identification = new BufferedProperty<Identification>(wrapped);
	identification.addListener(readOnlyLintener);
	identification.addValidator(mustBeId1);

	// invalidAllowed is true, we can set
	identification.setValue(id2);

	identification.setInvalidAllowed(false);
	try {
	    // id1 is valid, we can set
	    identification.setValue(id1);
	    // invalidAllowed is false and id2 is not valid
	    identification.setValue(id2);
	    fail("able to set invalid values on invalidAllowed = false");
	} catch (InvalidValueException e) {
	    assertSame(id1, identification.getValue());
	}

	// free-way
	identification.setInvalidAllowed(true);
	identification.setInvalidCommitted(true);
	identification.setValue(id2);
	identification.commit();
	assertSame(id2, wrapped.getValue());

	// setValue is ok, but commit is not
	identification.setInvalidCommitted(false);
	identification.setValue(id3);
	try {
	    identification.commit();
	    fail("commit should break for invalidity");
	} catch (InvalidValueException e) {
	    assertSame(id2, wrapped.getValue());
	}
    }

    @Test
    public final void testBufferedPropertyModifications() {
	BufferedProperty<Identification> identification = new BufferedProperty<Identification>(wrapped);
	identification.addListener(changeListener);

	// setting to same should do nothing
	identification.setValue(id1);
	assertFalse(identification.isModified());

	// putting back 'persisted' value should make property unmodified
	expectingNotify = true;
	identification.setValue(id2);
	assertFalse(expectingNotify);
	expectingNotify = true;
	identification.setValue(id1);
	assertFalse(expectingNotify);
	assertFalse(identification.isModified());
    }
}
