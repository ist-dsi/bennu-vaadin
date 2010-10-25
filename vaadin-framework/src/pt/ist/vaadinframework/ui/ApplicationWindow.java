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

import com.vaadin.data.Property;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;

/**
 * @author Pedro Santos (pedro.miguel.santos@ist.utl.pt)
 */
@SuppressWarnings("serial")
public class ApplicationWindow extends Window {
    protected final Property applicationTitle;

    protected final Property applicationSubtitle;

    protected final Property copyright;

    private CssLayout body;

    public ApplicationWindow(String theme, Property applicationTitle, Property applicationSubtitle, Property copyright) {
	setTheme(theme);
	this.applicationTitle = applicationTitle;
	this.applicationSubtitle = applicationSubtitle;
	this.copyright = copyright;
    }

    @Override
    public void attach() {
	super.attach();
	CssLayout main = new CssLayout();
	setContent(main);
	main.addStyleName("application-container");
	main.setSizeUndefined();
	main.setMargin(true);

	CssLayout header = new CssLayout();
	main.addComponent(header);
	header.addStyleName("application-header");
	header.setSizeUndefined();
	header.setMargin(true);
	Label title = new Label(applicationTitle);
	header.addComponent(title);
	title.addStyleName("application-title");
	title.setSizeUndefined();
	Label subtitle = new Label(applicationSubtitle);
	header.addComponent(subtitle);
	subtitle.addStyleName("application-subtitle");
	subtitle.setSizeUndefined();

	body = new CssLayout();
	main.addComponent(body);
	body.addStyleName("application-body");
	body.setSizeUndefined();
	body.setMargin(true);

	body.addComponent(createDefaultPageBody());

	CssLayout footer = new CssLayout();
	main.addComponent(footer);
	footer.addStyleName("application-footer");
	footer.setSizeUndefined();
	footer.setMargin(true);
	Label copyrightLabel = new Label(copyright);
	footer.addComponent(copyrightLabel);
	copyrightLabel.addStyleName("application-copyright");
	copyrightLabel.setSizeUndefined();
    }

    protected Component createDefaultPageBody() {
	HorizontalLayout layout = new HorizontalLayout();
	layout.addComponent(new Label("Welcome to "));
	layout.addComponent(new Label(applicationTitle));
	layout.addComponent(new Label(" application"));
	return layout;
    }

    public void setPage(Component component) {
	body.removeAllComponents();
	body.addComponent(component);
    }
}
