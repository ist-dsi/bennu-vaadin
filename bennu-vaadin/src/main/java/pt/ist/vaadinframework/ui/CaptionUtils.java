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
package pt.ist.vaadinframework.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;

import pt.ist.vaadinframework.VaadinFrameworkLogger;
import pt.ist.vaadinframework.data.AbstractBufferedContainer;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;

/**
 * @author Sérgio Silva (sergio.silva@ist.utl.pt)
 * 
 */

public class CaptionUtils {
    public static String makeCaption(ResourceBundle bundle, Container container, Object propertyId, Component uiContext) {
	if (container instanceof AbstractBufferedContainer) {
	    return makeCaption(bundle, ((AbstractBufferedContainer<?, ?, ?>) container).getElementType(), propertyId, uiContext);
	}
	return DefaultFieldFactory.createCaptionByPropertyId(propertyId);
    }

    public static String makeCaption(ResourceBundle bundle, Item item, Object propertyId, Component uiContext) {
	if (item instanceof Property) {
	    return makeCaption(bundle, ((Property) item).getType(), propertyId, uiContext);
	}
	return DefaultFieldFactory.createCaptionByPropertyId(propertyId);
    }

    private static String makeCaption(ResourceBundle bundle, Class<?> type, Object propertyId, Component uiContext) {
	String key = getBundleKey(bundle, type, propertyId, StringUtils.EMPTY);
	if (bundle.containsKey(key)) {
	    return bundle.getString(key);
	}
	VaadinFrameworkLogger.getLogger().warn("i18n opportunity missed: " + key);
	return DefaultFieldFactory.createCaptionByPropertyId(propertyId);
    }

    public static String makeDescription(ResourceBundle bundle, Container container, Object propertyId, Component uiContext) {
	if (container instanceof AbstractBufferedContainer) {
	    return makeDescription(bundle, ((AbstractBufferedContainer<?, ?, ?>) container).getElementType(), propertyId,
		    uiContext);
	}
	return makeCaption(bundle, container, propertyId, uiContext);
    }

    public static String makeDescription(ResourceBundle bundle, Item item, Object propertyId, Component uiContext) {
	if (item instanceof Property) {
	    return makeDescription(bundle, ((Property) item).getType(), propertyId, uiContext);
	}
	return makeCaption(bundle, item, propertyId, uiContext);
    }

    public static String makeDescription(ResourceBundle bundle, Class<?> type, Object propertyId, Component uiContext) {
	String key = getBundleKey(bundle, type, propertyId, ".description");
	if (bundle.containsKey(key)) {
	    return bundle.getString(key);
	}
	VaadinFrameworkLogger.getLogger().warn("i18n opportunity missed: " + key);
	return makeCaption(bundle, type, propertyId, uiContext);
    }

    private static String getBundleKey(ResourceBundle bundle, Class<?> clazz, Object propertyId, String suffix) {
	return getBundleKey(bundle, new ArrayList<String>(), clazz, propertyId, suffix);
    }

    private static String getBundleKey(ResourceBundle bundle, List<String> missed, Class<?> clazz, Object propertyId,
	    String suffix) {
	String key = clazz.getName() + "." + propertyId + suffix;
	if (bundle.containsKey(key)) {
	    return key;
	}
	missed.add(clazz.getName() + "." + propertyId + suffix);
	if (clazz.getSuperclass() == null) {
	    return StringUtils.join(missed, " or ");
	}
	return getBundleKey(bundle, missed, clazz.getSuperclass(), propertyId, suffix);
    }
}
