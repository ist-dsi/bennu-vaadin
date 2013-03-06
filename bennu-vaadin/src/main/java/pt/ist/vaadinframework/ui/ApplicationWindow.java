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
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * @author Pedro Santos (pedro.miguel.santos@ist.utl.pt)
 */
@SuppressWarnings("serial")
public class ApplicationWindow extends Window {
    protected final Property applicationTitle;

    protected final Property applicationSubtitle;

    protected final Property copyright;

    private final VerticalLayout body;

    public ApplicationWindow(String theme, Property applicationTitle, Property applicationSubtitle, Property copyright) {
        setTheme(theme);
        this.applicationTitle = applicationTitle;
        this.applicationSubtitle = applicationSubtitle;
        this.copyright = copyright;
        VerticalLayout main = new VerticalLayout();
        main.setWidth(90, UNITS_PERCENTAGE);
        main.setHeight(98, UNITS_PERCENTAGE);
        main.addStyleName("application-container");

        VerticalLayout header = new VerticalLayout();
        header.setMargin(true, true, false, true);
        header.setSpacing(true);
        main.addComponent(header);
        HorizontalLayout iconAndTitle = new HorizontalLayout();
        iconAndTitle.setSizeFull();
        iconAndTitle.setSpacing(true);
        header.addComponent(iconAndTitle);
        Embedded logo = new Embedded(null, new ThemeResource("../runo/icons/64/globe.png"));
        iconAndTitle.addComponent(logo);
        iconAndTitle.setComponentAlignment(logo, Alignment.MIDDLE_LEFT);

        VerticalLayout titles = new VerticalLayout();
        titles.setSpacing(true);
        iconAndTitle.addComponent(titles);
        iconAndTitle.setExpandRatio(titles, 0.8f);
        Label title = new Label(applicationTitle);
        title.addStyleName("application-title");
        titles.addComponent(title);
        Label subtitle = new Label(applicationSubtitle);
        subtitle.addStyleName("application-subtitle");
        titles.addComponent(subtitle);

        HorizontalLayout controls = new HorizontalLayout();
        controls.setSpacing(true);
        iconAndTitle.addComponent(controls);
        iconAndTitle.setComponentAlignment(controls, Alignment.TOP_RIGHT);
        Label user = new Label("ist148357");
        controls.addComponent(user);
        Link logout = new Link("logout", new ExternalResource("#"));
        controls.addComponent(logout);

        MenuBar menu = new MenuBar();
        menu.addStyleName("application-menu");
        header.addComponent(menu);
        MenuItem hello = menu.addItem("hello", null);
        hello.addItem("sdgjk", new Command() {
            @Override
            public void menuSelected(MenuItem selectedItem) {
                getWindow().showNotification("skjhfgksjdfhglksdjh");
            }
        });
        MenuItem hello1 = menu.addItem("hello", null);
        hello1.addItem("sdgjk", new Command() {
            @Override
            public void menuSelected(MenuItem selectedItem) {
                getWindow().showNotification("skjhfgksjdfhglksdjh");
            }
        });
        MenuItem hello2 = menu.addItem("hello", null);
        hello2.addItem("sdgjk", new Command() {
            @Override
            public void menuSelected(MenuItem selectedItem) {
                getWindow().showNotification("skjhfgksjdfhglksdjh");
            }
        });

        body = new VerticalLayout();
        body.setSizeFull();
        body.setMargin(true);
        body.addStyleName("application-body");
        main.addComponent(body);
        main.setExpandRatio(body, 1f);
        body.addComponent(createDefaultPageBody());

        VerticalLayout footer = new VerticalLayout();
        main.addComponent(footer);
        main.setComponentAlignment(footer, Alignment.MIDDLE_CENTER);
        Label copyrightLabel = new Label(copyright);
        copyrightLabel.setSizeUndefined();
        copyrightLabel.addStyleName("application-footer");
        footer.addComponent(copyrightLabel);
        footer.setComponentAlignment(copyrightLabel, Alignment.MIDDLE_CENTER);

        VerticalLayout outer = (VerticalLayout) getContent();
        outer.setSizeFull();
        outer.addComponent(main);
        outer.setComponentAlignment(main, Alignment.MIDDLE_CENTER);
    }

    // @Override
    // public void attach() {
    // super.attach();
    // CssLayout main = new CssLayout();
    // setContent(main);
    // main.addStyleName("application-container");
    // main.setSizeUndefined();
    // main.setMargin(true);
    //
    // CssLayout header = new CssLayout();
    // main.addComponent(header);
    // header.addStyleName("application-header");
    // header.setSizeUndefined();
    // header.setMargin(true);
    // Label title = new Label(applicationTitle);
    // header.addComponent(title);
    // title.addStyleName("application-title");
    // title.setSizeUndefined();
    // Label subtitle = new Label(applicationSubtitle);
    // header.addComponent(subtitle);
    // subtitle.addStyleName("application-subtitle");
    // subtitle.setSizeUndefined();
    //
    // body = new CssLayout();
    // main.addComponent(body);
    // body.addStyleName("application-body");
    // body.setSizeUndefined();
    // body.setMargin(true);
    //
    // body.addComponent(createDefaultPageBody());
    //
    // CssLayout footer = new CssLayout();
    // main.addComponent(footer);
    // footer.addStyleName("application-footer");
    // footer.setSizeUndefined();
    // footer.setMargin(true);
    // Label copyrightLabel = new Label(copyright);
    // footer.addComponent(copyrightLabel);
    // copyrightLabel.addStyleName("application-copyright");
    // copyrightLabel.setSizeUndefined();
    // }

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
