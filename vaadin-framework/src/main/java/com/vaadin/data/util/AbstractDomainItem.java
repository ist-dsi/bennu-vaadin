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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import pt.ist.fenixframework.pstm.AbstractDomainObject;

import com.vaadin.data.Item;
import com.vaadin.data.Property;

/**
 * @author Pedro Santos (pedro.miguel.santos@ist.utl.pt)
 * 
 */
public abstract class AbstractDomainItem extends AbstractDomainProperty implements Item, Item.PropertySetChangeNotifier {
    private final List<Object> propertyIds = new ArrayList<Object>();

    private final Map<Object, Property> properties = new HashMap<Object, Property>();

    /**
     * Create from domain object instance.
     * 
     * @param value The instance to wrap.
     */
    public AbstractDomainItem(AbstractDomainObject value) {
	super(value);
    }

    /**
     * Create from domain object type.
     * 
     * @param type The type of domain object.
     */
    public AbstractDomainItem(Class<? extends AbstractDomainObject> type) {
	super(type);
    }

    /**
     * Item that hosts the object instance,
     * 
     * @param host
     * @param type
     */
    public AbstractDomainItem(AbstractDomainItem host, Class<? extends AbstractDomainObject> type) {
	super(host, type);
    }

    /**
     * @see com.vaadin.data.Item#getItemProperty(java.lang.Object)
     */
    @Override
    public Property getItemProperty(Object propertyId) {
	if (!properties.containsKey(propertyId)) {
	    Property property;
	    if (propertyId instanceof String) {
		String id = (String) propertyId;
		int split = id.indexOf('.');
		if (split == -1) {
		    property = lazyCreateProperty(id);
		} else {
		    String first = id.substring(0, split);
		    String rest = id.substring(split + 1);
		    property = getItemProperty(first);
		    propertyIds.add(first);
		    properties.put(first, property);
		    property = ((AbstractDomainItem) property).getItemProperty(rest);
		}
	    } else {
		property = lazyCreateProperty(propertyId);
	    }
	    if (property != null) {
		propertyIds.add(propertyId);
		properties.put(propertyId, property);
	    }
	}
	return properties.get(propertyId);
    }

    /**
     * @see com.vaadin.data.util.AbstractDomainProperty#getValue()
     */
    @Override
    public AbstractDomainObject getValue() {
	return (AbstractDomainObject) super.getValue();
    }

    public AbstractDomainObject getOrCreateValue() {
	if (getValue() == null) {
	    // setValue(getType().newInstance());
	    setValue(createNewInstance());
	    fireInstanceCreation();
	}
	return getValue();
    }

    /**
     * @see com.vaadin.data.util.AbstractDomainProperty#getType()
     */
    @Override
    public Class<? extends AbstractDomainObject> getType() {
	return (Class<? extends AbstractDomainObject>) super.getType();
    }

    /**
     * @param id
     * @return
     */
    protected abstract Property lazyCreateProperty(Object propertyId);

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
	if (id == null) {
	    throw new NullPointerException("Item property id can not be null");
	}
	if (properties.containsKey(id)) {
	    return false;
	}
	propertyIds.add(id);
	properties.put(id, property);
	fireItemPropertySetChange();
	return true;
    }

    /**
     * @see com.vaadin.data.Item#removeItemProperty(java.lang.Object)
     */
    @Override
    public boolean removeItemProperty(Object id) throws UnsupportedOperationException {
	if (properties.remove(id) == null) {
	    return false;
	}
	propertyIds.remove(id);
	fireItemPropertySetChange();
	return true;
    }

    private List<PropertySetChangeListener> propertySetChangeListeners;

    private List<InstanceCreationListener> instanceCreationListeners;

    private class PropertySetChangeEvent extends EventObject implements Item.PropertySetChangeEvent {
	private PropertySetChangeEvent(AbstractDomainItem source) {
	    super(source);
	}

	@Override
	public AbstractDomainItem getItem() {
	    return (AbstractDomainItem) getSource();
	}
    }

    /**
     * @see com.vaadin.data.Item.PropertySetChangeNotifier#addListener(com.vaadin.data.Item.PropertySetChangeListener)
     */
    @Override
    public void addListener(PropertySetChangeListener listener) {
	if (propertySetChangeListeners == null) {
	    propertySetChangeListeners = new LinkedList<PropertySetChangeListener>();
	}
	propertySetChangeListeners.add(listener);
    }

    /**
     * @see com.vaadin.data.Item.PropertySetChangeNotifier#removeListener(com.vaadin.data.Item.PropertySetChangeListener)
     */
    @Override
    public void removeListener(PropertySetChangeListener listener) {
	if (propertySetChangeListeners != null) {
	    propertySetChangeListeners.remove(listener);
	}
    }

    protected void fireItemPropertySetChange() {
	if (propertySetChangeListeners != null) {
	    final PropertySetChangeListener[] l = propertySetChangeListeners.toArray(new PropertySetChangeListener[0]);
	    final Item.PropertySetChangeEvent event = new PropertySetChangeEvent(this);
	    for (int i = 0; i < l.length; i++) {
		l[i].itemPropertySetChange(event);
	    }
	}
    }

    private class DomainItemInstanceCreationEvent extends EventObject implements InstanceCreationEvent {
	protected DomainItemInstanceCreationEvent(AbstractDomainItem source) {
	    super(source);
	}

	@Override
	public AbstractDomainItem getDomainItem() {
	    return (AbstractDomainItem) getSource();
	}
    }

    public void addListener(InstanceCreationListener listener) {
	if (instanceCreationListeners == null) {
	    instanceCreationListeners = new LinkedList<InstanceCreationListener>();
	}
	instanceCreationListeners.add(listener);
    }

    public void removeListener(InstanceCreationListener listener) {
	if (instanceCreationListeners != null) {
	    instanceCreationListeners.remove(listener);
	}
    }

    /**
     * Sends a read only status change event to all registered listeners.
     */
    protected void fireInstanceCreation() {
	if (instanceCreationListeners != null) {
	    final Object[] l = instanceCreationListeners.toArray();
	    final InstanceCreationEvent event = new DomainItemInstanceCreationEvent(this);
	    for (int i = 0; i < l.length; i++) {
		((InstanceCreationListener) l[i]).itemCreation(event);
	    }
	}
    }
    
    @SuppressWarnings("unchecked")
    private String getBundleKey(ResourceBundle bundle, Class<? extends AbstractDomainObject> clazz, Object propertyId) {
	if (AbstractDomainObject.class.equals(clazz)) {
	    return null;
	}
	String key = clazz.getName() + "." + propertyId;
	if (bundle.containsKey(key)) {
	    return bundle.getString(key);
	}
	final Class<? extends AbstractDomainObject> superclass = (Class<? extends AbstractDomainObject>)clazz.getSuperclass();
	return getBundleKey(bundle,superclass, propertyId);
    }
    
    /**
     * @param propertyId
     * @return
     */
    public String getLabelKey(ResourceBundle bundle, Object propertyId) {
//	return getType().getName() + "." + propertyId;
	return getBundleKey(bundle, getType(), propertyId);
    }

    /**
     * @param propertyId
     * @return
     */
    public String getDescriptionKey(ResourceBundle bundle, Object propertyId) {
	return getBundleKey(bundle,getType(),propertyId + ".description");
    }
}
