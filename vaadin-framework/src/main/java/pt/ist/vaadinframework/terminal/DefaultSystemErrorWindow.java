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
package pt.ist.vaadinframework.terminal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import pt.ist.vaadinframework.VaadinResourceConstants;
import pt.ist.vaadinframework.VaadinResources;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout.MarginHandler;
import com.vaadin.ui.Layout.MarginInfo;
import com.vaadin.ui.Layout.SpacingHandler;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Window;

/**
 * @author Pedro Santos (pedro.miguel.santos@ist.utl.pt)
 * 
 */
public class DefaultSystemErrorWindow extends SystemErrorWindow implements VaadinResourceConstants {
    private Label stacktrace;

    public DefaultSystemErrorWindow() {
    }

    @Override
    protected void setErrorContext(Throwable systemError) {
	removeAllComponents();
	setCaption(VaadinResources.getString(ERROR_WINDOW_TITLE));
	getContent().setSizeUndefined();
	setModal(true);
	center();
	setBorder(Window.BORDER_NONE);
	setClosable(false);
	setCloseShortcut(KeyCode.ESCAPE);
	setResizable(false);
	((MarginHandler) getContent()).setMargin(new MarginInfo(true));
	((SpacingHandler) getContent()).setSpacing(true);
	addComponent(new Label(VaadinResources.getString(ERROR_WINDOW_ANNOUNCEMENT_LABEL)));

	Panel scroll = new Panel();
	addComponent(scroll);
	scroll.setWidth(450, UNITS_PIXELS);
	scroll.setHeight(450, UNITS_PIXELS);
	stacktrace = new Label();
	scroll.addComponent(stacktrace);
	ByteArrayOutputStream stream = null;
	try {
	    stream = new ByteArrayOutputStream();
	    systemError.printStackTrace(new PrintStream(stream));
	    stacktrace.setValue(stream.toString());
	} finally {
	    if (stream != null) {
		try {
		    stream.close();
		} catch (IOException e) {
		}
	    }
	}
    }
}
