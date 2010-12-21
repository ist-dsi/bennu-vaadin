/*
 * Copyright 2010 Instituto Superior Tecnico
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
package pt.ist.vaadinframework.ui.fields;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import com.vaadin.data.Buffered;
import com.vaadin.data.Container;
import com.vaadin.data.Validatable;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Field;

/**
 * @author Pedro Santos (pedro.miguel.santos@ist.utl.pt)
 */
@SuppressWarnings("serial")
public class ContainerEditor extends AbstractField implements Container.Editor, Buffered, Validatable {
    private Container containerDataSource;

    /**
     * Mapping from propertyName to corresponding field.
     */
    private final Map<Object, Field> fields = new HashMap<Object, Field>();

    /**
     * Current buffered source exception.
     */
    private Buffered.SourceException currentBufferedSourceException = null;

    /**
     * If this is true, commit implicitly calls setValidationVisible(true).
     */
    private final boolean validationVisibleOnCommit = true;

    public ContainerEditor() {
	super();
	setWriteThrough(false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.AbstractField#commit()
     */
    @Override
    public void commit() throws SourceException, InvalidValueException {
	LinkedList<SourceException> problems = null;

	// Only commit on valid state if so requested
	if (!isInvalidCommitted() && !isValid()) {
	    /*
	     * The values are not ok and we are told not to commit invalid
	     * values
	     */
	    if (validationVisibleOnCommit) {
		setValidationVisible(true);
	    }

	    // Find the first invalid value and throw the exception
	    validate();
	}

	for (Object itemId : containerDataSource.getItemIds()) {
	    try {
		Field field = fields.get(itemId);
		if (!field.isReadOnly()) {
		    field.commit();
		}
	    } catch (Buffered.SourceException e) {
		if (problems == null) {
		    problems = new LinkedList<SourceException>();
		}
		problems.add(e);
	    }
	}

	// No problems occurred
	if (problems == null) {
	    if (currentBufferedSourceException != null) {
		currentBufferedSourceException = null;
		requestRepaint();
	    }
	    return;
	}

	// Commit problems
	final Throwable[] causes = new Throwable[problems.size()];
	int index = 0;
	for (final Iterator<SourceException> i = problems.iterator(); i.hasNext();) {
	    causes[index++] = i.next();
	}
	final Buffered.SourceException e = new Buffered.SourceException(this, causes);
	currentBufferedSourceException = e;
	requestRepaint();
	throw e;
    }

    /**
     * @see com.vaadin.data.Container.Viewer#setContainerDataSource(com.vaadin.data
     *      .Container)
     */
    @Override
    public void setContainerDataSource(Container newDataSource) {
	// TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Container.Viewer#getContainerDataSource()
     */
    @Override
    public Container getContainerDataSource() {
	// TODO Auto-generated method stub
	return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.ui.AbstractField#getType()
     */
    @Override
    public Class getType() {
	// TODO Auto-generated method stub
	return null;
    }
}
