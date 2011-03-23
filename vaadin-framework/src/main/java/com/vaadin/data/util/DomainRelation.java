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
import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

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
	implements Container, Container.ItemSetChangeNotifier, Container.PropertySetChangeNotifier {
    private final Collection<String> propertyIds;

    private List<ItemSetChangeListener> itemSetChangeListeners;

    private List<PropertySetChangeListener> propertySetChangeListeners;

    /**
     * Maps all domain objects (item ids) in the container (including filtered)
     * to their corresponding {@link DomainItem}.
     */
    private final Map<Type, DomainItem<Type>> items = new HashMap<Type, DomainItem<Type>>();

    private final Map<UUID, DomainItem<Type>> newItems = new HashMap<UUID, DomainItem<Type>>();

    public DomainRelation(DomainItem<Host> item, PropertyDescriptor descriptor) {
	super(item, descriptor);
	if (!Collection.class.isAssignableFrom(descriptor.getPropertyType())) {
	    throw new IllegalArgumentException("DomainRelation must be bound to relation properties");
	}
	propertyIds = MetaModel.findMetaModelForType(descriptor.getCollectionElementType()).getPropertyIds();
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
	DomainItem<Type> item = new DomainItem<Type>((Class<? extends Type>) getElementType());
	item.addListener(new InstanceCreationListener() {
	    @Override
	    public void itemCreation(InstanceCreationEvent event) {
		fireContainerItemSetChange();
		event.getDomainItem().removeListener(this);
	    }
	});
	UUID id = UUID.randomUUID();
	newItems.put(id, item);
	return id;
    }

    /**
     * @see com.vaadin.data.Container#addItem(java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked")
    public Item addItem(Object itemId) throws UnsupportedOperationException {
	Collection<Type> ids = getValue();
	if (ids.contains(itemId)) {
	    return null;
	}
	DomainItem<Type> item = null;
	for (Entry<UUID, DomainItem<Type>> entry : newItems.entrySet()) {
	    if (entry.getValue().getInstance() != null && entry.getValue().getInstance().equals(itemId)) {
		item = entry.getValue();
	    }
	}
	if (item == null) {
	    item = new DomainItem<Type>((Type) itemId);
	}
	items.put((Type) itemId, item);
	Set<Type> newValue = new HashSet<Type>(getValue());
	newValue.add((Type) itemId);
	setValue(newValue);
	fireContainerItemSetChange();
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
	if (!containsId(itemId)) {
	    return false;
	} else {
	    items.remove(itemId);
	    Set<Type> newValue = new HashSet<Type>(getValue());
	    newValue.remove(itemId);
	    setValue(newValue);
	    fireContainerItemSetChange();
	    return true;
	}
    }

    /**
     * @see com.vaadin.data.Container#removeAllItems()
     */
    @Override
    public boolean removeAllItems() throws UnsupportedOperationException {
	items.clear();
	setValue(Collections.emptySet());
	fireContainerItemSetChange();
	return true;
    }

    /**
     * @see com.vaadin.data.Container#getItemIds()
     */
    @Override
    public Collection<Type> getItemIds() {
	return Collections.unmodifiableCollection(getValue());
    }

    /**
     * @see com.vaadin.data.Container#containsId(java.lang.Object)
     */
    @Override
    public boolean containsId(Object itemId) {
	if (itemId == null) {
	    return false;
	}
	return getItemIds().contains(itemId);
    }

    /**
     * @see com.vaadin.data.Container#getItem(java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked")
    public Item getItem(Object itemId) {
	if (newItems.containsKey(itemId)) {
	    return newItems.get(itemId);
	}
	if (containsId(itemId)) {
	    if (!items.containsKey(itemId)) {
		items.put((Type) itemId, new DomainItem<Type>((Type) itemId));
	    }
	    return items.get(itemId);
	}
	return null;
    }

    /**
     * @see com.vaadin.data.Container#size()
     */
    @Override
    public int size() {
	return getValue().size();
    }

    public Class<? extends AbstractDomainObject> getElementType() {
	return descriptor.getCollectionElementType();
    }

    /**
     * @see com.vaadin.data.Container#getType(java.lang.Object)
     */
    @Override
    public Class<?> getType(Object propertyId) {
	return MetaModel.findMetaModelForType(descriptor.getCollectionElementType()).getPropertyDescriptor(propertyId)
		.getPropertyType();
    }

    @SuppressWarnings("serial")
    private class ItemSetChangeEvent extends EventObject implements Container.ItemSetChangeEvent {
	private ItemSetChangeEvent(DomainRelation<Host, Type> source) {
	    super(source);
	}

	@SuppressWarnings("unchecked")
	public DomainRelation<Host, Type> getContainer() {
	    return (DomainRelation<Host, Type>) getSource();
	}
    }

    /**
     * @see com.vaadin.data.Container.ItemSetChangeNotifier#addListener(com.vaadin
     *      .data.Container.ItemSetChangeListener)
     */
    @Override
    public void addListener(ItemSetChangeListener listener) {
	if (itemSetChangeListeners == null) {
	    itemSetChangeListeners = new LinkedList<ItemSetChangeListener>();
	}
	itemSetChangeListeners.add(listener);
    }

    /**
     * @see com.vaadin.data.Container.ItemSetChangeNotifier#removeListener(com.vaadin
     *      .data.Container.ItemSetChangeListener)
     */
    @Override
    public void removeListener(ItemSetChangeListener listener) {
	if (itemSetChangeListeners != null) {
	    itemSetChangeListeners.remove(listener);
	}
    }

    protected void fireContainerItemSetChange() {
	if (itemSetChangeListeners != null) {
	    final ItemSetChangeListener[] l = itemSetChangeListeners.toArray(new ItemSetChangeListener[0]);
	    final Container.ItemSetChangeEvent event = new ItemSetChangeEvent(this);
	    for (int i = 0; i < l.length; i++) {
		l[i].containerItemSetChange(event);
	    }
	}
    }

    @SuppressWarnings("serial")
    private class PropertySetChangeEvent extends EventObject implements Container.PropertySetChangeEvent {
	private PropertySetChangeEvent(DomainRelation<Host, Type> source) {
	    super(source);
	}

	@SuppressWarnings("unchecked")
	public DomainRelation<Host, Type> getContainer() {
	    return (DomainRelation<Host, Type>) getSource();
	}
    }

    /**
     * @see com.vaadin.data.Container.PropertySetChangeNotifier#addListener(com.vaadin.data.Container.PropertySetChangeListener)
     */
    @Override
    public void addListener(PropertySetChangeListener listener) {
	if (propertySetChangeListeners == null) {
	    propertySetChangeListeners = new LinkedList<PropertySetChangeListener>();
	}
	propertySetChangeListeners.add(listener);
    }

    /**
     * @see com.vaadin.data.Container.PropertySetChangeNotifier#removeListener(com.vaadin.data.Container.PropertySetChangeListener)
     */
    @Override
    public void removeListener(PropertySetChangeListener listener) {
	if (propertySetChangeListeners != null) {
	    propertySetChangeListeners.remove(listener);
	}
    }

    protected void fireContainerPropertySetChange() {
	if (propertySetChangeListeners != null) {
	    final PropertySetChangeListener[] l = propertySetChangeListeners.toArray(new PropertySetChangeListener[0]);
	    final Container.PropertySetChangeEvent event = new PropertySetChangeEvent(this);
	    for (int i = 0; i < l.length; i++) {
		l[i].containerPropertySetChange(event);
	    }
	}
    }
}
