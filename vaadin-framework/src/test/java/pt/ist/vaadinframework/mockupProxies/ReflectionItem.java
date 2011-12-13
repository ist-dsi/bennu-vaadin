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
package pt.ist.vaadinframework.mockupProxies;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

import pt.ist.vaadinframework.data.AbstractBufferedItem;
import pt.ist.vaadinframework.data.BufferedProperty;

import com.vaadin.data.Property;
import com.vaadin.data.util.AbstractProperty;

/**
 * @author Pedro Santos (pedro.miguel.santos@ist.utl.pt)
 */
public class ReflectionItem<Type> extends AbstractBufferedItem<String, Type> {
    public class DescriptorProperty extends AbstractProperty {
	private final PropertyDescriptor descriptor;

	public DescriptorProperty(PropertyDescriptor descriptor) {
	    this.descriptor = descriptor;
	}

	@Override
	public Class<?> getType() {
	    return descriptor.getPropertyType();
	}

	@Override
	public Object getValue() {
	    try {
		Type host = getHost();
		return host != null ? descriptor.getReadMethod().invoke(host) : null;
	    } catch (Throwable e) {
		throw new Error(e);
	    }
	}

	@Override
	public void setValue(Object newValue) throws ReadOnlyException, ConversionException {
	    try {
		descriptor.getWriteMethod().invoke(getHost(), newValue);
	    } catch (Throwable e) {
		throw new Error(e);
	    }
	}

	protected Type getHost() {
	    return ReflectionItem.this.getValue();
	}
    }

    private BeanInfo info;

    public ReflectionItem(Class<? extends Type> type, Hint... hints) {
	super(type, hints);
    }

    public ReflectionItem(Property wrapped, Hint... hints) {
	super(wrapped, hints);
    }

    public ReflectionItem(Type value, Class<? extends Type> type, Hint... hints) {
	super(value, type, hints);
    }

    public ReflectionItem(Type value, Hint... hints) {
	super(value, hints);
    }

    {
	try {
	    info = Introspector.getBeanInfo(getType());
	} catch (IntrospectionException e) {
	    throw new Error();
	}
    }

    @Override
    protected Property makeProperty(String propertyId) {
	for (PropertyDescriptor descriptor : info.getPropertyDescriptors()) {
	    if (descriptor.getName().equals(propertyId)) {
		BufferedProperty<Type> property = new BufferedProperty<Type>(new DescriptorProperty(descriptor));
		addItemProperty(propertyId, property);
		return property;
	    }
	}
	return null;
    }
}
