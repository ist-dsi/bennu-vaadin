/*
 * Copyright 2011 Instituto Superior Tecnico
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the vaadin-framework-ant.
 *
 *   The vaadin-framework-ant Infrastructure is free software: you can 
 *   redistribute it and/or modify it under the terms of the GNU Lesser General 
 *   Public License as published by the Free Software Foundation, either version 
 *   3 of the License, or (at your option) any later version.*
 *
 *   vaadin-framework-ant is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with vaadin-framework-ant. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package com.vaadin.data.util;

import pt.ist.fenixframework.pstm.AbstractDomainObject;

import com.vaadin.data.Item;
import com.vaadin.data.Property;

/**
 * Custom property attached to a {@link CustomItem}. It will create the host
 * item when needed and delegates to the implementation how to read and write
 * the property from a domain object.
 * 
 * @author Pedro Santos (pedro.miguel.santos@ist.utl.pt)
 */
public abstract class CustomProperty<Host extends AbstractDomainObject> extends NotifierProperty implements Property,
	Property.ValueChangeNotifier, Property.ReadOnlyStatusChangeNotifier {
    private static final long serialVersionUID = -7997430708865201490L;

    protected final CustomItem<Host> host;

    public CustomProperty(CustomItem<Host> host) {
	this.host = host;
    }

    /**
     * @see com.vaadin.data.Property#getValue()
     */
    @Override
    public Object getValue() {
	if (host.getValue() != null) {
	    return getValueFrom(host.getValue());
	}
	return getNullValue();
    }

    /**
     * Getter for the null value of the property. Used when the host is not yet
     * bound to a domain object from which to extract the value.
     * 
     * @return Instance of the same type as the Property's value.
     */
    protected abstract Object getNullValue();

    /**
     * Getter for the property on an existing domain object of the host
     * {@link Item}
     * 
     * @param host The domain object hosting this property.
     * @return Instance of the same type as returned by {@link #getType()}.
     */
    protected abstract Object getValueFrom(Host host);

    /**
     * @see com.vaadin.data.Property#setValue(java.lang.Object)
     */
    @Override
    public void setValue(Object newValue) throws ReadOnlyException, ConversionException {
	if (isReadOnly()) {
	    throw new ReadOnlyException();
	}
	setValueOn(host.getOrCreateValue(), newValue);
	fireValueChange();
    }

    /**
     * Setter for the property on the host domain object, the host will be
     * created if it does not yet exist.
     * 
     * @param host The domain object hosting this property.
     * @param newValue The new value of the property.
     * @throws ConversionException If the new value is not, or cannot be
     *             converted to, the property type.
     */
    protected abstract void setValueOn(Host host, Object newValue) throws ConversionException;

    /**
     * @see com.vaadin.data.Property#getType()
     */
    @Override
    public Class<?> getType() {
	return getValue() != null ? getValue().getClass() : getNullValue().getClass();
    }

    @Override
    public String toString() {
	return getValue() != null ? getValue().toString() : null;
    }
}
