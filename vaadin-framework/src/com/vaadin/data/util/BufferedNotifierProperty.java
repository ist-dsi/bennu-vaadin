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
package com.vaadin.data.util;

import java.util.EventObject;
import java.util.LinkedList;

import com.vaadin.data.Buffered;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ReadOnlyStatusChangeNotifier;
import com.vaadin.data.Property.ValueChangeNotifier;
import com.vaadin.data.Validator.InvalidValueException;

/**
 * @author Pedro Santos (pedro.miguel.santos@ist.utl.pt)
 */
@SuppressWarnings("serial")
public abstract class BufferedNotifierProperty implements Property, Buffered, ValueChangeNotifier, ReadOnlyStatusChangeNotifier {
    private boolean writeThrough;

    private boolean readThrough;

    private boolean readOnly;

    private LinkedList<ValueChangeListener> valueChangeListeners;

    private LinkedList<ReadOnlyStatusChangeListener> readOnlyStatusChangeListeners;

    /**
     * @see com.vaadin.data.Buffered#isWriteThrough()
     */
    @Override
    public boolean isWriteThrough() {
	return writeThrough;
    }

    /**
     * @see com.vaadin.data.Buffered#setWriteThrough(boolean)
     */
    @Override
    public void setWriteThrough(boolean writeThrough) throws SourceException, InvalidValueException {
	this.writeThrough = writeThrough;
    }

    /**
     * @see com.vaadin.data.Buffered#isReadThrough()
     */
    @Override
    public boolean isReadThrough() {
	return readThrough;
    }

    /**
     * @see com.vaadin.data.Buffered#setReadThrough(boolean)
     */
    @Override
    public void setReadThrough(boolean readThrough) throws SourceException {
	this.readThrough = readThrough;
    }

    /**
     * @see com.vaadin.data.Property#isReadOnly()
     */
    @Override
    public boolean isReadOnly() {
	return readOnly;
    }

    /**
     * @see com.vaadin.data.Property#setReadOnly(boolean)
     */
    @Override
    public void setReadOnly(boolean readOnly) {
	final boolean prevStatus = readOnly;
	this.readOnly = readOnly;
	if (prevStatus != readOnly) {
	    fireReadOnlyStatusChange();
	}
    }

    private class ValueChangeEvent extends EventObject implements Property.ValueChangeEvent, Property.ReadOnlyStatusChangeEvent {
	protected ValueChangeEvent(BufferedNotifierProperty source) {
	    super(source);
	}

	public BufferedNotifierProperty getProperty() {
	    return (BufferedNotifierProperty) getSource();
	}
    }

    /**
     * @see com.vaadin.data.Property.ValueChangeNotifier#addListener(com.vaadin.data
     *      .Property.ValueChangeListener)
     */
    @Override
    public void addListener(ValueChangeListener listener) {
	if (valueChangeListeners == null) {
	    valueChangeListeners = new LinkedList<ValueChangeListener>();
	}
	valueChangeListeners.add(listener);
    }

    /**
     * @see com.vaadin.data.Property.ValueChangeNotifier#removeListener(com.vaadin
     *      .data.Property.ValueChangeListener)
     */
    @Override
    public void removeListener(ValueChangeListener listener) {
	if (valueChangeListeners != null) {
	    valueChangeListeners.remove(listener);
	}
    }

    /**
     * Sends a value change event to all registered listeners.
     */
    protected void fireValueChange() {
	if (valueChangeListeners != null) {
	    final Object[] l = valueChangeListeners.toArray();
	    final ValueChangeEvent event = new ValueChangeEvent(this);
	    for (int i = 0; i < l.length; i++) {
		((Property.ValueChangeListener) l[i]).valueChange(event);
	    }
	}
    }

    /**
     * @see com.vaadin.data.Property.ReadOnlyStatusChangeNotifier#addListener(com
     *      .vaadin.data.Property.ReadOnlyStatusChangeListener)
     */
    @Override
    public void addListener(ReadOnlyStatusChangeListener listener) {
	if (readOnlyStatusChangeListeners == null) {
	    readOnlyStatusChangeListeners = new LinkedList<ReadOnlyStatusChangeListener>();
	}
	readOnlyStatusChangeListeners.add(listener);
    }

    /**
     * @see com.vaadin.data.Property.ReadOnlyStatusChangeNotifier#removeListener(com.vaadin.data.Property.ReadOnlyStatusChangeListener)
     */
    @Override
    public void removeListener(ReadOnlyStatusChangeListener listener) {
	if (readOnlyStatusChangeListeners != null) {
	    readOnlyStatusChangeListeners.remove(listener);
	}
    }

    /**
     * Sends a read only status change event to all registered listeners.
     */
    protected void fireReadOnlyStatusChange() {
	if (readOnlyStatusChangeListeners != null) {
	    final Object[] l = readOnlyStatusChangeListeners.toArray();
	    final ValueChangeEvent event = new ValueChangeEvent(this);
	    for (int i = 0; i < l.length; i++) {
		((Property.ReadOnlyStatusChangeListener) l[i]).readOnlyStatusChange(event);
	    }
	}
    }
}
