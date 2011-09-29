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
package pt.ist.vaadinframework.ui;

import java.util.Map;

import pt.ist.vaadinframework.EmbeddedApplication;

import com.vaadin.ui.ComponentContainer;

/**
 * Interface for {@link ComponentContainer}s that can serve as embedded
 * components in {@link EmbeddedApplication} settings.
 * 
 * @author Pedro Santos (pedro.miguel.santos@ist.utl.pt)
 * @version 1.0
 */
public interface EmbeddedComponentContainer extends ComponentContainer {
    /**
     * Method that will be called with the parsed parameters by the resolver.
     * You can assume that this configuration method will be called before the
     * component is attached to the window.
     * 
     * @param arguments
     *            the parsed arguments passed to the container including the
     *            full parameter text.
     */
    public void setArguments(java.util.Map<String,String> arguments);
}
