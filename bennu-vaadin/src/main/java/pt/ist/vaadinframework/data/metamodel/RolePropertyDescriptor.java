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
package pt.ist.vaadinframework.data.metamodel;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.apache.commons.lang.WordUtils;

import pt.ist.fenixframework.pstm.AbstractDomainObject;

import com.vaadin.data.Property.ConversionException;

import dml.Role;

/**
 * @author Pedro Santos (pedro.miguel.santos@ist.utl.pt)
 * 
 */
public class RolePropertyDescriptor implements PropertyDescriptor {
	private final String propertyId;

	private transient Method reader;

	private transient Method writer;

	private final boolean required;

	// if the relation has * on other type this implies a collection of elements
	// of this type.
	private Class<? extends AbstractDomainObject> elementType;

	private final Role role;

	private final Class<? extends AbstractDomainObject> type;

	private final Class<? extends AbstractDomainObject> returnType;

	public RolePropertyDescriptor(Role role, Class<? extends AbstractDomainObject> type) throws IntrospectionException,
			SecurityException, NoSuchMethodException, ClassNotFoundException {
		this.propertyId = role.getName();
		required = role.getMultiplicityLower() > 0;
		this.role = role;
		this.type = type;
		calc();
		returnType = (Class<? extends AbstractDomainObject>) reader.getReturnType();
	}

	private void calc() throws IntrospectionException, SecurityException, NoSuchMethodException, ClassNotFoundException {
		if (role.getMultiplicityUpper() == 1) {
			java.beans.PropertyDescriptor property = new java.beans.PropertyDescriptor(role.getName(), type);
			reader = property.getReadMethod();
			writer = property.getWriteMethod();
		} else {
			reader = type.getMethod("get" + WordUtils.capitalize(role.getName()) + "Set");
			elementType = (Class<? extends AbstractDomainObject>) Class.forName(role.getType().getFullName());
		}
	}

	/**
	 * @see pt.ist.vaadinframework.data.metamodel.PropertyDescriptor#getPropertyId()
	 */
	@Override
	public String getPropertyId() {
		return propertyId;
	}

	/**
	 * @see pt.ist.vaadinframework.data.metamodel.PropertyDescriptor#getPropertyType()
	 */
	@Override
	public Class<? extends AbstractDomainObject> getPropertyType() {
		return returnType;
	}

	@Override
	public Class<? extends AbstractDomainObject> getCollectionElementType() {
		return elementType;
	}

	/**
	 * @see pt.ist.vaadinframework.data.metamodel.PropertyDescriptor#getDefaultValue()
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
	 * @see pt.ist.vaadinframework.data.metamodel.PropertyDescriptor#isCollection()
	 */
	@Override
	public boolean isCollection() {
		return Collection.class.isAssignableFrom(getPropertyType());
	}

	/**
	 * @see pt.ist.vaadinframework.data.metamodel.PropertyDescriptor#isRequired()
	 */
	@Override
	public boolean isRequired() {
		return required;
	}

	/**
	 * @see pt.ist.vaadinframework.data.metamodel.PropertyDescriptor#read(java.lang.Object)
	 */
	@Override
	public Object read(Object host) throws ModelIntroscpectionException {
		try {
			if (reader == null) {
				calc();
			}
			return reader.invoke(host);
		} catch (Throwable e) {
			throw new ModelIntroscpectionException(e);
		}
	}

	/**
	 * @see pt.ist.vaadinframework.data.metamodel.PropertyDescriptor#write(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void write(Object host, Object newValue) throws ConversionException {
		try {
			if (reader == null) {
				calc();
			}
			if (writer != null) {
				writer.invoke(host, newValue);
			} else {
				Set<?> set = (Set<?>) reader.invoke(host);
				set.clear();
				set.addAll((Collection) newValue);
			}
		} catch (IllegalArgumentException e) {
			throw new ConversionException(e);
		} catch (IllegalAccessException e) {
			throw new ConversionException(e);
		} catch (InvocationTargetException e) {
			throw new ConversionException(e);
		} catch (SecurityException e) {
			throw new ConversionException(e);
		} catch (IntrospectionException e) {
			throw new ConversionException(e);
		} catch (NoSuchMethodException e) {
			throw new ConversionException(e);
		} catch (ClassNotFoundException e) {
			throw new ConversionException(e);
		}
	}

}
