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
package com.vaadin.data.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import pt.ist.fenixframework.pstm.AbstractDomainObject;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;

/**
 * @author Pedro Santos (pedro.miguel.santos@ist.utl.pt)
 * 
 */
public abstract class AbstractDomainContainer extends AbstractDomainProperty implements Container.Sortable,
	Container.ItemSetChangeNotifier, Container.PropertySetChangeNotifier {
    private final List<Object> propertyIds = new ArrayList<Object>();

    private final Map<Object, Class<?>> model = new HashMap<Object, Class<?>>();

    private final List<Object> itemIds = new ArrayList<Object>();

    private final Map<Object, AbstractDomainItem> items = new HashMap<Object, AbstractDomainItem>();

    private ItemSorter itemSorter = new DefaultItemSorter();

    private List<PropertySetChangeListener> propertySetChangeListeners;

    private List<ItemSetChangeListener> itemSetChangeListeners;

    /**
     * Create from domain object instance.
     * 
     * @param value The instance to wrap.
     */
    public AbstractDomainContainer(AbstractDomainObject value) {
	super(value);
    }

    public AbstractDomainContainer(Set<? extends AbstractDomainObject> valueSet) {
	super(valueSet);
    }

    /**
     * Create from domain object type.
     * 
     * @param type The type of domain object.
     */
    public AbstractDomainContainer(Class<? extends AbstractDomainObject> type) {
	super(type);
    }

    public AbstractDomainContainer(AbstractDomainItem host, Class<? extends AbstractDomainObject> type) {
	super(host, type);
    }

    /**
     * @see com.vaadin.data.util.AbstractDomainProperty#setValue(java.lang.Object)
     */
    @Override
    public void setValue(Object newValue) throws ReadOnlyException, ConversionException {
	super.setValue(newValue);
    }

    /**
     * @see com.vaadin.data.util.AbstractDomainProperty#getValue()
     */
    @Override
    public Collection<AbstractDomainObject> getValue() {
	return (Collection<AbstractDomainObject>) super.getValue();
    }

    /**
     * @see com.vaadin.data.Container#getItem(java.lang.Object)
     */
    @Override
    public Item getItem(Object itemId) {
	if (!items.containsKey(itemId)) {
	    AbstractDomainItem item = lazyCreateItem((AbstractDomainObject) itemId);
	    if (item != null) {
		itemIds.add(itemId);
		items.put(itemId, item);
	    }
	}
	return items.get(itemId);
    }

    protected abstract AbstractDomainItem lazyCreateItem(AbstractDomainObject itemId);

    /**
     * @see com.vaadin.data.Container#getContainerPropertyIds()
     */
    @Override
    public Collection<?> getContainerPropertyIds() {
	return Collections.unmodifiableCollection(propertyIds);
    }

    /**
     * @see com.vaadin.data.Container#getItemIds()
     */
    @Override
    public Collection<?> getItemIds() {
	if (itemIds.isEmpty()) {
	    for (Object itemId : getValue()) {
		getItem(itemId);
	    }
	}
	return Collections.unmodifiableCollection(getValue());
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
     * @see com.vaadin.data.Container#getType(java.lang.Object)
     */
    @Override
    public Class<?> getType(Object propertyId) {
	return model.get(propertyId);
    }

    /**
     * @see com.vaadin.data.Container#size()
     */
    @Override
    public int size() {
	return getItemIds().size();
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
     * @see com.vaadin.data.Container#addItem(java.lang.Object)
     */
    @Override
    public Item addItem(Object itemId) throws UnsupportedOperationException {
	if (itemId == null) {
	    return null;
	}
	if (containsId(itemId)) {
	    return null;
	}
	getValue().add((AbstractDomainObject) itemId);
	Item item = getItem(itemId);
	fireContainerItemSetChange();
	return item;
    }

    /**
     * @see com.vaadin.data.Container#addItem()
     */
    @Override
    public Object addItem() throws UnsupportedOperationException {
	throw new UnsupportedOperationException();
    }

    public Item addItem(Class<? extends AbstractDomainObject> type) {
	if (type == null) {
	    return null;
	}
	AbstractDomainItem item = createItemForType(type);
	item.addListener(new InstanceCreationListener() {
	    @Override
	    public void itemCreation(InstanceCreationEvent event) {
		getOrCreateHost();
		final AbstractDomainItem domainItem = event.getDomainItem();
		final AbstractDomainObject itemId = domainItem.getValue();
		itemIds.add(itemId);
		items.put(itemId, domainItem);
		getValue().add(itemId);
		// this is needed because sometimes the creation already links
		// the object, and the container then thinks the object was
		// already there and doesn't fire the event.
		fireContainerItemSetChange();
		domainItem.removeListener(this);
	    }
	});
	return item;
    }

    /**
     * @param type
     * @return
     */
    protected abstract AbstractDomainItem createItemForType(Class<? extends AbstractDomainObject> type);

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
	}
	itemIds.remove(itemId);
	items.remove(itemId);
	getValue().remove(itemId);
	fireContainerItemSetChange();
	return true;
    }

    /**
     * @see com.vaadin.data.Container#addContainerProperty(java.lang.Object,
     *      java.lang.Class, java.lang.Object)
     */
    @Override
    public boolean addContainerProperty(Object propertyId, Class<?> type, Object defaultValue)
	    throws UnsupportedOperationException {
	if (propertyId == null || type == null) {
	    return false;
	}
	if (model.containsKey(propertyId)) {
	    return false;
	}
	propertyIds.add(propertyId);
	model.put(propertyId, type);
	fireContainerPropertySetChange();
	return true;
    }

    /**
     * @see com.vaadin.data.Container#removeContainerProperty(java.lang.Object)
     */
    @Override
    public boolean removeContainerProperty(Object propertyId) throws UnsupportedOperationException {
	if (!model.containsKey(propertyId)) {
	    return false;
	}
	propertyIds.remove(propertyId);
	model.remove(propertyId);
	fireContainerPropertySetChange();
	return true;
    }

    /**
     * @see com.vaadin.data.Container#removeAllItems()
     */
    @Override
    public boolean removeAllItems() throws UnsupportedOperationException {
	itemIds.clear();
	items.clear();
//	setValue(Collections.emptySet());
	setValue(new HashSet());
	fireContainerItemSetChange();
	return true;
    }

    /**
     * @see com.vaadin.data.Container.Ordered#nextItemId(java.lang.Object)
     */
    @Override
    public Object nextItemId(Object itemId) {
	try {
	    int idx = itemIds.indexOf(itemId);
	    if (idx == -1) {
		// If the given Item is not found in the Container,
		// null is returned.
		return null;
	    }
	    return itemIds.get(idx + 1);
	} catch (final IndexOutOfBoundsException e) {
	    return null;
	}
    }

    /**
     * @see com.vaadin.data.Container.Ordered#prevItemId(java.lang.Object)
     */
    @Override
    public Object prevItemId(Object itemId) {
	try {
	    return itemIds.get(itemIds.indexOf(itemId) - 1);
	} catch (final IndexOutOfBoundsException e) {
	    return null;
	}
    }

    /**
     * @see com.vaadin.data.Container.Ordered#firstItemId()
     */
    @Override
    public Object firstItemId() {
	try {
	    return itemIds.get(0);
	} catch (final IndexOutOfBoundsException e) {
	} catch (final NoSuchElementException e) {
	}
	return null;
    }

    /**
     * @see com.vaadin.data.Container.Ordered#lastItemId()
     */
    @Override
    public Object lastItemId() {
	try {
	    return itemIds.get(itemIds.size() - 1);
	} catch (final IndexOutOfBoundsException e) {
	}
	return null;
    }

    /**
     * @see com.vaadin.data.Container.Ordered#isFirstId(java.lang.Object)
     */
    @Override
    public boolean isFirstId(Object itemId) {
	return (size() >= 1 && itemIds.get(0).equals(itemId));
    }

    /**
     * @see com.vaadin.data.Container.Ordered#isLastId(java.lang.Object)
     */
    @Override
    public boolean isLastId(Object itemId) {
	final int s = size();
	return (s >= 1 && itemIds.get(s - 1).equals(itemId));
    }

    /**
     * @see com.vaadin.data.Container.Ordered#addItemAfter(java.lang.Object)
     */
    @Override
    public Object addItemAfter(Object previousItemId) throws UnsupportedOperationException {
	throw new UnsupportedOperationException();
    }

    /**
     * @see com.vaadin.data.Container.Ordered#addItemAfter(java.lang.Object,
     *      java.lang.Object)
     */
    @Override
    public Item addItemAfter(Object previousItemId, Object newItemId) throws UnsupportedOperationException {
	throw new UnsupportedOperationException();
    }

    /**
     * @see com.vaadin.data.Container.Sortable#sort(java.lang.Object[],
     *      boolean[])
     */
    @Override
    public void sort(Object[] propertyId, boolean[] ascending) {
	// Set up the item sorter for the sort operation
	itemSorter.setSortProperties(this, propertyId, ascending);

	// Perform the actual sort
	doSort();

	// Post sort updates
	fireContainerItemSetChange();
    }

    protected void doSort() {
	Collections.sort(itemIds, getItemSorter());
    }

    /**
     * @see com.vaadin.data.Container.Sortable#getSortableContainerPropertyIds()
     */
    @Override
    public Collection<?> getSortableContainerPropertyIds() {
	final List<Object> list = new LinkedList<Object>();
	for (final Iterator<?> i = propertyIds.iterator(); i.hasNext();) {
	    final Object id = i.next();
	    final Class<?> type = getType(id);
	    if (type != null && Comparable.class.isAssignableFrom(type)) {
		list.add(id);
	    }
	}

	return list;
    }

    /**
     * Returns the ItemSorter used for comparing items in a sort. See
     * {@link #setItemSorter(ItemSorter)} for more information.
     * 
     * @return The ItemSorter used for comparing two items in a sort.
     */
    public ItemSorter getItemSorter() {
	return itemSorter;
    }

    /**
     * Sets the ItemSorter used for comparing items in a sort. The ItemSorter is
     * called for each collection that needs sorting. A default ItemSorter is
     * used if this is not explicitly set.
     * 
     * @param itemSorter The ItemSorter used for comparing two items in a sort.
     */
    public void setItemSorter(ItemSorter itemSorter) {
	this.itemSorter = itemSorter;
    }

    private class PropertySetChangeEvent extends EventObject implements Container.PropertySetChangeEvent {
	private PropertySetChangeEvent(AbstractDomainContainer source) {
	    super(source);
	}

	@Override
	public AbstractDomainContainer getContainer() {
	    return (AbstractDomainContainer) getSource();
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

    private class ItemSetChangeEvent extends EventObject implements Container.ItemSetChangeEvent {
	private ItemSetChangeEvent(AbstractDomainContainer source) {
	    super(source);
	}

	@Override
	public AbstractDomainContainer getContainer() {
	    return (AbstractDomainContainer) getSource();
	}
    }

    /**
     * @see com.vaadin.data.Container.ItemSetChangeNotifier#addListener(com.vaadin.data.Container.ItemSetChangeListener)
     */
    @Override
    public void addListener(ItemSetChangeListener listener) {
	if (itemSetChangeListeners == null) {
	    itemSetChangeListeners = new LinkedList<ItemSetChangeListener>();
	}
	itemSetChangeListeners.add(listener);
    }

    /**
     * @see com.vaadin.data.Container.ItemSetChangeNotifier#removeListener(com.vaadin.data.Container.ItemSetChangeListener)
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
}
