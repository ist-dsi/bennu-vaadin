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

public abstract class AbstractViewerFactory implements ViewerFactory {
    public AbstractViewerFactory(ResourceBundle bundle) {
    }

    @Override
    public Viewer createViewer(Item item, Object propertyId, Component uiContext) {
	Viewer viewer = makeViewer(item, propertyId, uiContext);
	AbstractComponent component = (AbstractComponent) viewer;
	component.setCaption(makeCaption(item, propertyId, uiContext));
	component.setDescription(makeDescription(item, propertyId, uiContext));
	return viewer;
    }

    @Override
    public String makeCaption(Item item, Object propertyId, Component uiContext) {
	// if (item instanceof AbstractDomainItem) {
	// String key = ((AbstractDomainItem)
	// item).getLabelKey(bundle,propertyId);
	// if (bundle.containsKey(key)) {
	// return bundle.getString(key);
	// }
	// VaadinFrameworkLogger.getLogger().warn("i18n opportunity missed: " +
	// key);
	// }
	return DefaultFieldFactory.createCaptionByPropertyId(propertyId);
    }

    protected String makeDescription(Item item, Object propertyId, Component uiContext) {
	// if (item instanceof AbstractDomainItem) {
	// String key = ((AbstractDomainItem)
	// item).getDescriptionKey(bundle,propertyId);
	// if (bundle.containsKey(key)) {
	// return bundle.getString(key);
	// }
	// VaadinFrameworkLogger.getLogger().warn("i18n opportunity missed: " +
	// key);
	// }
	return makeCaption(item, propertyId, uiContext);
    }

    protected abstract Viewer makeViewer(Item item, Object propertyId, Component uiContext);
}