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
package pt.ist.vaadinframework.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EventObject;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import pt.ist.fenixframework.pstm.AbstractDomainObject;

import com.vaadin.data.Buffered;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Validator.InvalidValueException;

/**
 * @author Pedro Santos (pedro.miguel.santos@ist.utl.pt)
 */
public class EntityItem<T extends AbstractDomainObject> implements Item, Item.PropertySetChangeNotifier, Buffered {
    public class EntityItemProperty implements Property, Property.ValueChangeNotifier, Property.ReadOnlyStatusChangeNotifier {
	private final LinkedList<ReadOnlyStatusChangeListener> readOnlyStatusChangeListeners = null;

	private final LinkedList<ValueChangeListener> valueChangeListeners = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.vaadin.data.Property.ReadOnlyStatusChangeNotifier#addListener
	 * (com.vaadin.data.Property.ReadOnlyStatusChangeListener)
	 */
	@Override
	public void addListener(ReadOnlyStatusChangeListener listener) {
	    // TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.vaadin.data.Property.ReadOnlyStatusChangeNotifier#removeListener
	 * (com.vaadin.data.Property.ReadOnlyStatusChangeListener)
	 */
	@Override
	public void removeListener(ReadOnlyStatusChangeListener listener) {
	    // TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.vaadin.data.Property.ValueChangeNotifier#addListener(com.vaadin
	 * .data.Property.ValueChangeListener)
	 */
	@Override
	public void addListener(ValueChangeListener listener) {
	    // TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.vaadin.data.Property.ValueChangeNotifier#removeListener(com.vaadin
	 * .data.Property.ValueChangeListener)
	 */
	@Override
	public void removeListener(ValueChangeListener listener) {
	    // TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vaadin.data.Property#getValue()
	 */
	@Override
	public Object getValue() {
	    // TODO Auto-generated method stub
	    return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vaadin.data.Property#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(Object newValue) throws ReadOnlyException, ConversionException {
	    // TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vaadin.data.Property#getType()
	 */
	@Override
	public Class<?> getType() {
	    // TODO Auto-generated method stub
	    return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vaadin.data.Property#isReadOnly()
	 */
	@Override
	public boolean isReadOnly() {
	    // TODO Auto-generated method stub
	    return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vaadin.data.Property#setReadOnly(boolean)
	 */
	@Override
	public void setReadOnly(boolean newStatus) {
	    // TODO Auto-generated method stub

	}

	/**
	 * 
	 */
	public void commit() {
	    // TODO Auto-generated method stub

	}

	/**
	 * 
	 */
	public void discard() {
	    // TODO Auto-generated method stub

	}

	/**
	 * 
	 */
	public void clearCache() {
	    // TODO Auto-generated method stub

	}

	/**
	 * 
	 */
	public void notifyListenersIfCacheAndRealValueDiffer() {
	    // TODO Auto-generated method stub

	}

	/**
	 * 
	 */
	public void cacheRealValue() {
	    // TODO Auto-generated method stub

	}

    }

    private static final long serialVersionUID = -1255912064304892459L;

    private final T entity;

    private final Map<Object, EntityItemProperty> properties = new HashMap<Object, EntityItemProperty>();

    private final List<Object> propertyIds = new LinkedList<Object>();

    private List<PropertySetChangeListener> propertySetChangeListeners = null;

    private boolean modified;

    private boolean writeThrough;

    private boolean readThrough;

    public EntityItem(T entity) {
	this.entity = entity;
    }

    public T getEntity() {
	return entity;
    }

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
	// Cant remove missing properties
	if (properties.remove(id) == null) {
	    return false;
	}
	propertyIds.remove(id);

	// Send change events
	fireItemPropertySetChange();

	return true;
    }

    /**
     * Gets the <code>String</code> representation of the contents of the Item.
     * The format of the string is a space separated catenation of the
     * <code>String</code> representations of the Properties contained by the
     * Item.
     * 
     * @return <code>String</code> representation of the Item contents
     */
    @Override
    public String toString() {
	List<String> values = new ArrayList<String>();
	for (EntityItemProperty property : properties.values()) {
	    values.add(property.toString());
	}
	return StringUtils.join(values, " ");
    }

    /**
     * @see com.vaadin.data.Buffered#commit()
     */
    @Override
    public void commit() throws SourceException, InvalidValueException {
	if (!isWriteThrough()) {
	    try {
		/*
		 * Commit all properties. The commit() operation will check if
		 * the property is read only and ignore it if that is the case.
		 */
		for (EntityItemProperty prop : properties.values()) {
		    prop.commit();
		}
		modified = false;
		// container.containerItemModified(this);
	    } catch (Property.ConversionException e) {
		throw new InvalidValueException(e.getMessage());
	    } catch (Property.ReadOnlyException e) {
		throw new SourceException(this, e);
	    }
	}
    }

    /**
     * @see com.vaadin.data.Buffered#discard()
     */
    @Override
    public void discard() throws SourceException {
	if (!isWriteThrough()) {
	    for (EntityItemProperty prop : properties.values()) {
		prop.discard();
	    }
	    modified = false;
	}
    }

    /**
     * @see com.vaadin.data.Buffered#isWriteThrough()
     */
    @Override
    public boolean isWriteThrough() {
	return writeThrough;
    }

    /**
     * @see com.vaadin.data.Buffered#setWriteThrough(boolean)
     */
    @Override
    public void setWriteThrough(boolean writeThrough) throws SourceException, InvalidValueException {
	if (this.writeThrough != writeThrough) {
	    if (writeThrough) {
		commit();
		for (EntityItemProperty prop : properties.values()) {
		    prop.clearCache();
		}
	    } else {
		/*
		 * We can iterate directly over the map, as this operation only
		 * affects existing properties. Properties that are lazily
		 * created afterwards will work automatically.
		 */
		for (EntityItemProperty prop : properties.values()) {
		    prop.cacheRealValue();
		}
	    }
	    this.writeThrough = writeThrough;
	    /*
	     * Normally, if writeThrough is changed, readThrough should also be
	     * changed.
	     */
	    setReadThrough(writeThrough);
	}
    }

    /**
     * @see com.vaadin.data.Buffered#isReadThrough()
     */
    @Override
    public boolean isReadThrough() {
	return readThrough;
    }

    /**
     * @see com.vaadin.data.Buffered#setReadThrough(boolean)
     */
    @Override
    public void setReadThrough(boolean readThrough) throws SourceException {
	if (this.readThrough != readThrough) {
	    if (!readThrough && writeThrough) {
		throw new IllegalStateException("ReadThrough can only be turned off if WriteThrough is turned off");
	    }
	    /*
	     * We can iterate directly over the map, as this operation only
	     * affects existing properties. Properties that are lazily created
	     * afterwards will work automatically.
	     */
	    for (EntityItemProperty prop : properties.values()) {
		prop.notifyListenersIfCacheAndRealValueDiffer();
	    }
	    this.readThrough = readThrough;
	}
    }

    /**
     * @see com.vaadin.data.Buffered#isModified()
     */
    @Override
    public boolean isModified() {
	return modified;
    }

    /* Notifiers */

    private class PropertySetChangeEvent extends EventObject implements Item.PropertySetChangeEvent {
	private PropertySetChangeEvent(Item source) {
	    super(source);
	}

	/**
	 * Gets the Item whose Property set has changed.
	 * 
	 * @return source object of the event as an <code>Item</code>
	 */
	public Item getItem() {
	    return (Item) getSource();
	}
    }

    /**
     * Registers a new property set change listener for this Item.
     * 
     * @param listener
     *            the new Listener to be registered.
     */
    public void addListener(Item.PropertySetChangeListener listener) {
	if (propertySetChangeListeners == null) {
	    propertySetChangeListeners = new LinkedList<Item.PropertySetChangeListener>();
	}
	propertySetChangeListeners.add(listener);
    }

    /**
     * Removes a previously registered property set change listener.
     * 
     * @param listener
     *            the Listener to be removed.
     */
    public void removeListener(Item.PropertySetChangeListener listener) {
	if (propertySetChangeListeners != null) {
	    propertySetChangeListeners.remove(listener);
	}
    }

    /**
     * Sends a Property set change event to all interested listeners.
     */
    private void fireItemPropertySetChange() {
	if (propertySetChangeListeners != null) {
	    final Object[] l = propertySetChangeListeners.toArray();
	    final Item.PropertySetChangeEvent event = new PropertySetChangeEvent(this);
	    for (int i = 0; i < l.length; i++) {
		((Item.PropertySetChangeListener) l[i]).itemPropertySetChange(event);
	    }
	}
    }
}
