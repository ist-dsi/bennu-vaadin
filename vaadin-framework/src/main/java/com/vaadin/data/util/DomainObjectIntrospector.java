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

import java.beans.FeatureDescriptor;
import java.beans.IntrospectionException;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.WordUtils;

import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.pstm.AbstractDomainObject;
import pt.ist.vaadinframework.VaadinFrameworkLogger;
import dml.DomainClass;
import dml.DomainModel;
import dml.Role;
import dml.Slot;
import dml.Slot.Option;

/**
 * @author Pedro Santos (pedro.miguel.santos@ist.utl.pt)
 * 
 */
public class DomainObjectIntrospector {
    public static class DmlPropertyDescriptor extends PropertyDescriptor {
	private final boolean required;

	public DmlPropertyDescriptor(Slot slot, Class<? extends AbstractDomainObject> type) throws IntrospectionException {
	    super(slot.getName(), type);
	    this.required = slot.getOptions().contains(Option.REQUIRED);
	}

	public DmlPropertyDescriptor(Role role, Class<? extends AbstractDomainObject> type) throws IntrospectionException {
	    super(role.getName(), type);
	    this.required = role.getMultiplicityLower() == 1 && role.getMultiplicityUpper() == 1;
	}

	public boolean isRequired() {
	    return required;
	}
    }

    public static class RelationDescriptor extends MethodDescriptor {
	public RelationDescriptor(Role role, Class<?> type) throws SecurityException, NoSuchMethodException {
	    super(type.getMethod("get" + WordUtils.capitalize(role.getName()) + "Set"));
	}
    }

    public static Map<String, FeatureDescriptor> introspect(Class<? extends AbstractDomainObject> type) {
	Map<String, FeatureDescriptor> descriptors = new HashMap<String, FeatureDescriptor>();
	DomainModel model = FenixFramework.getDomainModel();
	DomainClass clazz = model.findClass(type.getName());
	for (Slot slot : clazz.getSlotsList()) {
	    try {
		descriptors.put(slot.getName(), new DmlPropertyDescriptor(slot, type));
	    } catch (IntrospectionException e) {
		VaadinFrameworkLogger.getLogger().error("Failed to create property descriptor for slot: " + slot.getName());
	    }
	}
	for (Role role : clazz.getRoleSlotsList()) {
	    try {
		if (role.getMultiplicityUpper() == 0 || role.getMultiplicityUpper() == 1) {
		    descriptors.put(role.getName(), new DmlPropertyDescriptor(role, type));
		} else {
		    descriptors.put(role.getName(), new RelationDescriptor(role, type));
		}
	    } catch (IntrospectionException e) {
		VaadinFrameworkLogger.getLogger().error("Failed to create property descriptor for role: " + role.getName());
	    } catch (SecurityException e) {
		VaadinFrameworkLogger.getLogger().error("Failed to create property descriptor for role: " + role.getName());
	    } catch (NoSuchMethodException e) {
		VaadinFrameworkLogger.getLogger().error("Failed to create property descriptor for role: " + role.getName());
	    }
	}
	return descriptors;
    }
}
