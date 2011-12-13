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

import org.junit.Before;
import org.junit.Test;

import pt.ist.vaadinframework.data.ItemConstructor;
import pt.ist.vaadinframework.domainMockups.Contact;
import pt.ist.vaadinframework.domainMockups.Identification;
import pt.ist.vaadinframework.mockupProxies.ReflectionItem;

import com.vaadin.data.Item.PropertySetChangeEvent;
import com.vaadin.data.Item.PropertySetChangeListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;

/**
 * @author Pedro Santos (pedro.miguel.santos@ist.utl.pt)
 */
public class ItemTests {
    private Identification id1;

    private Identification id2;

    private boolean expectingPropertySetChange;

    private final PropertySetChangeListener propertySetListener = new PropertySetChangeListener() {
	@Override
	public void itemPropertySetChange(PropertySetChangeEvent event) {
	    assertTrue(expectingPropertySetChange);
	    expectingPropertySetChange = false;
	}
    };

    private boolean expectingNotify;

    private final ValueChangeListener changeListener = new ValueChangeListener() {
	@Override
	public void valueChange(ValueChangeEvent event) {
	    assertTrue(expectingNotify);
	    expectingNotify = false;
	}
    };

    public class IdConstructor implements ItemConstructor<String> {
	@Override
	public String[] getOrderedArguments() {
	    return new String[] { "type", "value" };
	}

	public Identification construct(Identification.Type type, String value) {
	    return new Identification(type, value);
	}
    }

    @Before
    public void setUp() throws Exception {
	id1 = new Identification(Identification.Type.CITIZEN_CARD, "id1");
	id2 = new Identification(Identification.Type.CITIZEN_CARD, "id2");
	expectingPropertySetChange = false;
	expectingNotify = false;
    }

    @Test
    public final void testItemCreation() {
	ReflectionItem<Contact> identification = new ReflectionItem<Contact>(Contact.class);
	identification.addListener(propertySetListener);
	identification.addListener(changeListener);

	expectingPropertySetChange = true;
	identification.getItemProperty("type").setValue(Contact.Type.EMAIL);
	assertFalse(expectingPropertySetChange);

	expectingPropertySetChange = true;
	identification.getItemProperty("value").setValue("xpto@cenas.pt");
	assertFalse(expectingPropertySetChange);

	expectingNotify = true;
	identification.commit();
	assertFalse(expectingNotify);
	assertSame(Contact.Type.EMAIL, identification.getValue().getType());
	assertSame("xpto@cenas.pt", identification.getValue().getValue());
    }

    @Test
    public final void testItemCreationWithConstructor() {
	ReflectionItem<Identification> identification = new ReflectionItem<Identification>(Identification.class);
	identification.setConstructor(new IdConstructor());
	identification.addListener(propertySetListener);
	identification.addListener(changeListener);

	expectingPropertySetChange = true;
	identification.getItemProperty("type").setValue(Identification.Type.CITIZEN_CARD);
	assertFalse(expectingPropertySetChange);

	expectingPropertySetChange = true;
	identification.getItemProperty("value").setValue("id1");
	assertFalse(expectingPropertySetChange);

	expectingNotify = true;
	identification.commit();
	assertFalse(expectingNotify);
	assertSame(Identification.Type.CITIZEN_CARD, identification.getValue().getType());
	assertSame("id1", identification.getValue().getValue());
    }

    @Test
    public final void testItemModifyValueWithItemChanges() {
	ReflectionItem<Identification> identification = new ReflectionItem<Identification>(id1);
	identification.addListener(propertySetListener);
	identification.addListener(changeListener);

	expectingPropertySetChange = true;
	identification.getItemProperty("type").setValue(Identification.Type.PASSPORT);
	assertFalse(expectingPropertySetChange);

	expectingPropertySetChange = true;
	identification.getItemProperty("value").setValue("no see me");
	assertFalse(expectingPropertySetChange);

	// setting the value discards all the changes
	expectingNotify = true;
	identification.setValue(id2);
	assertFalse(expectingNotify);
	assertSame(Identification.Type.CITIZEN_CARD, identification.getItemProperty("type").getValue());
	assertSame("id2", identification.getItemProperty("value").getValue());
    }
}
