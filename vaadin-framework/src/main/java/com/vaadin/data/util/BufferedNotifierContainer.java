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

import java.util.EventObject;
import java.util.LinkedList;
import java.util.List;

import com.vaadin.data.Container;

/**
 * @author Pedro Santos (pedro.miguel.santos@ist.utl.pt)
 * 
 */
@SuppressWarnings("serial")
public abstract class BufferedNotifierContainer extends BufferedProxy implements Container, Container.ItemSetChangeNotifier,
	Container.PropertySetChangeNotifier {
    private List<ItemSetChangeListener> itemSetChangeListeners;

    private List<PropertySetChangeListener> propertySetChangeListeners;

    private class ItemSetChangeEvent extends EventObject implements Container.ItemSetChangeEvent {
	private ItemSetChangeEvent(BufferedNotifierContainer source) {
	    super(source);
	}

	public BufferedNotifierContainer getContainer() {
	    return (BufferedNotifierContainer) getSource();
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

    private class PropertySetChangeEvent extends EventObject implements Container.PropertySetChangeEvent {
	private PropertySetChangeEvent(BufferedNotifierContainer source) {
	    super(source);
	}

	public BufferedNotifierContainer getContainer() {
	    return (BufferedNotifierContainer) getSource();
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
