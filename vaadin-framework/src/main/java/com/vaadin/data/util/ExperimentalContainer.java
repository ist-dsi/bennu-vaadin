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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;

import pt.ist.fenixframework.pstm.AbstractDomainObject;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;

/**
 * @author Pedro Santos (pedro.miguel.santos@ist.utl.pt)
 */
@SuppressWarnings("serial")
public abstract class ExperimentalContainer<Type extends AbstractDomainObject> extends NotifierContainer implements Container,
	Container.Filterable, Container.Sortable, Container.Indexed, Container.Ordered, Container.PropertySetChangeNotifier,
	Container.ItemSetChangeNotifier {
    /**
     * Hash of Items, where each Item is implemented as a mapping from Property
     * ID to Property value.
     */
    private final Map<Object, Item> items = new HashMap<Object, Item>();

    /**
     * Linked list of ordered Item IDs.
     */
    private final ArrayList<Object> itemIds = new ArrayList<Object>();

    /** List of item ids that passes the filtering */
    private LinkedHashSet<Object> filteredItemIds = null;

    /**
     * Linked list of ordered Property IDs.
     */
    private final ArrayList<Object> propertyIds = new ArrayList<Object>();

    /**
     * Property ID to type mapping.
     */
    private final Hashtable types = new Hashtable();

    /**
     * Set of properties that are read-only.
     */
    private final HashSet readOnlyProperties = new HashSet();

    /**
     * List of all Property value change event listeners listening all the
     * properties.
     */
    private final LinkedList propertyValueChangeListeners = null;

    /**
     * Data structure containing all listeners interested in changes to single
     * Properties. The data structure is a hashtable mapping Property IDs to a
     * hashtable that maps Item IDs to a linked list of listeners listening
     * Property identified by given Property ID and Item ID.
     */
    private final Hashtable singlePropertyValueChangeListeners = null;

    /**
     * List of all Property set change event listeners.
     */
    private final LinkedList propertySetChangeListeners = null;

    /**
     * List of all container Item set change event listeners.
     */
    private final LinkedList itemSetChangeListeners = null;

    /**
     * The item sorter which is used for sorting the container.
     */
    private ItemSorter itemSorter = new DefaultItemSorter();

    /**
     * Filters that are applied to the container to limit the items visible in
     * it
     */
    private HashSet<Filter> filters;

    private HashMap<Object, Object> defaultPropertyValues;

    public ExperimentalContainer() {
    }

    /**
     * @return
     */
    protected abstract Type createNewValue();

    /**
     * @see com.vaadin.data.Container#getItem(java.lang.Object)
     */
    @Override
    public Item getItem(Object itemId) {
	if (itemId != null && items.containsKey(itemId) && (filteredItemIds == null || filteredItemIds.contains(itemId))) {
	    return items.get(itemId);
	}
	return null;
    }

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
	if (filteredItemIds != null) {
	    return Collections.unmodifiableCollection(filteredItemIds);
	}
	return Collections.unmodifiableCollection(itemIds);
    }

    /**
     * @see com.vaadin.data.Container#getContainerProperty(java.lang.Object,
     *      java.lang.Object)
     */
    @Override
    public Property getContainerProperty(Object itemId, Object propertyId) {
	if (itemId == null) {
	    return null;
	} else if (filteredItemIds == null) {
	    if (!items.containsKey(itemId)) {
		return null;
	    }
	} else if (!filteredItemIds.contains(itemId)) {
	    return null;
	}
	return items.get(itemId).getItemProperty(propertyId);
    }

    /**
     * @see com.vaadin.data.Container#getType(java.lang.Object)
     */
    @Override
    public Class<?> getType(Object propertyId) {
	return (Class<?>) types.get(propertyId);
    }

    /**
     * @see com.vaadin.data.Container#size()
     */
    @Override
    public int size() {
	if (filteredItemIds == null) {
	    return itemIds.size();
	}
	return filteredItemIds.size();
    }

    /**
     * @see com.vaadin.data.Container#containsId(java.lang.Object)
     */
    @Override
    public boolean containsId(Object itemId) {
	if (itemId == null) {
	    return false;
	}
	if (filteredItemIds != null) {
	    return filteredItemIds.contains(itemId);
	}
	return items.containsKey(itemId);
    }

    protected abstract Item createItem(Object itemId);

    /**
     * @see com.vaadin.data.Container#addItem(java.lang.Object)
     */
    @Override
    public Item addItem(Object itemId) throws UnsupportedOperationException {

	// Make sure that the Item is valid and has not been created yet
	if (itemId == null || items.containsKey(itemId)) {
	    return null;
	}

	// Adds the Item to container (at the end of the unfiltered list)
	itemIds.add(itemId);
	items.put(itemId, createItem(itemId));

	addDefaultValues(items.get(itemId));

	// this optimization is why some code is duplicated with
	// addItemAtInternalIndex()
	if (filteredItemIds != null) {
	    if (passesFilters(itemId)) {
		filteredItemIds.add(itemId);
	    }
	}

	// Sends the event
	fireContainerItemSetChange();

	return items.get(itemId);
    }

    /**
     * @see com.vaadin.data.Container#addItem()
     */
    @Override
    public Object addItem() throws UnsupportedOperationException {

	// Creates a new id
	final Object id = generateId();

	// Adds the Item into container
	addItem(id);

	return id;
    }

    protected abstract Object generateId();

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
	itemIds.remove(itemId);
	if (filteredItemIds != null) {
	    filteredItemIds.remove(itemId);
	}
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

	// Fails, if nulls are given
	if (propertyId == null || type == null) {
	    return false;
	}

	// Fails if the Property is already present
	if (propertyIds.contains(propertyId)) {
	    return false;
	}

	// Adds the Property to Property list and types
	propertyIds.add(propertyId);
	types.put(propertyId, type);

	// If default value is given, set it
	if (defaultValue != null) {
	    // for existing rows
	    for (final Iterator i = itemIds.iterator(); i.hasNext();) {
		getItem(i.next()).getItemProperty(propertyId).setValue(defaultValue);
	    }
	    // store for next rows
	    if (defaultPropertyValues == null) {
		defaultPropertyValues = new HashMap<Object, Object>();
	    }
	    defaultPropertyValues.put(propertyId, defaultValue);
	}

	// Sends a change event
	fireContainerPropertySetChange();

	return true;
    }

    /**
     * @see com.vaadin.data.Container#removeContainerProperty(java.lang.Object)
     */
    @Override
    public boolean removeContainerProperty(Object propertyId) throws UnsupportedOperationException {

	// Fails if the Property is not present
	if (!propertyIds.contains(propertyId)) {
	    return false;
	}

	// Removes the Property to Property list and types
	propertyIds.remove(propertyId);
	types.remove(propertyId);
	if (defaultPropertyValues != null) {
	    defaultPropertyValues.remove(propertyId);
	}

	// If remove the Property from all Items
	for (final Iterator i = itemIds.iterator(); i.hasNext();) {
	    items.get(i.next()).removeItemProperty(propertyId);
	}

	// Sends a change event
	fireContainerPropertySetChange();

	return true;
    }

    /**
     * @see com.vaadin.data.Container#removeAllItems()
     */
    @Override
    public boolean removeAllItems() throws UnsupportedOperationException {

	// Removes all Items
	itemIds.clear();
	items.clear();
	if (filteredItemIds != null) {
	    filteredItemIds.clear();
	}
	fireContainerItemSetChange();
	return true;
    }

    /**
     * Helper method to add default values for items if available
     * 
     * @param t data table of added item
     */
    private void addDefaultValues(Item item) {
	if (defaultPropertyValues != null) {
	    for (Object key : defaultPropertyValues.keySet()) {
		item.getItemProperty(key).setValue(defaultPropertyValues.get(key));
	    }
	}
    }

    /* Container.Ordered interface implementation */

    /**
     * @see com.vaadin.data.Container.Ordered#nextItemId(java.lang.Object)
     */
    @Override
    public Object nextItemId(Object itemId) {
	if (filteredItemIds != null) {
	    if (itemId == null || !filteredItemIds.contains(itemId)) {
		return null;
	    }
	    final Iterator i = filteredItemIds.iterator();
	    while (i.hasNext() && !itemId.equals(i.next())) {
		;
	    }
	    if (i.hasNext()) {
		return i.next();
	    }
	    return null;
	}
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
	if (filteredItemIds != null) {
	    if (!filteredItemIds.contains(itemId)) {
		return null;
	    }
	    final Iterator i = filteredItemIds.iterator();
	    if (itemId == null) {
		return null;
	    }
	    Object prev = null;
	    Object current;
	    while (i.hasNext() && !itemId.equals(current = i.next())) {
		prev = current;
	    }
	    return prev;
	}
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
	    if (filteredItemIds != null) {
		return filteredItemIds.iterator().next();
	    }
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
	    if (filteredItemIds != null) {
		final Iterator i = filteredItemIds.iterator();
		Object last = null;
		while (i.hasNext()) {
		    last = i.next();
		}
		return last;
	    }
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
	if (filteredItemIds != null) {
	    try {
		final Object first = filteredItemIds.iterator().next();
		return (itemId != null && itemId.equals(first));
	    } catch (final NoSuchElementException e) {
		return false;
	    }
	}
	return (size() >= 1 && itemIds.get(0).equals(itemId));
    }

    /**
     * @see com.vaadin.data.Container.Ordered#isLastId(java.lang.Object)
     */
    @Override
    public boolean isLastId(Object itemId) {
	if (filteredItemIds != null) {
	    try {
		Object last = null;
		for (final Iterator i = filteredItemIds.iterator(); i.hasNext();) {
		    last = i.next();
		}
		return (itemId != null && itemId.equals(last));
	    } catch (final NoSuchElementException e) {
		return false;
	    }
	}
	final int s = size();
	return (s >= 1 && itemIds.get(s - 1).equals(itemId));
    }

    /**
     * @see com.vaadin.data.Container.Ordered#addItemAfter(java.lang.Object)
     */
    @Override
    public Object addItemAfter(Object previousItemId) throws UnsupportedOperationException {

	// Creates a new id
	final Object id = generateId();

	if (addItemAfter(previousItemId, id) != null) {
	    return id;
	} else {
	    return null;
	}
    }

    /**
     * @see com.vaadin.data.Container.Ordered#addItemAfter(java.lang.Object,
     *      java.lang.Object)
     */
    @Override
    public Item addItemAfter(Object previousItemId, Object newItemId) throws UnsupportedOperationException {
	// Adding an item after null item adds the item as first item of the
	// ordered container.
	if (previousItemId == null) {
	    return addItemAt(0, newItemId);
	}

	// Get the index of the addition
	int index = -1;
	if (previousItemId != null) {
	    index = 1 + indexOfId(previousItemId);
	    if (index <= 0 || index > size()) {
		return null;
	    }
	}
	return addItemAt(index, newItemId);
    }

    /* Container.Indexed interface implementation */

    /**
     * @see com.vaadin.data.Container.Indexed#indexOfId(java.lang.Object)
     */
    @Override
    public int indexOfId(Object itemId) {
	if (filteredItemIds != null) {
	    int index = 0;
	    if (itemId == null) {
		return -1;
	    }
	    final Iterator i = filteredItemIds.iterator();
	    while (i.hasNext()) {
		Object id = i.next();
		if (itemId.equals(id)) {
		    return index;
		}
		index++;
	    }
	    return -1;
	}
	return itemIds.indexOf(itemId);
    }

    /**
     * @see com.vaadin.data.Container.Indexed#getIdByIndex(int)
     */
    @Override
    public Object getIdByIndex(int index) {
	if (filteredItemIds != null) {
	    if (index < 0) {
		throw new IndexOutOfBoundsException();
	    }
	    try {
		final Iterator i = filteredItemIds.iterator();
		while (index-- > 0) {
		    i.next();
		}
		return i.next();
	    } catch (final NoSuchElementException e) {
		throw new IndexOutOfBoundsException();
	    }
	}

	return itemIds.get(index);
    }

    /**
     * @see com.vaadin.data.Container.Indexed#addItemAt(int)
     */
    @Override
    public Object addItemAt(int index) throws UnsupportedOperationException {
	// Creates a new id
	final Object id = generateId();
	// Adds the Item into container
	addItemAt(index, id);
	return id;
    }

    /**
     * @see com.vaadin.data.Container.Indexed#addItemAt(int, java.lang.Object)
     */
    @Override
    public Item addItemAt(int index, Object newItemId) throws UnsupportedOperationException {

	// add item based on a filtered index
	int internalIndex = -1;
	if (filteredItemIds == null) {
	    internalIndex = index;
	} else if (index == 0) {
	    internalIndex = 0;
	} else if (index == size()) {
	    // add just after the last item
	    Object id = getIdByIndex(index - 1);
	    internalIndex = itemIds.indexOf(id) + 1;
	} else if (index > 0 && index < size()) {
	    // map the index of the visible item to its unfiltered index
	    Object id = getIdByIndex(index);
	    internalIndex = itemIds.indexOf(id);
	}
	if (internalIndex >= 0) {
	    return addItemAtInternalIndex(internalIndex, newItemId);
	} else {
	    return null;
	}
    }

    /**
     * Adds new item at given index of the internal (unfiltered) list.
     * <p>
     * The item is also added in the visible part of the list if it passes the
     * filters.
     * </p>
     * 
     * @param index Internal index to add the new item.
     * @param newItemId Id of the new item to be added.
     * @return Returns new item or null if the operation fails.
     */
    private Item addItemAtInternalIndex(int index, Object newItemId) {
	// Make sure that the Item is valid and has not been created yet
	if (index < 0 || index > itemIds.size() || newItemId == null || items.containsKey(newItemId)) {
	    return null;
	}

	// Adds the Item to container
	itemIds.add(index, newItemId);
	items.put(newItemId, createItem(newItemId));
	addDefaultValues(items.get(newItemId));

	if (filteredItemIds != null) {
	    // when the item data is set later (IndexedContainerProperty),
	    // filtering is updated
	    updateContainerFiltering();
	} else {
	    fireContainerItemSetChange();
	}

	return items.get(newItemId);
    }

    /* Container.Sortable interface implementation */

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
	if (filteredItemIds != null) {
	    updateContainerFiltering();
	} else {
	    fireContainerItemSetChange();
	}
    }

    /**
     * @see com.vaadin.data.Container.Sortable#getSortableContainerPropertyIds()
     */
    @Override
    public Collection<?> getSortableContainerPropertyIds() {
	final LinkedList list = new LinkedList();
	for (final Iterator i = propertyIds.iterator(); i.hasNext();) {
	    final Object id = i.next();
	    final Class type = getType(id);
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

    /**
     * Perform the sorting of the data structures in the container. This is
     * invoked when the <code>itemSorter</code> has been prepared for the sort
     * operation. Typically this method calls
     * <code>Collections.sort(aCollection, getItemSorter())</code> on all arrays
     * (containing item ids) that need to be sorted.
     * 
     */
    protected void doSort() {
	Collections.sort(itemIds, getItemSorter());
    }

    /* Container.Filterable interface implementation */

    /**
     * @see com.vaadin.data.Container.Filterable#addContainerFilter(java.lang.Object,
     *      java.lang.String, boolean, boolean)
     */
    @Override
    public void addContainerFilter(Object propertyId, String filterString, boolean ignoreCase, boolean onlyMatchPrefix) {
	if (filters == null) {
	    filters = new HashSet<Filter>();
	}
	filters.add(new Filter(propertyId, filterString, ignoreCase, onlyMatchPrefix));
	updateContainerFiltering();
    }

    /**
     * @see com.vaadin.data.Container.Filterable#removeAllContainerFilters()
     */
    @Override
    public void removeAllContainerFilters() {
	if (filters == null) {
	    return;
	}
	filters.clear();
	updateContainerFiltering();
    }

    /**
     * @see com.vaadin.data.Container.Filterable#removeContainerFilters(java.lang
     *      .Object)
     */
    @Override
    public void removeContainerFilters(Object propertyId) {
	if (filters == null || propertyId == null) {
	    return;
	}
	final Iterator<Filter> i = filters.iterator();
	while (i.hasNext()) {
	    final Filter f = i.next();
	    if (propertyId.equals(f.propertyId)) {
		i.remove();
	    }
	}
	updateContainerFiltering();
    }

    private void updateContainerFiltering(Object propertyId) {
	if (filters == null || propertyId == null) {
	    return;
	}
	// update container filtering if there is a filter for the given
	// property
	final Iterator<Filter> i = filters.iterator();
	while (i.hasNext()) {
	    final Filter f = i.next();
	    if (propertyId.equals(f.propertyId)) {
		updateContainerFiltering();
		return;
	    }
	}
    }

    /**
     * Called when the filters have changed or when another event that effects
     * filtering has taken place. Updates internal data structures and fires an
     * item set change if necessary.
     */
    private void updateContainerFiltering() {

	// Clearing filters?
	boolean hasFilters = (filters != null && !filters.isEmpty());

	if (doFilterContainer(hasFilters)) {
	    fireContainerItemSetChange();
	}
    }

    /**
     * Filters the data in the container and updates internal data structures.
     * This method should reset any internal data structures and then repopulate
     * them so {@link #getItemIds()} and other methods only return the filtered
     * items.
     * 
     * @param hasFilters true if filters has been set for the container, false
     *            otherwise
     * @return true if the item set has changed as a result of the filtering
     */
    protected boolean doFilterContainer(boolean hasFilters) {
	if (!hasFilters) {
	    filteredItemIds = null;
	    if (filters != null) {
		filters = null;
		return true;
	    }

	    return false;
	}
	// Reset filtered list
	if (filteredItemIds == null) {
	    filteredItemIds = new LinkedHashSet();
	} else {
	    filteredItemIds.clear();
	}

	// Filter
	for (final Iterator i = itemIds.iterator(); i.hasNext();) {
	    final Object id = i.next();
	    if (passesFilters(id)) {
		filteredItemIds.add(id);
	    }
	}

	return true;

    }

    /**
     * Checks if the given itemId passes the filters set for the container. The
     * caller should make sure the itemId exists in the container. For
     * non-existing itemIds the behavior is undefined.
     * 
     * @param itemId An itemId that exists in the container.
     * @return true if the itemId passes all filters or no filters are set,
     *         false otherwise.
     */
    protected boolean passesFilters(Object itemId) {
	Item item = items.get(itemId);
	if (filters == null) {
	    return true;
	}
	if (item == null) {
	    return false;
	}
	final Iterator<Filter> i = filters.iterator();
	while (i.hasNext()) {
	    final Filter f = i.next();
	    if (!f.passesFilter(item)) {
		return false;
	    }
	}
	return true;
    }
}
