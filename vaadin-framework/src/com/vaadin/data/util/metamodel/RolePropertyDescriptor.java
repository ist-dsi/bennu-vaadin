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
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.apache.commons.lang.WordUtils;

import pt.ist.fenixframework.pstm.AbstractDomainObject;
import dml.Role;

/**
 * @author Pedro Santos (pedro.miguel.santos@ist.utl.pt)
 * 
 */
public class RolePropertyDescriptor implements PropertyDescriptor {
    private final String propertyId;

    private Method reader;

    private Method writer;

    private final boolean required;

    // if the relation has * on other type this implies a collection of elements
    // of this type.
    private Class<? extends AbstractDomainObject> elementType;

    public RolePropertyDescriptor(Role role, Class<? extends AbstractDomainObject> type) throws IntrospectionException,
	    SecurityException, NoSuchMethodException, ClassNotFoundException {
	this.propertyId = role.getName();
	if (role.getMultiplicityUpper() == 1) {
	    java.beans.PropertyDescriptor property = new java.beans.PropertyDescriptor(role.getName(), type);
	    reader = property.getReadMethod();
	    writer = property.getWriteMethod();
	} else {
	    reader = type.getMethod("get" + WordUtils.capitalize(role.getName()) + "Set");
	    elementType = (Class<? extends AbstractDomainObject>) Class.forName(role.getType().getFullName());
	}
	required = role.getMultiplicityLower() > 0;
    }

    /**
     * @see com.vaadin.data.util.metamodel.PropertyDescriptor#getPropertyId()
     */
    @Override
    public String getPropertyId() {
	return propertyId;
    }

    /**
     * @see com.vaadin.data.util.metamodel.PropertyDescriptor#getPropertyType()
     */
    @Override
    public Class<? extends AbstractDomainObject> getPropertyType() {
	return (Class<? extends AbstractDomainObject>) reader.getReturnType();
    }

    @Override
    public Class<? extends AbstractDomainObject> getCollectionElementType() {
	return elementType;
    }

    /**
     * @see com.vaadin.data.util.metamodel.PropertyDescriptor#getDefaultValue()
     */
    @Override
    public Object getDefaultValue() {
	if (Boolean.class.isAssignableFrom(getPropertyType())) {
	    return Boolean.FALSE;
	}
	if (Collection.class.isAssignableFrom(getPropertyType())) {
	    return Collections.emptySet();
	}
	return null;
    }

    /**
     * @see com.vaadin.data.util.metamodel.PropertyDescriptor#isCollection()
     */
    @Override
    public boolean isCollection() {
	return Collection.class.isAssignableFrom(getPropertyType());
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
	    return reader.invoke(host);
	} catch (IllegalArgumentException e) {
	    throw new ModelIntroscpectionException(e);
	} catch (IllegalAccessException e) {
	    throw new ModelIntroscpectionException(e);
	} catch (InvocationTargetException e) {
	    throw new ModelIntroscpectionException(e);
	}
    }

    /**
     * @see com.vaadin.data.util.metamodel.PropertyDescriptor#write(java.lang.Object,
     *      java.lang.Object)
     */
    @Override
    public void write(Object host, Object newValue) throws ModelIntroscpectionException {
	try {
	    if (writer != null) {
		writer.invoke(host, newValue);
	    } else {
		Set<?> set = (Set<?>) reader.invoke(host);
		set.clear();
		set.addAll((Collection) newValue);
	    }
	} catch (IllegalArgumentException e) {
	    throw new ModelIntroscpectionException(e);
	} catch (IllegalAccessException e) {
	    throw new ModelIntroscpectionException(e);
	} catch (InvocationTargetException e) {
	    throw new ModelIntroscpectionException(e);
	}
    }

}
