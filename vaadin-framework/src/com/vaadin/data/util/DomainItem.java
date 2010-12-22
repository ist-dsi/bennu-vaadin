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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import jvstm.PerTxBox;
import pt.ist.fenixframework.pstm.AbstractDomainObject;

import com.vaadin.data.Buffered;
import com.vaadin.data.Property;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.metamodel.MetaModel;
import com.vaadin.data.util.metamodel.PropertyDescriptor;

/**
 * @author Pedro Santos (pedro.miguel.santos@ist.utl.pt)
 */
@SuppressWarnings("serial")
public class DomainItem<Type extends AbstractDomainObject> extends BufferedNotifierItem {
    private final PerTxBox<Type> instance;

    private final Class<Type> type;

    private final Collection<String> propertyIds;

    private final Map<Object, DomainProperty<Type>> properties = new HashMap<Object, DomainProperty<Type>>();

    public DomainItem(Class<Type> type) {
	this.type = type;
	this.instance = new PerTxBox<Type>(null);
	propertyIds = MetaModel.findMetaModelForType(type).getPropertyIds();
    }

    @SuppressWarnings("unchecked")
    public DomainItem(Type instance) {
	this.type = (Class<Type>) instance.getClass();
	this.instance = new PerTxBox<Type>(instance);
	propertyIds = MetaModel.findMetaModelForType(type).getPropertyIds();
    }

    public Class<? extends Type> getType() {
	return type;
    }

    /* Instance management */

    protected Type getInstance() {
	return instance.get();
    }

    protected Type getOrCreateInstance() throws SourceException {
	if (instance.get() == null) {
	    try {
		instance.put(type.newInstance());
	    } catch (InstantiationException e) {
		throw new SourceException(this, e);
	    } catch (IllegalAccessException e) {
		throw new SourceException(this, e);
	    }
	}
	return instance.get();
    }

    /* Proxy interfaces implementation */

    /**
     * @see com.vaadin.data.Item#getItemProperty(java.lang.Object)
     */
    @Override
    public DomainProperty<Type> getItemProperty(Object id) {
	if (!properties.containsKey(id)) {
	    MetaModel model = MetaModel.findMetaModelForType(getType());
	    PropertyDescriptor descriptor = model.getPropertyDescriptor(id);
	    if (descriptor != null) {
		if (descriptor.isCollection()) {
		    properties.put(id, new DomainRelation<Type, AbstractDomainObject>(this, descriptor));
		} else {
		    properties.put(id, new DomainProperty<Type>(this, descriptor));
		}
	    }
	}
	return properties.get(id);
    }

    /**
     * @see com.vaadin.data.Item#getItemPropertyIds()
     */
    @Override
    public Collection<?> getItemPropertyIds() {
	return Collections.unmodifiableCollection(propertyIds);
    }

    /**
     * @see com.vaadin.data.Item#addItemProperty(java.lang.Object,
     *      com.vaadin.data.Property)
     */
    @Override
    public boolean addItemProperty(Object id, Property property) throws UnsupportedOperationException {
	throw new UnsupportedOperationException();
    }

    /**
     * @see com.vaadin.data.Item#removeItemProperty(java.lang.Object)
     */
    @Override
    public boolean removeItemProperty(Object id) throws UnsupportedOperationException {
	if (properties.remove(id) != null) {
	    fireItemPropertySetChange();
	    return true;
	}
	return false;
    }

    /**
     * @see com.vaadin.data.Buffered#commit()
     */
    @Override
    public void commit() throws SourceException, InvalidValueException {
	for (Property property : properties.values()) {
	    if (property instanceof Buffered) {
		((Buffered) property).commit();
	    }
	}
    }

    /**
     * @see com.vaadin.data.Buffered#discard()
     */
    @Override
    public void discard() throws SourceException {
	for (Property property : properties.values()) {
	    if (property instanceof Buffered) {
		((Buffered) property).discard();
	    }
	}
    }

    /**
     * @see com.vaadin.data.Buffered#isModified()
     */
    @Override
    public boolean isModified() {
	for (Property property : properties.values()) {
	    if (property instanceof Buffered) {
		if (((Buffered) property).isModified()) {
		    return true;
		}
	    }
	}
	return false;
    }

}
