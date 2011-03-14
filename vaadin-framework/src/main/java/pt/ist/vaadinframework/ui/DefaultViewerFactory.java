/*
 * Copyright 2011 Instituto Superior Tecnico
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the vaadin-framework-ant.
 *
 *   The vaadin-framework-ant Infrastructure is free software: you can 
 *   redistribute it and/or modify it under the terms of the GNU Lesser General 
 *   Public License as published by the Free Software Foundation, either version 
 *   3 of the License, or (at your option) any later version.*
 *
 *   vaadin-framework-ant is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with vaadin-framework-ant. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package pt.ist.vaadinframework.ui;

import com.vaadin.data.Item;
import com.vaadin.data.Property.Viewer;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;

/**
 * @author Pedro Santos (pedro.miguel.santos@ist.utl.pt)
 * 
 */
public class DefaultViewerFactory implements ViewerFactory {

    /**
     * @see pt.ist.vaadinframework.ui.ViewerFactory#createViewer(com.vaadin.data.Item,
     *      java.lang.Object, com.vaadin.ui.Component)
     */
    @Override
    public Viewer createViewer(Item item, Object propertyId, Component uiContext) {
	return new Label();
    }

}
