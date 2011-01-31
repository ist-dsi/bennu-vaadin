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

import com.vaadin.data.Item;

/**
 * @author Pedro Santos (pedro.miguel.santos@ist.utl.pt)
 */
@SuppressWarnings("serial")
public abstract class BufferedNotifierItem extends BufferedProxy implements Item, Item.PropertySetChangeNotifier {
    private List<PropertySetChangeListener> propertySetChangeListeners;

    private class PropertySetChangeEvent extends EventObject implements Item.PropertySetChangeEvent {
	private PropertySetChangeEvent(BufferedNotifierItem source) {
	    super(source);
	}

	public BufferedNotifierItem getItem() {
	    return (BufferedNotifierItem) getSource();
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

}
