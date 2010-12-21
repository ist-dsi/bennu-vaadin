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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
    private MetaModel(Class<? extends AbstractDomainObject> type) {
	DomainClass clazz = FenixFramework.getDomainModel().findClass(type.getName());
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

    /**
     * @return
     */
    public Collection<PropertyDescriptor> getPropertyDescriptors() {
	return Collections.unmodifiableCollection(descriptors.values());
    }

    /**
     * @param propertyId
     */
    public PropertyDescriptor getPropertyDescriptor(Object propertyId) {
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
