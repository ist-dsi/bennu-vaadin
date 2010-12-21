package pt.ist.vaadinframework.data.old;
//package pt.ist.bennu.vaadin.data;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.Map.Entry;
//import java.util.Set;
//
//import com.vaadin.data.Container;
//import com.vaadin.data.Item;
//import com.vaadin.data.Property;
//import com.vaadin.data.util.BeanItem;
//import com.vaadin.data.util.Filter;
//import com.vaadin.data.util.MethodProperty;
//
//public class BeanItemContainer<BT> implements Container, Container.Filterable {
//    private static final long serialVersionUID = 7674707007444985973L;
//
//    private final List<BT> filteredItemIds = new ArrayList<BT>();
//
//    private final Map<BT, BeanItem<BT>> items = new HashMap<BT, BeanItem<BT>>();
//
//    private Collection<String> propertyIds;
//
//    private final Map<String, Class<?>> types = new HashMap<String, Class<?>>();
//
//    private final Set<Filter> filters = new HashSet<Filter>();
//
//    @Override
//    public Item getItem(Object itemId) {
//	if (itemId != null && filteredItemIds.contains(itemId)) {
//	    return items.get(itemId);
//	}
//	return null;
//    }
//
//    @Override
//    public Collection<?> getContainerPropertyIds() {
//	return Collections.unmodifiableCollection(propertyIds);
//    }
//
//    @Override
//    public Collection<?> getItemIds() {
//	return Collections.unmodifiableCollection(filteredItemIds);
//    }
//
//    @Override
//    public Property getContainerProperty(Object itemId, Object propertyId) {
//	if (itemId == null) {
//	    return null;
//	} else if (!filteredItemIds.contains(itemId)) {
//	    return null;
//	}
//	return items.get(itemId).getItemProperty(propertyId);
//    }
//
//    @Override
//    public Class<?> getType(Object propertyId) {
//	return types.get(propertyId);
//    }
//
//    @Override
//    public int size() {
//	return filteredItemIds.size();
//    }
//
//    @Override
//    public boolean containsId(Object itemId) {
//	if (itemId == null) {
//	    return false;
//	}
//	return filteredItemIds.contains(itemId);
//    }
//
//    @Override
//    public Item addItem(Object itemId) throws UnsupportedOperationException {
//	// Make sure that the Item is valid and has not been created yet
//	if (itemId == null || items.containsKey(itemId)) {
//	    return null;
//	}
//
//	BT id = (BT) itemId;
//	BeanItem<BT> item = new BeanItem<BT>(id);
//	// Adds the Item to container (at the end of the unfiltered list)
//	items.put(id, item);
//
//	if (passesFilters(id)) {
//	    filteredItemIds.add(id);
//	}
//
//	// Sends the event
//	fireContentsChange(items.size() - 1);
//
//	return item;
//    }
//
//    @Override
//    public Object addItem() throws UnsupportedOperationException {
//	throw new UnsupportedOperationException();
//    }
//
//    @Override
//    public boolean removeItem(Object itemId) throws UnsupportedOperationException {
//	if (itemId == null) {
//	    return false;
//	}
//
//	if (items.remove(itemId) == null) {
//	    return false;
//	}
//	filteredItemIds.remove(itemId);
//
//	fireContentsChange(-1);
//
//	return true;
//    }
//
//    @Override
//    public boolean addContainerProperty(Object propertyId, Class<?> type, Object defaultValue)
//	    throws UnsupportedOperationException {
//	throw new UnsupportedOperationException();
//    }
//
//    public boolean addContainerProperty(String propertyId, Class<?> type) {
//
//	// Fails, if nulls are given
//	if (propertyId == null || type == null) {
//	    return false;
//	}
//
//	// Fails if the Property is already present
//	if (propertyIds.contains(propertyId)) {
//	    return false;
//	}
//
//	// Adds the Property to Property list and types
//	propertyIds.add(propertyId);
//	types.put(propertyId, type);
//
//	for (Entry<BT, BeanItem<BT>> entry : items.entrySet()) {
//	    entry.getValue().addItemProperty(propertyId, new MethodProperty(entry.getKey(), propertyId));
//	}
//
//	// Sends a change event
//	fireContainerPropertySetChange();
//
//	return true;
//    }
//
//    @Override
//    public boolean removeContainerProperty(Object propertyId) throws UnsupportedOperationException {
//	// Fails if the Property is not present
//	if (!propertyIds.contains(propertyId)) {
//	    return false;
//	}
//
//	// Removes the Property to Property list and types
//	propertyIds.remove(propertyId);
//	types.remove(propertyId);
//
//	for (Entry<BT, BeanItem<BT>> entry : items.entrySet()) {
//	    entry.getValue().removeItemProperty(propertyId);
//	}
//
//	// Sends a change event
//	fireContainerPropertySetChange();
//
//	return true;
//    }
//
//    @Override
//    public boolean removeAllItems() throws UnsupportedOperationException {
//	items.clear();
//	filteredItemIds.clear();
//
//	// Sends a change event
//	fireContentsChange(-1);
//
//	return true;
//    }
//
//    /* Container.Filterable */
//
//    @Override
//    public void addContainerFilter(Object propertyId, String filterString, boolean ignoreCase, boolean onlyMatchPrefix) {
//	filters.add(new Filter(propertyId, filterString, ignoreCase, onlyMatchPrefix));
//	updateContainerFiltering();
//    }
//
//    @Override
//    public void removeAllContainerFilters() {
//	// TODO Auto-generated method stub
//
//    }
//
//    @Override
//    public void removeContainerFilters(Object propertyId) {
//	// TODO Auto-generated method stub
//
//    }
//
//    /* Private Utility Methods */
//
//    /**
//     * Checks if the given itemId passes the filters set for the container. The
//     * caller should make sure the itemId exists in the container. For
//     * non-existing itemIds the behavior is undefined.
//     * 
//     * @param itemId
//     *            An itemId that exists in the container.
//     * @return true if the itemId passes all filters or no filters are set,
//     *         false otherwise.
//     */
//    protected boolean passesFilters(Object itemId) {
//	if (filters.isEmpty()) {
//	    return true;
//	}
//	Item item = getItem(itemId);
//	if (item == null) {
//	    return false;
//	}
//	for (Filter filter : filters) {
//	    if (!filter.passesFilter(item)) {
//		return false;
//	    }
//	}
//	return true;
//    }
// }
