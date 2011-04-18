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
package pt.ist.vaadinframework.ui.layout;

import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;

/**
 * @author Pedro Santos (pedro.miguel.santos@ist.utl.pt)
 * 
 */
public class FlowLayout extends CssLayout {
    private static final long serialVersionUID = 3515741439654516552L;

    private String separator;

    public FlowLayout() {
	addStyleName("flow");
    }

    public FlowLayout(String separator) {
	this();
	this.separator = separator;
    }

    public void setSeparator(String separator) {
	this.separator = separator;
    }

    public String getSeparator() {
	return separator;
    }

    @Override
    public void addComponent(Component c) {
	if (separator != null && !components.isEmpty()) {
	    addSeparator();
	}
	super.addComponent(c);
	setStyle(c);
    }

    @Override
    public void addComponent(Component c, int index) {
	super.addComponent(c, index);
	setStyle(c);
    }

    @Override
    public void addComponentAsFirst(Component c) {
	super.addComponentAsFirst(c);
	setStyle(c);
    }

    private void addSeparator() {
	Label separatorLabel = new Label(separator);
	separatorLabel.addStyleName("flow-separator");
	separatorLabel.setSizeUndefined();
	super.addComponent(separatorLabel);
    }

    private void setStyle(Component c) {
	c.addStyleName("flow-part");
	c.setSizeUndefined();
    }
}
