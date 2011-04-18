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

import java.io.Serializable;

import com.vaadin.data.Item;
import com.vaadin.data.Property.Viewer;
import com.vaadin.ui.Component;
import com.vaadin.ui.Form;

/**
 * Factory interface for creating new Viewer-instances based on {@link Item},
 * property id and uiContext (the component responsible for displaying viewers).
 * 
 * @author Pedro Santos (pedro.miguel.santos@ist.utl.pt)
 */
public interface ViewerFactory extends Serializable {
    /**
     * Creates a field based on the item, property id and the component (most
     * commonly {@link Form}) where the Field will be presented.
     * 
     * @param item the item where the property belongs to.
     * @param propertyId the Id of the property.
     * @param uiContext the component where the field is presented, most
     *            commonly this is {@link Form}. uiContext will not necessary be
     *            the parent component of the field, but the one that is
     *            responsible for creating it.
     * @return Component the component suitable for viewing the specified data.
     */
    Viewer createViewer(Item item, Object propertyId, Component uiContext);
}
