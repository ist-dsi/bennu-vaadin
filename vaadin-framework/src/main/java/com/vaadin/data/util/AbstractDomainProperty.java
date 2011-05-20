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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.EventObject;
import java.util.LinkedList;
import java.util.Set;

import pt.ist.fenixWebFramework.services.Service;
import pt.ist.fenixframework.pstm.AbstractDomainObject;

import com.vaadin.data.Item;
import com.vaadin.data.Property;

/**
 * @author Pedro Santos (pedro.miguel.santos@ist.utl.pt)
 */
public abstract class AbstractDomainProperty implements Property, Property.ValueChangeNotifier,
	Property.ReadOnlyStatusChangeNotifier {
    private boolean readOnly;

    private LinkedList<ValueChangeListener> valueChangeListeners;

    private LinkedList<ReadOnlyStatusChangeListener> readOnlyStatusChangeListeners;

    private AbstractDomainItem host;

    private jvstm.VBox<AbstractDomainObject> value;

    private jvstm.VBox<Set<? extends AbstractDomainObject>> valueSet;

    private final Class<?> type;

    private Instantiator instantiator = null;

    public AbstractDomainProperty(AbstractDomainObject value) {
	initValue(value);
	this.type = value.getClass();
    }

    public AbstractDomainProperty(Class<? extends AbstractDomainObject> type) {
	initValue((AbstractDomainObject) null);
	this.type = type;
    }

    public AbstractDomainProperty(Set<? extends AbstractDomainObject> valueSet) {
	initValue(valueSet);
	this.type = Set.class;
    }

    @Service
    private void initValue(Set<? extends AbstractDomainObject> valueSet) {
	this.valueSet = new jvstm.VBox<Set<? extends AbstractDomainObject>>(valueSet);
    }

    @Service
    public void initValue(AbstractDomainObject host) {
	this.value = new jvstm.VBox<AbstractDomainObject>(host);
    }

    public AbstractDomainProperty(AbstractDomainItem host, Class<?> type) {
	this.host = host;
	this.type = type;
	host.addListener(new ValueChangeListener() {
	    @Override
	    public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {
		fireValueChange();
	    }
	});
    }

    public final AbstractDomainObject getHost() {
	if (value != null) {
	    return value.get();
	}
	return host.getValue();
    }

    protected final AbstractDomainObject getOrCreateHost() {
	if (host != null) {
	    return host.getOrCreateValue();
	}
	if (value.get() == null) {
	    value.put(createNewInstance());
	}
	return value.get();
    }

    protected AbstractDomainObject createNewInstance() {
	if (instantiator != null) {
	    return instantiator.createInstance();
	}
	try {
	    return (AbstractDomainObject) type.newInstance();
	} catch (InstantiationException e) {
	    throw new RuntimeException(e);
	} catch (IllegalAccessException e) {
	    throw new RuntimeException(e);
	}
    }

    public void setInstantiator(Instantiator instantiator) {
	this.instantiator = instantiator;
    }

    /**
     * @see com.vaadin.data.Property#getValue()
     */
    @Override
    public Object getValue() {
	if (value != null) {
	    return value.get() != null ? value.get() : getNullValue();
	}
	if (valueSet != null) {
	    return valueSet.get();
	}
	return host.getValue() != null ? getValueFrom(host.getValue()) : getNullValue();
    }

    /**
     * Getter for the null value of the property. Used when the host is not yet
     * bound to a domain object from which to extract the value.
     * 
     * @return Instance of the same type as the Property's value.
     */
    protected abstract Object getNullValue();

    /**
     * Getter for the property on an existing domain object of the host
     * {@link Item}
     * 
     * @param host The domain object hosting this property.
     * @return Instance of the same type as returned by {@link #getType()}.
     */
    protected abstract Object getValueFrom(AbstractDomainObject host);

    /**
     * @see com.vaadin.data.Property#setValue(java.lang.Object)
     */
    @Override
    @Service
    public void setValue(Object newValue) throws ReadOnlyException, ConversionException {
	if (isReadOnly()) {
	    throw new ReadOnlyException();
	}
	if (newValue != null && !getType().isAssignableFrom(newValue.getClass())) {
	    try {
		try {
		    // try assignment by invoking constructor expecting the new
		    // value's type
		    Constructor<?> constructor = getType().getConstructor(newValue.getClass());
		    newValue = constructor.newInstance(newValue);
		} catch (NoSuchMethodException e) {
		    // try assignment by invoking the String constructor and
		    // passing the toString() of the new value
		    Constructor<?> constructor = getType().getConstructor(String.class);
		    newValue = constructor.newInstance(newValue.toString());
		}
	    } catch (NoSuchMethodException e) {
		throw new Property.ConversionException(e);
	    } catch (IllegalAccessException e) {
		throw new Property.ConversionException(e);
	    } catch (IllegalArgumentException e) {
		throw new Property.ConversionException(e);
	    } catch (InstantiationException e) {
		throw new Property.ConversionException(e);
	    } catch (InvocationTargetException e) {
		throw new Property.ConversionException(e);
	    }
	}
	if (value != null) {
	    value.put((AbstractDomainObject) newValue);
	} else if (valueSet != null) {
	    valueSet.put((Set) newValue);
	} else {
	    setValueOn(getOrCreateHost(), newValue);
	}
	fireValueChange();
    }

    /**
     * Setter for the property on the host domain object, the host will be
     * created if it does not yet exist.
     * 
     * @param host The domain object hosting this property.
     * @param newValue The new value of the property.
     * @throws ConversionException If the new value is not, or cannot be
     *             converted to, the property type.
     */
    protected abstract void setValueOn(AbstractDomainObject host, Object newValue) throws ConversionException;

    /**
     * @see com.vaadin.data.Property#getType()
     */
    @Override
    public Class<?> getType() {
	return type;
    }

    public abstract boolean isRequired();

    /**
     * @see com.vaadin.data.Property#isReadOnly()
     */
    @Override
    public boolean isReadOnly() {
	return readOnly;
    }

    /**
     * @see com.vaadin.data.Property#setReadOnly(boolean)
     */
    @Override
    public void setReadOnly(boolean readOnly) {
	final boolean prevStatus = readOnly;
	this.readOnly = readOnly;
	if (prevStatus != readOnly) {
	    fireReadOnlyStatusChange();
	}
    }

    @Override
    public String toString() {
	return getValue() != null ? getValue().toString() : null;
    }

    private class ValueChangeEvent extends EventObject implements Property.ValueChangeEvent, Property.ReadOnlyStatusChangeEvent {
	protected ValueChangeEvent(AbstractDomainProperty source) {
	    super(source);
	}

	public AbstractDomainProperty getProperty() {
	    return (AbstractDomainProperty) getSource();
	}
    }

    /**
     * @see com.vaadin.data.Property.ValueChangeNotifier#addListener(com.vaadin.data
     *      .Property.ValueChangeListener)
     */
    @Override
    public void addListener(ValueChangeListener listener) {
	if (valueChangeListeners == null) {
	    valueChangeListeners = new LinkedList<ValueChangeListener>();
	}
	valueChangeListeners.add(listener);
    }

    /**
     * @see com.vaadin.data.Property.ValueChangeNotifier#removeListener(com.vaadin
     *      .data.Property.ValueChangeListener)
     */
    @Override
    public void removeListener(ValueChangeListener listener) {
	if (valueChangeListeners != null) {
	    valueChangeListeners.remove(listener);
	}
    }

    /**
     * Sends a value change event to all registered listeners.
     */
    protected void fireValueChange() {
	if (valueChangeListeners != null) {
	    final Object[] l = valueChangeListeners.toArray();
	    final ValueChangeEvent event = new ValueChangeEvent(this);
	    for (int i = 0; i < l.length; i++) {
		((Property.ValueChangeListener) l[i]).valueChange(event);
	    }
	}
    }

    /**
     * @see com.vaadin.data.Property.ReadOnlyStatusChangeNotifier#addListener(com
     *      .vaadin.data.Property.ReadOnlyStatusChangeListener)
     */
    @Override
    public void addListener(ReadOnlyStatusChangeListener listener) {
	if (readOnlyStatusChangeListeners == null) {
	    readOnlyStatusChangeListeners = new LinkedList<ReadOnlyStatusChangeListener>();
	}
	readOnlyStatusChangeListeners.add(listener);
    }

    /**
     * @see com.vaadin.data.Property.ReadOnlyStatusChangeNotifier#removeListener(com.vaadin.data.Property.ReadOnlyStatusChangeListener)
     */
    @Override
    public void removeListener(ReadOnlyStatusChangeListener listener) {
	if (readOnlyStatusChangeListeners != null) {
	    readOnlyStatusChangeListeners.remove(listener);
	}
    }

    /**
     * Sends a read only status change event to all registered listeners.
     */
    protected void fireReadOnlyStatusChange() {
	if (readOnlyStatusChangeListeners != null) {
	    final Object[] l = readOnlyStatusChangeListeners.toArray();
	    final ValueChangeEvent event = new ValueChangeEvent(this);
	    for (int i = 0; i < l.length; i++) {
		((Property.ReadOnlyStatusChangeListener) l[i]).readOnlyStatusChange(event);
	    }
	}
    }
}
