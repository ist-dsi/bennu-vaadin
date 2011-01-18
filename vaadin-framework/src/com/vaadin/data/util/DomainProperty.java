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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import pt.ist.fenixframework.pstm.AbstractDomainObject;

import com.vaadin.data.Buffered;
import com.vaadin.data.Property;
import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.metamodel.ModelIntroscpectionException;
import com.vaadin.data.util.metamodel.PropertyDescriptor;

/**
 * @author Pedro Santos (pedro.miguel.santos@ist.utl.pt)
 */
@SuppressWarnings("serial")
public class DomainProperty<Type extends AbstractDomainObject> extends BufferedNotifierProperty implements Property,
	Property.ValueChangeNotifier, Property.ReadOnlyStatusChangeNotifier, Buffered {
    protected final DomainItem<Type> item;

    protected final PropertyDescriptor descriptor;

    private Object value;

    private boolean modified = false;

    private Collection<?> possibleValues;

    private final Collection<Validator> validators = new ArrayList<Validator>();

    public DomainProperty(DomainItem<Type> item, PropertyDescriptor descriptor) {
	this.item = item;
	this.descriptor = descriptor;
	this.value = getPersistentValue();
	setWriteThrough(item.isWriteThrough());
	setReadThrough(item.isReadThrough());
    }

    /**
     * @see com.vaadin.data.Property#getValue()
     */
    @Override
    public Object getValue() {
	if (isReadThrough()) {
	    return getPersistentValue();
	}
	return value;
    }

    protected Object getPersistentValue() throws SourceException {
	try {
	    return item.getInstance() != null ? descriptor.read(item.getInstance()) : descriptor.getDefaultValue();
	} catch (ModelIntroscpectionException e) {
	    throw new SourceException(this, e);
	}
    }

    @Override
    public void setValue(Object newValue) throws ReadOnlyException, ConversionException {
	// Checks the mode
	if (isReadOnly()) {
	    throw new Property.ReadOnlyException();
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
	if (isWriteThrough()) {
	    setPersistentValue(newValue);
	} else {
	    value = newValue;
	    modified = true;
	}
	if (!isReadThrough() || isWriteThrough()) {
	    /*
	     * We don't want to notify the listeners if we have only updated the
	     * cached value and they are watching the real value.
	     */
	    fireValueChange();
	}
    }

    protected void setPersistentValue(Object newValue) {
	try {
	    descriptor.write(item.getOrCreateInstance(), newValue);
	} catch (ModelIntroscpectionException e) {
	    throw new SourceException(this, e);
	}
    }

    /**
     * @see com.vaadin.data.Property#getType()
     */
    @Override
    public Class<?> getType() {
	return descriptor.getPropertyType();
    }

    public Collection<Validator> getValidators() {
	return validators;
    }

    public DomainProperty<Type> addValidator(Validator validator) {
	validators.add(validator);
	return this;
    }

    public DomainProperty<Type> removeValidator(Validator validator) {
	validators.remove(validator);
	return this;
    }

    /**
     * @return the possibleValues
     */
    public Collection<?> getPossibleValues() {
	return possibleValues;
    }

    /**
     * @param possibleValues the possibleValues to set
     */
    public void setPossibleValues(Collection<?> possibleValues) {
	this.possibleValues = possibleValues;
    }

    public void setPossibleValues(Object... possibleValues) {
	this.possibleValues = Arrays.asList(possibleValues);
    }

    /**
     * @see com.vaadin.data.Buffered#commit()
     */
    @Override
    public void commit() throws SourceException, InvalidValueException {
	setPersistentValue(value);
    }

    /**
     * @see com.vaadin.data.Buffered#discard()
     */
    @Override
    public void discard() throws SourceException {
	value = getPersistentValue();
    }

    /**
     * @see com.vaadin.data.Buffered#isModified()
     */
    @Override
    public boolean isModified() {
	return modified;
    }

    /**
     * @return the required
     */
    public boolean isRequired() {
	return descriptor.isRequired();
    }

    /**
     * Returns the value of the <code>DomainProperty</code> in human readable
     * textual format. The return value should be assignable to the
     * <code>setValue</code> method if the Property is not in read-only mode.
     * 
     * @return String representation of the value stored in the Property
     */
    @Override
    public String toString() {
	return getValue() != null ? getValue().toString() : null;
    }
}
