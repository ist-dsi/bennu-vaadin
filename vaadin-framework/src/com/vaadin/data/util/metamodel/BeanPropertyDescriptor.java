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
package com.vaadin.data.util.metamodel;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Pedro Santos (pedro.miguel.santos@ist.utl.pt)
 */
public class BeanPropertyDescriptor extends java.beans.PropertyDescriptor implements PropertyDescriptor {
    private final boolean required;

    public BeanPropertyDescriptor(java.beans.PropertyDescriptor descriptor, boolean required) throws IntrospectionException {
	super(descriptor.getName(), descriptor.getReadMethod(), descriptor.getWriteMethod());
	this.required = required;
    }

    /**
     * @see com.vaadin.data.util.metamodel.PropertyDescriptor#getPropertyId()
     */
    @Override
    public String getPropertyId() {
	return getName();
    }

    /**
     * @see com.vaadin.data.util.metamodel.PropertyDescriptor#getDefaultValue()
     */
    @Override
    public Object getDefaultValue() {
	if (Boolean.class.isAssignableFrom(getPropertyType())) {
	    return Boolean.FALSE;
	}
	return null;
    }

    /**
     * @see com.vaadin.data.util.metamodel.PropertyDescriptor#isCollection()
     */
    @Override
    public boolean isCollection() {
	return false;
    }

    /**
     * @see com.vaadin.data.util.metamodel.PropertyDescriptor#isRequired()
     */
    @Override
    public boolean isRequired() {
	return required;
    }

    /**
     * @see com.vaadin.data.util.metamodel.PropertyDescriptor#read(java.lang.Object)
     */
    @Override
    public Object read(Object host) throws ModelIntroscpectionException {
	try {
	    return getReadMethod().invoke(host);
	} catch (IllegalArgumentException e) {
	    throw new ModelIntroscpectionException(e);
	} catch (IllegalAccessException e) {
	    throw new ModelIntroscpectionException(e);
	} catch (InvocationTargetException e) {
	    throw new ModelIntroscpectionException(e);
	}
    }

    /**
     * @throws ModelIntroscpectionException
     * @see com.vaadin.data.util.metamodel.PropertyDescriptor#write(java.lang.Object,
     *      java.lang.Object)
     */
    @Override
    public void write(Object host, Object newValue) throws ModelIntroscpectionException {
	try {
	    getWriteMethod().invoke(host, newValue);
	} catch (IllegalArgumentException e) {
	    throw new ModelIntroscpectionException(e);
	} catch (IllegalAccessException e) {
	    throw new ModelIntroscpectionException(e);
	} catch (InvocationTargetException e) {
	    throw new ModelIntroscpectionException(e);
	}
    }

}
