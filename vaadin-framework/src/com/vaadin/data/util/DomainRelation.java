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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import pt.ist.fenixframework.pstm.AbstractDomainObject;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.metamodel.MetaModel;
import com.vaadin.data.util.metamodel.PropertyDescriptor;

/**
 * @author Pedro Santos (pedro.miguel.santos@ist.utl.pt)
 */
public class DomainRelation<Host extends AbstractDomainObject, Type extends AbstractDomainObject> extends DomainProperty<Host>
	implements Container {
    private final Collection<Object> propertyIds = new ArrayList<Object>();

    /**
     * Maps all domain objects (item ids) in the container (including filtered)
     * to their corresponding {@link DomainItem}.
     */
    private final Map<Type, DomainItem<Type>> items = new HashMap<Type, DomainItem<Type>>();

    public DomainRelation(DomainItem<Host> item, PropertyDescriptor descriptor) {
	super(item, descriptor);
	if (Collection.class.isAssignableFrom(descriptor.getPropertyType())) {
	    throw new IllegalArgumentException("DomainRelation must be bound to relation properties");
	}
	MetaModel model = MetaModel.findMetaModelForType(item.getType());
	for (PropertyDescriptor propertyDescriptor : model.getPropertyDescriptors()) {
	    propertyIds.add(propertyDescriptor.getPropertyId());
	}
	for (Type instance : getValue()) {
	    addItem(instance);
	}
    }

    /**
     * @see com.vaadin.data.Property#getValue()
     */
    @Override
    @SuppressWarnings("unchecked")
    public Collection<Type> getValue() {
	return (Collection<Type>) super.getValue();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Collection<Type> getPersistentValue() throws SourceException {
	return (Collection<Type>) super.getPersistentValue();
    }

    /* Container Properties */

    /**
     * @see com.vaadin.data.Container#addContainerProperty(java.lang.Object,
     *      java.lang.Class, java.lang.Object)
     */
    @Override
    public boolean addContainerProperty(Object propertyId, Class<?> type, Object defaultValue)
	    throws UnsupportedOperationException {
	throw new UnsupportedOperationException();
    }

    /**
     * @see com.vaadin.data.Container#removeContainerProperty(java.lang.Object)
     */
    @Override
    public boolean removeContainerProperty(Object propertyId) throws UnsupportedOperationException {
	throw new UnsupportedOperationException();
    }

    /**
     * @see com.vaadin.data.Container#getContainerPropertyIds()
     */
    @Override
    public Collection<?> getContainerPropertyIds() {
	return Collections.unmodifiableCollection(propertyIds);
    }

    /**
     * @see com.vaadin.data.Container#getContainerProperty(java.lang.Object,
     *      java.lang.Object)
     */
    @Override
    public Property getContainerProperty(Object itemId, Object propertyId) {
	return getItem(itemId).getItemProperty(propertyId);
    }

    /**
     * @see com.vaadin.data.Container#addItem()
     */
    @Override
    public Object addItem() throws UnsupportedOperationException {
	throw new UnsupportedOperationException();
    }

    /**
     * @see com.vaadin.data.Container#addItem(java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked")
    public Item addItem(Object itemId) throws UnsupportedOperationException {
	if (items.containsKey(itemId)) {
	    return null;
	}
	DomainItem<Type> item = new DomainItem<Type>((Type) itemId);
	items.put((Type) itemId, item);
	return item;
    }

    /**
     * @see com.vaadin.data.Container#removeItem(java.lang.Object)
     */
    @Override
    public boolean removeItem(Object itemId) throws UnsupportedOperationException {
	if (itemId == null) {
	    return false;
	}

	if (items.remove(itemId) == null) {
	    return false;
	}
	return true;
    }

    /**
     * @see com.vaadin.data.Container#removeAllItems()
     */
    @Override
    public boolean removeAllItems() throws UnsupportedOperationException {
	items.clear();
	return true;
    }

    /**
     * @see com.vaadin.data.Container#getItemIds()
     */
    @Override
    public Collection<?> getItemIds() {
	return Collections.unmodifiableCollection(items.keySet());
    }

    /**
     * @see com.vaadin.data.Container#containsId(java.lang.Object)
     */
    @Override
    public boolean containsId(Object itemId) {
	if (itemId == null) {
	    return false;
	}
	return items.containsKey(itemId);
    }

    /**
     * @see com.vaadin.data.Container#getItem(java.lang.Object)
     */
    @Override
    public Item getItem(Object itemId) {
	return items.get(itemId);
    }

    /**
     * @see com.vaadin.data.Container#size()
     */
    @Override
    public int size() {
	return items.size();
    }

    /**
     * @see com.vaadin.data.Container#getType(java.lang.Object)
     */
    @Override
    public Class<?> getType(Object propertyId) {
	return MetaModel.findMetaModelForType(item.getType()).getPropertyDescriptor(propertyId).getPropertyType();
    }
}
