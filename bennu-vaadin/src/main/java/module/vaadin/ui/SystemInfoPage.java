/*
 * @(#)SystemInfoPage.java
 *
 * Copyright 2012 Instituto Superior Tecnico
 * Founding Authors: Pedro Santos
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the Bennu-Vadin Integration Module.
 *
 *   The Bennu-Vadin Integration Module is free software: you can
 *   redistribute it and/or modify it under the terms of the GNU Lesser General
 *   Public License as published by the Free Software Foundation, either version 
 *   3 of the License, or (at your option) any later version.
 *
 *   The Bennu-Vadin Module is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with the Bennu-Vadin Module. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package module.vaadin.ui;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.HttpSession;

import pt.ist.bennu.core.applicationTier.Authenticate.UserView;
import pt.ist.bennu.core.domain.RoleType;
import pt.ist.bennu.core.domain.groups.Role;
import pt.ist.vaadinframework.annotation.EmbeddedComponent;
import pt.ist.vaadinframework.ui.EmbeddedComponentContainer;

import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

@EmbeddedComponent(path = "sysinfo")
/**
 * 
 * @author Pedro Santos
 * 
 */
public class SystemInfoPage extends VerticalLayout implements EmbeddedComponentContainer {
    public SystemInfoPage() {
        setSpacing(true);

        final Label status = new Label((String) null, Label.CONTENT_PREFORMATTED);
        addComponent(status);

        Button serialize = new Button("serialize session", new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                try {
                    StringBuilder result = new StringBuilder();
                    HttpSession session = ((WebApplicationContext) getApplication().getContext()).getHttpSession();
                    for (Enumeration att = session.getAttributeNames(); att.hasMoreElements();) {
                        String key = (String) att.nextElement();
                        ByteArrayOutputStream array = new ByteArrayOutputStream();
                        ObjectOutputStream stream = new ObjectOutputStream(array);
                        stream.writeObject(session.getAttribute(key));
                        result.append(key + ":" + array.size() + " bytes\n");
                    }
                    status.setValue(result.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        addComponent(serialize);
    }

    @Override
    public boolean isAllowedToOpen(Map<String, String> arguments) {
        return UserView.getCurrentUser() != null && Role.getRole(RoleType.MANAGER).isMember(UserView.getCurrentUser());
    }

    @Override
    public void setArguments(Map<String, String> arguments) {
    }

}
