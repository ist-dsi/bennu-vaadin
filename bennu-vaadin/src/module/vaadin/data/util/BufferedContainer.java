package module.vaadin.data.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import pt.ist.fenixWebFramework.services.Service;

import com.vaadin.data.Buffered;
import com.vaadin.data.BufferedValidatable;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.AbstractInMemoryContainer;
import com.vaadin.data.util.filter.UnsupportedFilterException;

public abstract class BufferedContainer<ItemId, PropertyId, ItemType extends Item> extends
AbstractInMemoryContainer<Property, PropertyId, ItemType> implements Property, BufferedValidatable,
Property.ReadOnlyStatusChangeNotifier, Property.ValueChangeNotifier, Container.Sortable, Container.Filterable,
Container.PropertySetChangeNotifier {
    private final Property value;

    private final List<PropertyId> propertyIds = new ArrayList<PropertyId>();

    private final Map<PropertyId, Class<?>> types = new HashMap<PropertyId, Class<?>>();

    private final Map<Property, ItemType> items = new HashMap<Property, ItemType>();

    private boolean readThrough = true;

    private boolean writeThrough = true;

    private boolean invalidAllowed = true;

    private boolean invalidCommited = false;

    private boolean modified = false;

    private List<Validator> validators;

    private ItemConstructor<PropertyId> constructor;

    private ItemWriter<PropertyId> writer;

    private final Class<? extends ItemId> elementType;

    private boolean disableCommitPropagation = false;

    public BufferedContainer(Property value, Class<? extends ItemId> elementType) {
	this.value = value;
	this.elementType = elementType;
	if (!Collection.class.isAssignableFrom(value.getType())) {
	    throw new UnsupportedOperationException("Containers work with Collection typed properties");
	}
	if (getValue() != null) {
	    for (ItemId itemId : getValue()) {
		addItem(itemId);
	    }
	}
    }

    // Property implementation
    @Override
    public Collection<ItemId> getValue() {
	return (Collection<ItemId>) value.getValue();
    }

    @Override
    public void setValue(Object newValue) throws ReadOnlyException, ConversionException {
	value.setValue(newValue);
    }

    @Override
    public Class<? extends Collection<Property>> getType() {
	return (Class<? extends Collection<Property>>) value.getType();
    }

    public Class<? extends ItemId> getElementType() {
	return elementType;
    }

    @Override
    public boolean isReadOnly() {
	return value.isReadOnly();
    }

    @Override
    public void setReadOnly(boolean newStatus) {
	value.setReadOnly(newStatus);
    }

    @Override
    public void addListener(ReadOnlyStatusChangeListener listener) {
	if (value instanceof ReadOnlyStatusChangeNotifier) {
	    ((ReadOnlyStatusChangeNotifier) value).addListener(listener);
	} else {
	    throw new UnsupportedOperationException("Underlying property is not a ReadOnlyStatusChangeNotifier");
	}
    }

    @Override
    public void removeListener(ReadOnlyStatusChangeListener listener) {
	if (value instanceof ReadOnlyStatusChangeNotifier) {
	    ((ReadOnlyStatusChangeNotifier) value).removeListener(listener);
	} else {
	    throw new UnsupportedOperationException("Underlying property is not a ReadOnlyStatusChangeNotifier");
	}
    }

    @Override
    public void addListener(ValueChangeListener listener) {
	if (value instanceof ValueChangeNotifier) {
	    ((ValueChangeNotifier) value).addListener(listener);
	} else {
	    throw new UnsupportedOperationException("Underlying property is not a ValueChangeNotifier");
	}
    }

    @Override
    public void removeListener(ValueChangeListener listener) {
	if (value instanceof ValueChangeNotifier) {
	    ((ValueChangeNotifier) value).removeListener(listener);
	} else {
	    throw new UnsupportedOperationException("Underlying property is not a ValueChangeNotifier");
	}
    }

    // end of property implementation

    // BufferedValidatable implementation

    public void setConstructor(ItemConstructor<PropertyId> constructor) {
	this.constructor = constructor;
    }

    public void setWriter(ItemWriter<PropertyId> writer) {
	this.writer = writer;
    }

    @Override
    @Service
    public void commit() throws SourceException, InvalidValueException {
	if (!disableCommitPropagation) {
	    disableCommitPropagation = true;
	    for (Property itemId : getItemIds()) {
		if (getItem(itemId) instanceof Buffered) {
		    ((Buffered) getItem(itemId)).commit();
		}
	    }
	    Collection<ItemId> values = new ArrayList<ItemId>();
	    for (Property property : getItemIds()) {
		values.add((ItemId) property.getValue());
	    }
	    setValue(values);
	    disableCommitPropagation = false;
	}
    }

    @Override
    public void discard() throws SourceException {
	removeAllItems();
	if (getValue() != null) {
	    for (ItemId itemId : getValue()) {
		addItem(itemId);
	    }
	}
	modified = false;
    }

    @Override
    public boolean isWriteThrough() {
	return writeThrough;
    }

    @Override
    public void setWriteThrough(boolean writeThrough) throws SourceException, InvalidValueException {
	this.writeThrough = writeThrough;
    }

    @Override
    public boolean isReadThrough() {
	return readThrough;
    }

    @Override
    public void setReadThrough(boolean readThrough) throws SourceException {
	this.readThrough = readThrough;
    }

    @Override
    public boolean isModified() {
	return modified;
    }

    @Override
    public void addValidator(Validator validator) {
	if (validators == null) {
	    validators = new LinkedList<Validator>();
	}
	validators.add(validator);
    }

    @Override
    public void removeValidator(Validator validator) {
	if (validators != null) {
	    validators.remove(validator);
	}
    }

    @Override
    public Collection<Validator> getValidators() {
	if (validators == null || validators.isEmpty()) {
	    return null;
	}
	return Collections.unmodifiableCollection(validators);
    }

    @Override
    public boolean isValid() {
	if (validators != null) {
	    for (Validator validator : validators) {
		if (!validator.isValid(this)) {
		    return false;
		}
	    }
	}
	return true;
    }

    @Override
    public void validate() throws InvalidValueException {
	LinkedList<InvalidValueException> errors = null;
	if (validators != null) {
	    for (Validator validator : validators) {
		try {
		    validator.validate(this);
		} catch (InvalidValueException e) {
		    if (errors == null) {
			errors = new LinkedList<InvalidValueException>();
		    }
		    errors.add(e);
		}
	    }
	}
	if (errors != null) {
	    throw new InvalidValueException(null, errors.toArray(new InvalidValueException[0]));
	}
    }

    @Override
    public boolean isInvalidAllowed() {
	return invalidAllowed;
    }

    @Override
    public void setInvalidAllowed(boolean invalidAllowed) throws UnsupportedOperationException {
	this.invalidAllowed = invalidAllowed;
    }

    @Override
    public boolean isInvalidCommitted() {
	return invalidCommited;
    }

    @Override
    public void setInvalidCommitted(boolean isCommitted) {
	this.invalidCommited = isCommitted;
    }

    // end of BufferedValidatable implementation

    // container implementation

    @Override
    public Collection<PropertyId> getContainerPropertyIds() {
	return Collections.unmodifiableCollection(propertyIds);
    }

    /**
     * Add a new property to the container. The default value is ignored, we
     * have no intention to override the values in the underlying items.
     * 
     * @see Container#addContainerProperty(Object, Class, Object)
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
	propertyIds.add((PropertyId) propertyId);
	types.put((PropertyId) propertyId, type);

	// Sends a change event
	fireContainerPropertySetChange();

	return true;
    }

    /**
     * @see Container#removeContainerProperty(Object)
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

	// If remove the Property from all Items
	for (Property itemId : getAllItemIds()) {
	    items.get(itemId).removeItemProperty(propertyId);
	}

	// Sends a change event
	fireContainerPropertySetChange();

	return true;
    }

    /**
     * @see Container#getContainerProperty(java.lang.Object, java.lang.Object)
     */
    @Override
    public Property getContainerProperty(Object itemId, Object propertyId) {
	return getItem(itemId).getItemProperty(propertyId);
    }

    /**
     * @see Container#getType(java.lang.Object)
     */
    @Override
    public Class<?> getType(Object propertyId) {
	return types.get(propertyId);
    }

    /**
     * @see com.vaadin.data.util.AbstractInMemoryContainer#getItemIds()
     */
    @Override
    public Collection<Property> getItemIds() {
	return (Collection<Property>) super.getItemIds();
    }

    /**
     * @see Container.Filterable#addContainerFilter(Container.Filter)
     */
    @Override
    public void addContainerFilter(Filter filter) throws UnsupportedFilterException {
	addFilter(filter);
    }

    /**
     * @see Container.Filterable#removeContainerFilter(Container.Filter)
     */
    @Override
    public void removeContainerFilter(Filter filter) {
	removeFilter(filter);
    }

    /**
     * @see Container.Filterable#removeAllContainerFilters()
     */
    @Override
    public void removeAllContainerFilters() {
	removeAllFilters();
    }

    /**
     * @see Container.Sortable#sort(java.lang.Object[], boolean[])
     */
    @Override
    public void sort(Object[] propertyId, boolean[] ascending) {
	sortContainer(propertyId, ascending);
    }

    /**
     * @see Container.Sortable#getSortableContainerPropertyIds()
     */
    @Override
    public Collection<?> getSortableContainerPropertyIds() {
	return getSortablePropertyIds();
    }

    /**
     * @see com.vaadin.data.util.AbstractInMemoryContainer#getUnfilteredItem(java.lang.Object)
     */
    @Override
    protected ItemType getUnfilteredItem(Object itemId) {
	if (itemId != null && items.containsKey(itemId)) {
	    return items.get(itemId);
	}
	return null;
    }

    public abstract ItemType makeItem(Property itemId);

    @Override
    protected void registerNewItem(int position, final Property itemId, ItemType item) {
	items.put(itemId, item);
	if (itemId instanceof ValueChangeNotifier) {
	    ((ValueChangeNotifier) itemId).addListener(new ValueChangeListener() {
		@Override
		public void valueChange(ValueChangeEvent event) {
		    if (isWriteThrough()) {
			((ValueChangeNotifier) itemId).removeListener(this);
			commit();
			((ValueChangeNotifier) itemId).addListener(this);
		    }
		}
	    });
	}
	if (item instanceof BufferedItem) {
	    ((BufferedItem) item).setConstructor(constructor);
	    ((BufferedItem) item).setWriter(writer);
	}
    }

    @Override
    public Object addItemAt(int index) throws UnsupportedOperationException {
	throw new UnsupportedOperationException();
    }

    @Override
    public Item addItemAt(int index, Object newItemId) throws UnsupportedOperationException {
	return internalAddItemAt(index, (Property) newItemId, makeItem((Property) newItemId), true);
    }

    @Override
    public Object addItemAfter(Object previousItemId) throws UnsupportedOperationException {
	throw new UnsupportedOperationException();
    }

    @Override
    public Item addItemAfter(Object previousItemId, Object newItemId) throws UnsupportedOperationException {
	return internalAddItemAfter((Property) previousItemId, (Property) newItemId, makeItem((Property) newItemId), true);
    }

    @Override
    public Item addItem(Object itemId) throws UnsupportedOperationException {
	return internalAddItemAtEnd((Property) itemId, makeItem((Property) itemId), true);
    }

    @Override
    public Object addItem() throws UnsupportedOperationException {
	throw new UnsupportedOperationException();
    }

    @Override
    protected ItemType internalAddItemAt(int index, Property newItemId, ItemType item, boolean filter) {
	ItemType result = super.internalAddItemAt(index, newItemId, item, filter);
	if (isWriteThrough()) {
	    commit();
	}
	return result;
    }

    @Override
    public boolean removeItem(Object itemId) throws UnsupportedOperationException {
	if (itemId == null || items.remove(itemId) == null) {
	    return false;
	}
	int origSize = size();
	int position = indexOfId(itemId);
	if (internalRemoveItem(itemId)) {
	    // fire event only if the visible view changed, regardless of
	    // whether filtered out items were removed or not
	    if (size() != origSize) {
		if (isWriteThrough()) {
		    commit();
		}
		fireItemRemoved(position, itemId);
	    }

	    return true;
	}
	return false;
    }

    @Override
    public boolean removeAllItems() throws UnsupportedOperationException {
	int origSize = size();

	internalRemoveAllItems();

	items.clear();

	// fire event only if the visible view changed, regardless of whether
	// filtered out items were removed or not
	if (origSize != 0) {
	    if (isWriteThrough()) {
		commit();
	    }
	    // Sends a change event
	    fireItemSetChange();
	}
	return true;
    }

    /**
     * @see com.vaadin.data.util.AbstractContainer#addListener(com.vaadin.data.Container.PropertySetChangeListener)
     */
    @Override
    public void addListener(PropertySetChangeListener listener) {
	super.addListener(listener);
    }

    /**
     * @see com.vaadin.data.util.AbstractContainer#removeListener(com.vaadin.data.Container.PropertySetChangeListener)
     */
    @Override
    public void removeListener(PropertySetChangeListener listener) {
	super.removeListener(listener);
    }

    // end of container implementation
}
