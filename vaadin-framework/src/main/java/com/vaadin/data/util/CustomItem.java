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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import jvstm.PerTxBox;
import pt.ist.fenixframework.pstm.AbstractDomainObject;

import com.vaadin.data.Item;
import com.vaadin.data.Property;

/**
 * Abstract {@link Item} implementation for domain objects. Manages the creation
 * of the object keeping the interface equal for both object creation and
 * edition.
 * 
 * @author Pedro Santos (pedro.miguel.santos@ist.utl.pt)
 */
public abstract class CustomItem<Type extends AbstractDomainObject> extends NotifierItemProperty implements Item,
	Item.PropertySetChangeNotifier, Property, Property.ValueChangeNotifier, Property.ReadOnlyStatusChangeNotifier {
    private static final long serialVersionUID = 4760018698456703812L;

    private final PerTxBox<Type> value;

    private final Class<? extends Type> type;

    private final Map<Object, Property> properties = new HashMap<Object, Property>();

    /**
     * Create from domain object instance.
     * 
     * @param value The instance to wrap.
     */
    public CustomItem(Type value) {
	this.value = new PerTxBox<Type>(value);
	this.type = (Class<? extends Type>) value.getClass();
    }

    /**
     * Create from domain object type.
     * 
     * @param type The type of domain object.
     */
    public CustomItem(Class<? extends Type> type) {
	this.value = new PerTxBox<Type>(null);
	this.type = type;
    }

    /**
     * Create a new, empty instance of the domain object
     * 
     * @return An instance of domain object.
     */
    protected abstract Type createNewValue();

    /**
     * @see com.vaadin.data.Item#getItemProperty(java.lang.Object)
     */
    @Override
    public Property getItemProperty(Object id) {
	return properties.get(id);
    }

    /**
     * @see com.vaadin.data.Item#getItemPropertyIds()
     */
    @Override
    public Collection<?> getItemPropertyIds() {
	return Collections.unmodifiableCollection(properties.keySet());
    }

    /**
     * @see com.vaadin.data.Item#addItemProperty(java.lang.Object,
     *      com.vaadin.data.Property)
     */
    @Override
    public boolean addItemProperty(Object id, Property property) throws UnsupportedOperationException {
	if (id == null) {
	    throw new NullPointerException("Item property id can not be null");
	}
	if (properties.containsKey(id)) {
	    return false;
	}
	properties.put(id, property);
	fireItemPropertySetChange();
	return true;
    }

    /**
     * @see com.vaadin.data.Item#removeItemProperty(java.lang.Object)
     */
    @Override
    public boolean removeItemProperty(Object id) throws UnsupportedOperationException {
	if (properties.remove(id) == null) {
	    return false;
	}
	fireItemPropertySetChange();
	return true;
    }

    /**
     * @see com.vaadin.data.Property#getValue()
     */
    @Override
    public Type getValue() {
	return value.get();
    }

    /**
     * Access for the domain object instance, creating one if unexisting.
     * 
     * @return Domain object instance.
     */
    public Type getOrCreateValue() {
	if (value.get() == null) {
	    value.put(createNewValue());
	}
	return getValue();
    }

    /**
     * @see com.vaadin.data.Property#setValue(java.lang.Object)
     */
    @Override
    public void setValue(Object newValue) throws ReadOnlyException, ConversionException {
	if (isReadOnly()) {
	    throw new ReadOnlyException();
	}
	if (type.isAssignableFrom(newValue.getClass())) {
	    value.put((Type) newValue);
	} else {
	    throw new ConversionException();
	}
    }

    /**
     * @see com.vaadin.data.Property#getType()
     */
    @Override
    public Class<? extends Type> getType() {
	return type;
    }
}
