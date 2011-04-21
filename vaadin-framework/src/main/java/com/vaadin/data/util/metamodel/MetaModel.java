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
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.pstm.AbstractDomainObject;
import pt.ist.vaadinframework.VaadinFrameworkLogger;
import dml.DomainClass;
import dml.Role;
import dml.Slot;

/**
 * @author Pedro Santos (pedro.miguel.santos@ist.utl.pt)
 * 
 */
public class MetaModel {
    private static final Map<Class<? extends AbstractDomainObject>, MetaModel> modelCache = new HashMap<Class<? extends AbstractDomainObject>, MetaModel>();

    private final Map<String, PropertyDescriptor> descriptors = new HashMap<String, PropertyDescriptor>();

    /**
     * @param type
     */
    
    private Method getSetMethod(Class<? extends AbstractDomainObject> type, String fieldName) {
	for(Method method : type.getMethods()) {
	    if (method.getName().equals("set" + StringUtils.capitalize(fieldName))) {
		return method;
	    }
	}
	return null;
    }
    
    private MetaModel(Class<? extends AbstractDomainObject> type) {
	for (DomainClass clazz = FenixFramework.getDomainModel().findClass(type.getName()); clazz != null; clazz = (DomainClass) clazz
		.getSuperclass()) {
	    for (Slot slot : clazz.getSlotsList()) {
		try {
		    descriptors.put(slot.getName(), new SlotPropertyDescriptor(slot, type));
		} catch (IntrospectionException e) {
		    VaadinFrameworkLogger.getLogger().error("Failed to create property descriptor for slot: " + slot.getName());
		}
	    }
	    for (Role role : clazz.getRoleSlotsList()) {
		try {
		    descriptors.put(role.getName(), new RolePropertyDescriptor(role, type));
		} catch (SecurityException e) {
		    VaadinFrameworkLogger.getLogger().error("Failed to create property descriptor for role: " + role.getName());
		} catch (IntrospectionException e) {
		    VaadinFrameworkLogger.getLogger().error("Failed to create property descriptor for role: " + role.getName());
		} catch (NoSuchMethodException e) {
		    VaadinFrameworkLogger.getLogger().error("Failed to create property descriptor for role: " + role.getName());
		} catch (ClassNotFoundException e) {
		    VaadinFrameworkLogger.getLogger().error("Failed to create property descriptor for role: " + role.getName());
		}
	    }
	}
	for (Method method : type.getMethods()) {
	    final String methodName = method.getName();
	    String fieldName = StringUtils.uncapitalize(methodName.substring(3,methodName.length()));
	    
	    if (descriptors.get(fieldName) != null) {
		continue;
	    }
	    
	    Method readMethod = null;
	    Method writeMethod = null;
	    
	    if (!methodName.contains("$")) {
		if (methodName.startsWith("get")) {
		    readMethod = method;
		}
		writeMethod = getSetMethod(type, fieldName);
	    }
	    
	    if (readMethod != null && writeMethod != null) {
		java.beans.PropertyDescriptor propertyDescriptor;
		try {
		    propertyDescriptor = new java.beans.PropertyDescriptor(fieldName,readMethod,writeMethod);
		    descriptors.put(fieldName,new BeanPropertyDescriptor(propertyDescriptor, false));
		    System.out.printf("Add fieldName %s of class %s\n", fieldName, type.getName());
		} catch (IntrospectionException e) {
		    VaadinFrameworkLogger.getLogger().error("Failed to create property descriptor for method : " + methodName);
		}
	    }
	}
    }

    /**
     * @return
     */
    public Collection<PropertyDescriptor> getPropertyDescriptors() {
	return Collections.unmodifiableCollection(descriptors.values());
    }

    /**
     * @param propertyId
     */
    public PropertyDescriptor getPropertyDescriptor(String propertyId) {
	if (!descriptors.containsKey(propertyId)) {
	    int dotLocation = propertyId.indexOf('.');
	    PropertyDescriptor descriptor = descriptors.get(propertyId.substring(0, dotLocation));
	    MetaModel model = MetaModel.findMetaModelForType(descriptor.getPropertyType());
	    descriptors.put(propertyId, model.getPropertyDescriptor(propertyId.substring(dotLocation + 1)));
	}
	return descriptors.get(propertyId);
    }

    /**
     * @return
     */
    public Collection<String> getPropertyIds() {
	return Collections.unmodifiableCollection(descriptors.keySet());
    }

    public static MetaModel findMetaModelForType(Class<? extends AbstractDomainObject> type) {
	if (!modelCache.containsKey(type)) {
	    modelCache.put(type, new MetaModel(type));
	}
	return modelCache.get(type);
    }
}
