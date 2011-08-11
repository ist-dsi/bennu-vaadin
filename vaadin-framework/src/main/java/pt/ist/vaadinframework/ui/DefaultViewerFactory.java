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

import java.util.ResourceBundle;

import pt.ist.bennu.ui.viewers.ViewerFactory;

import com.vaadin.data.Item;
import com.vaadin.data.Property.Viewer;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Label;

public class DefaultViewerFactory implements ViewerFactory {
    private final ResourceBundle bundle;

    public DefaultViewerFactory(ResourceBundle bundle) {
	this.bundle = bundle;
    }

    @Override
    public final Viewer createViewer(Item item, Object propertyId, Component uiContext) {
	Viewer viewer = makeViewer(item, propertyId, uiContext);
	AbstractComponent component = (AbstractComponent) viewer;
	component.setDescription(makeDescription(item, propertyId, uiContext));
	component.setCaption(makeCaption(item, propertyId, uiContext));
	return viewer;
    }

    @Override
    public final String makeCaption(Item item, Object propertyId, Component uiContext) {
	// if (item instanceof AbstractDomainItem) {
	// AbstractDomainItem domainItem = (AbstractDomainItem) item;
	// String key = domainItem.getLabelKey(bundle, propertyId);
	// if (key != null) {
	// return key;
	// }
	// key = domainItem.getType().getName() + "." + propertyId;
	// VaadinFrameworkLogger.getLogger().warn("i18n opportunity missed: " +
	// key);
	// }
	return DefaultFieldFactory.createCaptionByPropertyId(propertyId);
    }

    protected String makeDescription(Item item, Object propertyId, Component uiContext) {
	// if (item instanceof AbstractDomainItem) {
	// AbstractDomainItem domainItem = (AbstractDomainItem) item;
	// String key = domainItem.getDescriptionKey(bundle,propertyId);
	// if (key != null) {
	// return key;
	// }
	// key = domainItem.getType().getName() + "." + propertyId +
	// ".description";
	// VaadinFrameworkLogger.getLogger().warn("i18n opportunity missed: " +
	// key);
	// }
	return makeCaption(item, propertyId, uiContext);
    }

    protected Viewer makeViewer(Item item, Object propertyId, Component uiContext) {
	return new Label();
    }
}