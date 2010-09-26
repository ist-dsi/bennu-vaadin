package pt.ist.bennu.vaadin.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;

public class BennuContainer implements Container {
    // Container.Indexed, Container.ItemSetChangeNotifier,
    // Container.PropertySetChangeNotifier,
    // Property.ValueChangeNotifier, Container.Sortable, Cloneable,
    // Container.Filterable {
    private static final long serialVersionUID = -3053876101289734601L;

    private final Map<Object, BennuItem> items = new HashMap<Object, BennuItem>();

    private final List<String> propertyIds = new ArrayList<String>();

    public BennuContainer(Set<?> objects) {
	for (Object object : objects) {
	    super.addItem(object);
	}
    }

    @Override
    public Item getItem(Object itemId) {
	if (items.containsKey(itemId)) {
	    return items.get(itemId);
	}
	return null;
    }

    @Override
    public Collection<String> getContainerPropertyIds() {
	return propertyIds;
    }

    @Override
    public Collection<?> getItemIds() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public Property getContainerProperty(Object itemId, Object propertyId) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public Class<?> getType(Object propertyId) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public int size() {
	// TODO Auto-generated method stub
	return 0;
    }

    @Override
    public boolean containsId(Object itemId) {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public Item addItem(Object itemId) throws UnsupportedOperationException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public Object addItem() throws UnsupportedOperationException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public boolean removeItem(Object itemId) throws UnsupportedOperationException {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public boolean addContainerProperty(Object propertyId, Class<?> type, Object defaultValue)
	    throws UnsupportedOperationException {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public boolean removeContainerProperty(Object propertyId) throws UnsupportedOperationException {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public boolean removeAllItems() throws UnsupportedOperationException {
	// TODO Auto-generated method stub
	return false;
    }

    //
    // @Override
    // public Item getItem(Object itemId) {
    // if (containsId(itemId)) {
    // return new BennuItem(itemId);
    // }
    // return null;
    // }
    //
    // @Override
    // public Property getContainerProperty(Object itemId, Object propertyId) {
    // if (containsId(itemId)) {
    // BennuItem item = (BennuItem) getItem(itemId);
    // if (item.getItemProperty(propertyId) == null) {
    // item.addItemProperty((String) propertyId);
    // }
    // return item.getItemProperty(propertyId);
    // }
    // return null;
    // }
    //
    // @Override
    // public boolean addContainerProperty(Object propertyId, Class<?> type,
    // Object defaultValue) {
    // for (Object itemId : getItemIds()) {
    // ((BennuItem) getItem(itemId)).addItemProperty((String) propertyId);
    // }
    //
    // // Fails, if nulls are given
    // if (propertyId == null || type == null) {
    // return false;
    // }
    //
    // // Fails if the Property is already present
    // if (propertyIds.contains(propertyId)) {
    // return false;
    // }
    //
    // // Adds the Property to Property list and types
    // propertyIds.add(propertyId);
    // types.put(propertyId, type);
    //
    // // If default value is given, set it
    // if (defaultValue != null) {
    // // for existing rows
    // for (final Iterator i = itemIds.iterator(); i.hasNext();) {
    // getItem(i.next()).getItemProperty(propertyId).setValue(defaultValue);
    // }
    // // store for next rows
    // if (defaultPropertyValues == null) {
    // defaultPropertyValues = new HashMap<Object, Object>();
    // }
    // defaultPropertyValues.put(propertyId, defaultValue);
    // }
    //
    // // Sends a change event
    // fireContainerPropertySetChange();
    //
    // return true;
    // }
    //
    // @Override
    // public Item addItem(Object itemId) {
    // super.addItem(itemId);
    // return new BennuItem(itemId);
    // }
    //
    // @Service
    // public void save() {
    // for (Object itemId : getItemIds()) {
    // Item item = getItem(itemId);
    // if (item instanceof BennuItem) {
    // ((BennuItem) item).save();
    // }
    // }
    // }

}
