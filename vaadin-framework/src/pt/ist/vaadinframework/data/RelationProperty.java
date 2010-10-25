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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import pt.ist.fenixframework.pstm.AbstractDomainObject;

import com.vaadin.data.Property;

/**
 * @author Pedro Santos (pedro.miguel.santos@ist.utl.pt)
 * 
 */
@SuppressWarnings("serial")
public class RelationProperty<Type extends AbstractDomainObject> implements Property, Property.ValueChangeNotifier,
	Property.ReadOnlyStatusChangeNotifier {

    public static class MethodException extends RuntimeException {
	public MethodException(Throwable e) {
	    super(e);
	}
    }

    protected static final Logger LOGGER = Logger.getLogger(RelationProperty.class.getName());

    private transient Type instance;

    private transient Method getMethod;

    private transient Class<?> type;

    private boolean readOnly;

    private List<ValueChangeListener> valueChangeListeners;

    private List<ReadOnlyStatusChangeListener> readOnlyStatusChangeListeners;

    public RelationProperty(Type instance, String beanPropertyName) {
	this.instance = instance;
	final Class<?> beanClass = instance.getClass();

	// Assure that the first letter is upper cased (it is a common
	// mistake to write firstName, not FirstName).
	if (Character.isLowerCase(beanPropertyName.charAt(0))) {
	    final char[] buf = beanPropertyName.toCharArray();
	    buf[0] = Character.toUpperCase(buf[0]);
	    beanPropertyName = new String(buf);
	}

	// Find the get method
	getMethod = null;
	try {
	    getMethod = beanClass.getMethod("get" + beanPropertyName + "Set", new Class[] {});
	} catch (final NoSuchMethodException e) {
	    LOGGER.error(
		    "unable to find getter method for collection property " + beanPropertyName + " at class "
			    + beanClass.getName(), e);

	    throw new MethodException(e);
	}

	// In case the get method is found, resolve the type
	type = getMethod.getReturnType();
    }

    /**
     * @see com.vaadin.data.Property#getValue()
     */
    @Override
    public Object getValue() {
	try {
	    return getMethod.invoke(instance);
	} catch (final Throwable e) {
	    throw new MethodException(e);
	}
    }

    /**
     * @see com.vaadin.data.Property#setValue(java.lang.Object)
     */
    @Override
    public void setValue(Object newValue) throws ReadOnlyException, ConversionException {
	// Checks the mode
	if (isReadOnly()) {
	    throw new Property.ReadOnlyException();
	}

	// Try to assign the compatible value directly
	if (newValue == null || type.isAssignableFrom(newValue.getClass())) {
	    try {
		List<Type> list = (List<Type>) getMethod.invoke(instance);
		list.clear();
		list.addAll((Collection<Type>) newValue);
	    } catch (IllegalArgumentException e) {
		throw new MethodException(e);
	    } catch (IllegalAccessException e) {
		throw new MethodException(e);
	    } catch (InvocationTargetException e) {
		throw new MethodException(e);
	    }
	} else {
	    throw new Property.ConversionException();
	}
	fireValueChange();
    }

    /**
     * @see com.vaadin.data.Property#getType()
     */
    @Override
    public Class<?> getType() {
	return type;
    }

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
    public void setReadOnly(boolean newStatus) {
	final boolean prevStatus = readOnly;
	readOnly = newStatus;
	if (prevStatus != readOnly) {
	    fireReadOnlyStatusChange();
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

    private void fireReadOnlyStatusChange() {
	if (readOnlyStatusChangeListeners != null) {
	    final Object[] l = readOnlyStatusChangeListeners.toArray();
	    final Property.ReadOnlyStatusChangeEvent event = new ReadOnlyStatusChangeEvent(this);
	    for (int i = 0; i < l.length; i++) {
		((Property.ReadOnlyStatusChangeListener) l[i]).readOnlyStatusChange(event);
	    }
	}
    }

    /**
     * Sends a value change event to all registered listeners.
     */
    public void fireValueChange() {
	if (valueChangeListeners != null) {
	    final Object[] l = valueChangeListeners.toArray();
	    final Property.ValueChangeEvent event = new ValueChangeEvent(this);
	    for (int i = 0; i < l.length; i++) {
		((Property.ValueChangeListener) l[i]).valueChange(event);
	    }
	}
    }

    /**
     * An <code>Event</code> object specifying the Property whose read-only
     * status has been changed.
     * 
     * @author IT Mill Ltd.
     * @version 6.4.5
     * @since 3.0
     */
    private class ReadOnlyStatusChangeEvent extends java.util.EventObject implements Property.ReadOnlyStatusChangeEvent {

	/**
	 * Constructs a new read-only status change event for this object.
	 * 
	 * @param source
	 *            source object of the event.
	 */
	protected ReadOnlyStatusChangeEvent(RelationProperty<Type> source) {
	    super(source);
	}

	/**
	 * Gets the Property whose read-only state has changed.
	 * 
	 * @return source Property of the event.
	 */
	public Property getProperty() {
	    return (Property) getSource();
	}

    }

    /**
     * An <code>Event</code> object specifying the Property whose value has been
     * changed.
     * 
     * @author IT Mill Ltd.
     * @version 6.4.5
     * @since 5.3
     */
    private class ValueChangeEvent extends java.util.EventObject implements Property.ValueChangeEvent {

	/**
	 * Constructs a new value change event for this object.
	 * 
	 * @param source
	 *            source object of the event.
	 */
	protected ValueChangeEvent(RelationProperty<Type> source) {
	    super(source);
	}

	/**
	 * Gets the Property whose value has changed.
	 * 
	 * @return source Property of the event.
	 */
	public Property getProperty() {
	    return (Property) getSource();
	}

    }
}
