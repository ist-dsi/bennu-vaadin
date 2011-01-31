///*
// * Copyright 2010 Instituto Superior Tecnico
// * 
// *      https://fenix-ashes.ist.utl.pt/
// * 
// *   This file is part of the vaadin-framework.
// *
// *   The vaadin-framework Infrastructure is free software: you can 
// *   redistribute it and/or modify it under the terms of the GNU Lesser General 
// *   Public License as published by the Free Software Foundation, either version 
// *   3 of the License, or (at your option) any later version.*
// *
// *   vaadin-framework is distributed in the hope that it will be useful,
// *   but WITHOUT ANY WARRANTY; without even the implied warranty of
// *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// *   GNU Lesser General Public License for more details.
// *
// *   You should have received a copy of the GNU Lesser General Public License
// *   along with vaadin-framework. If not, see <http://www.gnu.org/licenses/>.
// * 
// */
//package com.vaadin.data.util;
//
//import java.util.Collection;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Iterator;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import jvstm.PerTxBox;
//import pt.ist.fenixframework.pstm.AbstractDomainObject;
//import pt.ist.fenixframework.pstm.RelationList;
//
//import com.vaadin.data.Container.Filterable;
//import com.vaadin.data.Container.Indexed;
//import com.vaadin.data.Container.Sortable;
//import com.vaadin.data.Property;
//import com.vaadin.data.Property.ValueChangeEvent;
//import com.vaadin.data.Property.ValueChangeListener;
//import com.vaadin.data.Property.ValueChangeNotifier;
//import com.vaadin.data.Validator.InvalidValueException;
//import com.vaadin.data.util.metamodel.PropertyDescriptor;
//
///**
// * @author Pedro Santos (pedro.miguel.santos@ist.utl.pt)
// * 
// */
//public class DomainContainer<Host extends AbstractDomainObject, Type extends AbstractDomainObject> extends
//	BufferedNotifierContainer implements Indexed, Sortable, Filterable, ValueChangeListener {
//    private final PerTxBox<Host> host;
//
//    private final RelationList<Host, Type> relation;
//
//    /**
//     * The type of the host domain object
//     */
//    private final Class<? extends Host> hostType;
//
//    /**
//     * The type of the beans in the container.
//     */
//    private final Class<? extends Type> type;
//
//    /**
//     * Maps all domain objects (item ids) in the container (including filtered)
//     * to their corresponding {@link DomainItem}.
//     */
//    private final Map<Type, DomainItem<Type>> items = new HashMap<Type, DomainItem<Type>>();
//
//    /**
//     * The filteredItems variable contains the items that are visible outside
//     * the container. If filters are enabled this contains a subset of allItems,
//     * if no filters are set this contains the same items as allItems.
//     */
//    private ListSet<Type> filteredItems = new ListSet<Type>();
//
//    /**
//     * The allItems variable always contains all the items in the container.
//     * Some or all of these are also in the filteredItems list.
//     */
//    private final ListSet<Type> allItems = new ListSet<Type>();
//
//    /**
//     * A description of the properties found in objects of type {@link #type}.
//     * Determines the property ids that are present in the container.
//     */
//    private transient Map<String, PropertyDescriptor> model;
//
//    /**
//     * Filters currently applied to the container.
//     */
//    private Set<Filter> filters = new HashSet<Filter>();
//
//    /**
//     * The item sorter which is used for sorting the container.
//     */
//    private ItemSorter itemSorter = new DefaultItemSorter();
//
//    public DomainContainer(DomainProperty<Host> collection, Class<? extends Type> type) {
//	if (Collection.class.isAssignableFrom(collection.getType())) {
//	    throw new IllegalArgumentException("the property passed to DomainContainer must be a Collection");
//	}
//
//    }
//
//    @SuppressWarnings("unchecked")
//    public DomainContainer(Host host, RelationList<Host, Type> relation, Class<? extends Type> type) {
//	if (host == null) {
//	    throw new IllegalArgumentException("The host passed to DomainContainer must not be null");
//	}
//	if (relation == null) {
//	    throw new IllegalArgumentException("The relation passed to DomainContainer must not be null");
//	}
//	this.host = new PerTxBox<Host>(host);
//	this.relation = relation;
//	this.hostType = (Class<? extends Host>) host.getClass();
//	this.type = type;
//	this.model = BeanItem.getPropertyDescriptors(type);
//	int index = 0;
//	for (Type element : relation) {
//	    if (internalAddAt(index, element) != null) {
//		index++;
//	    }
//	}
//	filterAll();
//    }
//
//    /**
//     * @see com.vaadin.data.Container#addItem()
//     */
//    @Override
//    public Object addItem() throws UnsupportedOperationException {
//	throw new UnsupportedOperationException();
//    }
//
//    /**
//     * @see com.vaadin.data.Container#addItem(java.lang.Object)
//     */
//    @Override
//    @SuppressWarnings("unchecked")
//    public DomainItem<Type> addItem(Object itemId) throws UnsupportedOperationException {
//	if (size() > 0) {
//	    // add immediately after last visible item
//	    int lastIndex = allItems.indexOf(lastItemId());
//	    return addItemAtInternalIndex(lastIndex + 1, itemId);
//	} else {
//	    return addItemAtInternalIndex(0, itemId);
//	}
//    }
//
//    /**
//     * Unsupported operation, use {@link #addItemAfter(Object, Object)}.
//     * 
//     * @see com.vaadin.data.Container.Ordered#addItemAfter(java.lang.Object)
//     */
//    @Override
//    public Object addItemAfter(Object previousItemId) throws UnsupportedOperationException {
//	throw new UnsupportedOperationException();
//    }
//
//    /**
//     * @see com.vaadin.data.Container.Ordered#addItemAfter(java.lang.Object,
//     *      java.lang.Object)
//     */
//    @Override
//    public DomainItem<Type> addItemAfter(Object previousItemId, Object newItemId) throws UnsupportedOperationException {
//	// only add if the previous item is visible
//	if (previousItemId == null) {
//	    return addItemAtInternalIndex(0, newItemId);
//	} else if (containsId(previousItemId)) {
//	    return addItemAtInternalIndex(allItems.indexOf(previousItemId) + 1, newItemId);
//	} else {
//	    return null;
//	}
//    }
//
//    /**
//     * Unsupported operation, use {@link #addItemAt(int, Object)}.
//     * 
//     * @see com.vaadin.data.Container.Indexed#addItemAt(int)
//     */
//    @Override
//    public Object addItemAt(int index) throws UnsupportedOperationException {
//	throw new UnsupportedOperationException();
//    }
//
//    /**
//     * @see com.vaadin.data.Container.Indexed#addItemAt(int, java.lang.Object)
//     */
//    @Override
//    public DomainItem<Type> addItemAt(int index, Object newItemId) throws UnsupportedOperationException {
//	if (index < 0 || index > size()) {
//	    return null;
//	} else if (index == 0) {
//	    // add before any item, visible or not
//	    return addItemAtInternalIndex(0, newItemId);
//	} else {
//	    // if index==size(), adds immediately after last visible item
//	    return addItemAfter(getIdByIndex(index - 1), newItemId);
//	}
//    }
//
//    /**
//     * Adds a domain object at the given index of the internal (unfiltered)
//     * list.
//     * <p>
//     * The item is also added in the visible part of the list if it passes the
//     * filters.
//     * </p>
//     * 
//     * @param index Internal index to add the new item.
//     * @param newItemId Id of the new item to be added.
//     * @return Returns new item or null if the operation fails.
//     */
//    private DomainItem<Type> addItemAtInternalIndex(int index, Object newItemId) {
//	DomainItem<Type> item = internalAddAt(index, (Type) newItemId);
//	if (item != null) {
//	    filterAll();
//	}
//	return item;
//    }
//
//    /**
//     * Adds the domain object to all internal data structures at the given
//     * position. Fails if the object is already in the container or is not
//     * assignable to the correct type. Returns a new DomainItem if the object
//     * was added successfully.
//     * 
//     * <p>
//     * Caller should call {@link #filterAll()} after calling this method to
//     * ensure the filtered list is updated.
//     * </p>
//     * 
//     * @param position The position at which the object should be inserted
//     * @param object The object to insert
//     * @return true if the object was added successfully, false otherwise
//     */
//    private DomainItem<Type> internalAddAt(int position, Type object) {
//	// Make sure that the item has not been added previously
//	if (allItems.contains(object)) {
//	    return null;
//	}
//
//	if (!type.isAssignableFrom(object.getClass())) {
//	    return null;
//	}
//
//	// "filteredList" will be updated in filterAll() which should be invoked
//	// by the caller after calling this method.
//	allItems.add(position, object);
//	DomainItem<Type> item = new DomainItem<Type>(object, model);
//	items.put(object, item);
//
//	// add listeners to be able to update filtering on property
//	// changes
//	for (Filter filter : filters) {
//	    // addValueChangeListener avoids adding duplicates
//	    addValueChangeListener(item, filter.propertyId);
//	}
//
//	return item;
//    }
//
//    /**
//     * @see com.vaadin.data.Container#getItem(java.lang.Object)
//     */
//    @Override
//    public DomainItem<Type> getItem(Object itemId) {
//	return items.get(itemId);
//    }
//
//    /**
//     * @see com.vaadin.data.Container#getContainerPropertyIds()
//     */
//    @Override
//    public Collection<String> getContainerPropertyIds() {
//	return Collections.unmodifiableCollection(model.keySet());
//    }
//
//    /**
//     * @see com.vaadin.data.Container#getItemIds()
//     */
//    @Override
//    public Collection<Type> getItemIds() {
//	return Collections.unmodifiableCollection(filteredItems);
//    }
//
//    /**
//     * @see com.vaadin.data.Container#getContainerProperty(java.lang.Object,
//     *      java.lang.Object)
//     */
//    @Override
//    public DomainProperty<Type> getContainerProperty(Object itemId, Object propertyId) {
//	return getItem(itemId).getItemProperty(propertyId);
//    }
//
//    /**
//     * @see com.vaadin.data.Container#getType(java.lang.Object)
//     */
//    @Override
//    public Class<?> getType(Object propertyId) {
//	return model.get(propertyId).getPropertyType();
//    }
//
//    /**
//     * @see com.vaadin.data.Container#size()
//     */
//    @Override
//    public int size() {
//	return filteredItems.size();
//    }
//
//    /**
//     * @see com.vaadin.data.Container#containsId(java.lang.Object)
//     */
//    @Override
//    public boolean containsId(Object itemId) {
//	return filteredItems.contains(itemId);
//    }
//
//    /**
//     * @see com.vaadin.data.Container#removeItem(java.lang.Object)
//     */
//    @Override
//    public boolean removeItem(Object itemId) throws UnsupportedOperationException {
//	if (!allItems.remove(itemId)) {
//	    return false;
//	}
//	// detach listeners from Item
//	removeAllValueChangeListeners(getItem(itemId));
//	// remove item
//	items.remove(itemId);
//	filteredItems.remove(itemId);
//	fireContainerItemSetChange();
//	return true;
//    }
//
//    /**
//     * @see com.vaadin.data.Container#addContainerProperty(java.lang.Object,
//     *      java.lang.Class, java.lang.Object)
//     */
//    @Override
//    public boolean addContainerProperty(Object propertyId, Class<?> type, Object defaultValue)
//	    throws UnsupportedOperationException {
//	throw new UnsupportedOperationException();
//    }
//
//    /**
//     * @see com.vaadin.data.Container#removeContainerProperty(java.lang.Object)
//     */
//    @Override
//    public boolean removeContainerProperty(Object propertyId) throws UnsupportedOperationException {
//	throw new UnsupportedOperationException();
//    }
//
//    /**
//     * @see com.vaadin.data.Container#removeAllItems()
//     */
//    @Override
//    public boolean removeAllItems() throws UnsupportedOperationException {
//	allItems.clear();
//	filteredItems.clear();
//	// detach listeners from all BeanItems
//	for (DomainItem<Type> item : items.values()) {
//	    removeAllValueChangeListeners(item);
//	}
//	items.clear();
//	fireContainerItemSetChange();
//	return true;
//    }
//
//    /*
//     * (non-Javadoc)
//     * 
//     * @see com.vaadin.data.Buffered#commit()
//     */
//    @Override
//    public void commit() throws SourceException, InvalidValueException {
//	// TODO Auto-generated method stub
//
//    }
//
//    /*
//     * (non-Javadoc)
//     * 
//     * @see com.vaadin.data.Buffered#discard()
//     */
//    @Override
//    public void discard() throws SourceException {
//	// TODO Auto-generated method stub
//
//    }
//
//    /*
//     * (non-Javadoc)
//     * 
//     * @see com.vaadin.data.Buffered#isModified()
//     */
//    @Override
//    public boolean isModified() {
//	// TODO Auto-generated method stub
//	return false;
//    }
//
//    /**
//     * @see com.vaadin.data.Container.Ordered#nextItemId(java.lang.Object)
//     */
//    @Override
//    public Type nextItemId(Object itemId) {
//	int index = indexOfId(itemId);
//	if (index >= 0 && index < size() - 1) {
//	    return getIdByIndex(index + 1);
//	} else {
//	    // out of bounds
//	    return null;
//	}
//    }
//
//    /**
//     * @see com.vaadin.data.Container.Ordered#prevItemId(java.lang.Object)
//     */
//    @Override
//    public Type prevItemId(Object itemId) {
//	int index = indexOfId(itemId);
//	if (index > 0) {
//	    return getIdByIndex(index - 1);
//	} else {
//	    // out of bounds
//	    return null;
//	}
//    }
//
//    /**
//     * @see com.vaadin.data.Container.Ordered#firstItemId()
//     */
//    @Override
//    public Type firstItemId() {
//	if (size() > 0) {
//	    return getIdByIndex(0);
//	} else {
//	    return null;
//	}
//    }
//
//    /**
//     * @see com.vaadin.data.Container.Ordered#lastItemId()
//     */
//    @Override
//    public Type lastItemId() {
//	if (size() > 0) {
//	    return getIdByIndex(size() - 1);
//	} else {
//	    return null;
//	}
//    }
//
//    /**
//     * @see com.vaadin.data.Container.Ordered#isFirstId(java.lang.Object)
//     */
//    @Override
//    public boolean isFirstId(Object itemId) {
//	return firstItemId() == itemId;
//    }
//
//    /**
//     * @see com.vaadin.data.Container.Ordered#isLastId(java.lang.Object)
//     */
//    @Override
//    public boolean isLastId(Object itemId) {
//	return lastItemId() == itemId;
//    }
//
//    /**
//     * @see com.vaadin.data.Container.Filterable#addContainerFilter(java.lang.Object,
//     *      java.lang.String, boolean, boolean)
//     */
//    @Override
//    public void addContainerFilter(Object propertyId, String filterString, boolean ignoreCase, boolean onlyMatchPrefix) {
//	if (filters.isEmpty()) {
//	    filteredItems = (ListSet<Type>) allItems.clone();
//	}
//	// listen to change events to be able to update filtering
//	for (DomainItem<Type> item : items.values()) {
//	    addValueChangeListener(item, propertyId);
//	}
//	Filter f = new Filter(propertyId, filterString, ignoreCase, onlyMatchPrefix);
//	filter(f);
//	filters.add(f);
//	fireContainerItemSetChange();
//    }
//
//    /**
//     * Filter the view to recreate the visible item list from the unfiltered
//     * items, and send a notification if the set of visible items changed in any
//     * way.
//     */
//    protected void filterAll() {
//	// avoid notification if the filtering had no effect
//	List<Type> originalItems = filteredItems;
//	// it is somewhat inefficient to do a (shallow) clone() every time
//	filteredItems = (ListSet<Type>) allItems.clone();
//	for (Filter f : filters) {
//	    filter(f);
//	}
//	// check if exactly the same items are there after filtering to avoid
//	// unnecessary notifications
//	// this may be slow in some cases as it uses BT.equals()
//	if (!originalItems.equals(filteredItems)) {
//	    fireContainerItemSetChange();
//	}
//    }
//
//    /**
//     * Remove (from the filtered list) any items that do not match the given
//     * filter.
//     * 
//     * @param f The filter used to determine if items should be removed
//     */
//    protected void filter(Filter f) {
//	Iterator<Type> iterator = filteredItems.iterator();
//	while (iterator.hasNext()) {
//	    Type object = iterator.next();
//	    if (!f.passesFilter(getItem(object))) {
//		iterator.remove();
//	    }
//	}
//    }
//
//    /**
//     * @see com.vaadin.data.Container.Filterable#removeAllContainerFilters()
//     */
//    @Override
//    public void removeAllContainerFilters() {
//	if (!filters.isEmpty()) {
//	    filters = new HashSet<Filter>();
//	    // stop listening to change events for any property
//	    for (DomainItem<Type> item : items.values()) {
//		removeAllValueChangeListeners(item);
//	    }
//	    filterAll();
//	}
//    }
//
//    /**
//     * @see com.vaadin.data.Container.Filterable#removeContainerFilters(java.lang
//     *      .Object)
//     */
//    @Override
//    public void removeContainerFilters(Object propertyId) {
//	if (!filters.isEmpty()) {
//	    for (Iterator<Filter> iterator = filters.iterator(); iterator.hasNext();) {
//		Filter f = iterator.next();
//		if (f.propertyId.equals(propertyId)) {
//		    iterator.remove();
//		}
//	    }
//	    // stop listening to change events for the property
//	    for (DomainItem<Type> item : items.values()) {
//		removeValueChangeListener(item, propertyId);
//	    }
//	    filterAll();
//	}
//    }
//
//    /**
//     * @see com.vaadin.data.Container.Sortable#sort(java.lang.Object[],
//     *      boolean[])
//     */
//    @Override
//    public void sort(Object[] propertyId, boolean[] ascending) {
//	itemSorter.setSortProperties(this, propertyId, ascending);
//	Collections.sort(allItems, getItemSorter());
//	// notifies if anything changes in the filtered list, including order
//	filterAll();
//    }
//
//    public ItemSorter getItemSorter() {
//	return itemSorter;
//    }
//
//    /**
//     * Sets the ItemSorter that is used for sorting the container. The
//     * {@link ItemSorter#compare(Object, Object)} method is called to compare
//     * two beans (item ids).
//     * 
//     * @param itemSorter The ItemSorter to use when sorting the container
//     */
//    public void setItemSorter(ItemSorter itemSorter) {
//	this.itemSorter = itemSorter;
//    }
//
//    /**
//     * @see com.vaadin.data.Container.Sortable#getSortableContainerPropertyIds()
//     */
//    @Override
//    public Collection<?> getSortableContainerPropertyIds() {
//	LinkedList<Object> sortables = new LinkedList<Object>();
//	for (Object propertyId : getContainerPropertyIds()) {
//	    Class<?> propertyType = getType(propertyId);
//	    if (Comparable.class.isAssignableFrom(propertyType) || propertyType.isPrimitive()) {
//		sortables.add(propertyId);
//	    }
//	}
//	return sortables;
//    }
//
//    /**
//     * @see com.vaadin.data.Container.Indexed#indexOfId(java.lang.Object)
//     */
//    @Override
//    public int indexOfId(Object itemId) {
//	return filteredItems.indexOf(itemId);
//    }
//
//    /**
//     * @see com.vaadin.data.Container.Indexed#getIdByIndex(int)
//     */
//    @Override
//    public Type getIdByIndex(int index) {
//	return filteredItems.get(index);
//    }
//
//    private void removeAllValueChangeListeners(DomainItem<Type> item) {
//	for (Object propertyId : item.getItemPropertyIds()) {
//	    removeValueChangeListener(item, propertyId);
//	}
//    }
//
//    private void removeValueChangeListener(DomainItem<Type> item, Object propertyId) {
//	Property property = item.getItemProperty(propertyId);
//	if (property instanceof ValueChangeNotifier) {
//	    ((ValueChangeNotifier) property).removeListener(this);
//	}
//    }
//
//    /**
//     * Make this container listen to the given property provided it notifies
//     * when its value changes.
//     * 
//     * @param item The BeanItem that contains the property
//     * @param propertyId The id of the property
//     */
//    private void addValueChangeListener(DomainItem<Type> item, Object propertyId) {
//	Property property = item.getItemProperty(propertyId);
//	if (property instanceof ValueChangeNotifier) {
//	    // avoid multiple notifications for the same property if
//	    // multiple filters are in use
//	    ValueChangeNotifier notifier = (ValueChangeNotifier) property;
//	    notifier.removeListener(this);
//	    notifier.addListener(this);
//	}
//    }
//
//    /**
//     * @see com.vaadin.data.Property.ValueChangeListener#valueChange(com.vaadin.data.Property.ValueChangeEvent)
//     */
//    @Override
//    public void valueChange(ValueChangeEvent event) {
//	filterAll();
//    }
// }
