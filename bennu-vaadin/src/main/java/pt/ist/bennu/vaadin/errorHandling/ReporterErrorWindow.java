/*
 * @(#)ReporterErrorWindow.java
 *
 * Copyright 2011 Instituto Superior Tecnico
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
package pt.ist.bennu.vaadin.errorHandling;

import java.util.Collections;

import pt.ist.bennu.core.applicationTier.Authenticate.UserView;
import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.core.domain.VirtualHost;
import pt.ist.vaadinframework.VaadinFrameworkLogger;
import pt.ist.vaadinframework.VaadinResourceConstants;
import pt.ist.vaadinframework.VaadinResources;
import pt.ist.vaadinframework.terminal.SystemErrorWindow;
import pt.utl.ist.fenix.tools.smtp.EmailSender;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout.MarginHandler;
import com.vaadin.ui.Layout.MarginInfo;
import com.vaadin.ui.Layout.SpacingHandler;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.Window;

/**
 * 
 * @author Pedro Santos
 * 
 */
public class ReporterErrorWindow extends SystemErrorWindow implements VaadinResourceConstants {
    Throwable systemError;

    // private final TextField email;

    private TextArea comment;

    public ReporterErrorWindow() {
    }

    @Override
    protected void setErrorContext(final Throwable systemError) {
	this.systemError = systemError;
	removeAllComponents();
	setCaption(VaadinResources.getString(ERROR_WINDOW_TITLE));
	setModal(true);
	center();
	setBorder(Window.BORDER_NONE);
	setClosable(false);
	setCloseShortcut(KeyCode.ESCAPE);
	setResizable(false);
	setWidth(350, Sizeable.UNITS_PIXELS);
	((MarginHandler) getContent()).setMargin(new MarginInfo(true));
	((SpacingHandler) getContent()).setSpacing(true);
	addComponent(new Label(VaadinResources.getString(ERROR_WINDOW_ANNOUNCEMENT_LABEL)));

	// email = new
	// TextField(VaadinResources.getString(ERROR_WINDOW_EMAIL_LABEL));
	// addComponent(email);

	comment = new TextArea();
	addComponent(comment);
	comment.setInputPrompt(VaadinResources.getString(ERROR_WINDOW_COMMENT_LABEL));
	comment.setSizeFull();
	comment.setRows(6);

	HorizontalLayout response = new HorizontalLayout();
	addComponent(response);
	response.setSpacing(true);

	Button report = new Button(VaadinResources.getString(COMMONS_ACTION_SUBMIT), new ClickListener() {
	    @Override
	    public void buttonClick(ClickEvent event) {
		getApplication().getMainWindow().showNotification(VaadinResources.getString(ERROR_WINDOW_THANKING_LABEL));
		sendEmail();
		ReporterErrorWindow.this.close();
	    }
	});
	response.addComponent(report);
	report.setClickShortcut(KeyCode.ENTER);

	Button ignore = new Button(VaadinResources.getString(COMMONS_ACTION_CANCEL), new ClickListener() {
	    @Override
	    public void buttonClick(ClickEvent event) {
		ReporterErrorWindow.this.close();
	    }
	});
	response.addComponent(ignore);
	// email.setValue(getFromAddress(UserView.getCurrentUser()));
    }

    protected void sendEmail() {
	final User user = UserView.getCurrentUser();

	String fromName = null;
	if (user != null) {
	    fromName = user.getPresentationName() + " (" + user.getUsername() + ")";
	}

	final VirtualHost virtualHost = VirtualHost.getVirtualHostForThread();
	final String supportEmailAddress = virtualHost.getSupportEmailAddress();

	final String subject = "Error: " + systemError.getLocalizedMessage();

	final StringBuilder builder = new StringBuilder();
	builder.append("User Comment: \n");
	builder.append(comment.getValue());
	builder.append("\n\n");

	fillErrorInfo(builder);

	try {
	    EmailSender.send(fromName, null, null, Collections.singleton(supportEmailAddress), null, null, subject,
		    builder.toString());
	} catch (Throwable e) {
	    VaadinFrameworkLogger.getLogger().error("failed to report the exception by email");
	}
    }

    private void fillErrorInfo(final StringBuilder builder) {
	builder.append("Caused by: ");
	builder.append(systemError.getClass().getName());
	builder.append("\n");
	builder.append("   message: ");
	builder.append(systemError.getMessage());
	builder.append("\n");
	builder.append("   localized message: ");
	builder.append(systemError.getLocalizedMessage());
	builder.append("\n");
	builder.append("   stack trace:\n");
	for (final StackTraceElement stackTraceElement : systemError.getStackTrace()) {
	    builder.append(stackTraceElement.toString());
	    builder.append("\n");
	}
	builder.append("\n\n");
    }
}
