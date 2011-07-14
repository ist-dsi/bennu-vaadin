package pt.ist.bennu.vaadin.errorHandling;

import java.util.Collections;

import module.vaadin.resources.VaadinResourceConstants;
import module.vaadin.resources.VaadinResources;
import myorg.applicationTier.Authenticate.UserView;
import myorg.domain.User;
import myorg.domain.VirtualHost;
import pt.utl.ist.fenix.tools.smtp.EmailSender;

import com.vaadin.Application;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

public class TerminalErrorWindow extends Window implements ClickListener, VaadinResourceConstants {

    private final com.vaadin.terminal.Terminal.ErrorEvent event;

    private final TextField emailAddress = new TextField(VaadinResources.getString(ERRORPAGE_FORM_EMAILADDRESS));
    private final TextField subject = new TextField(VaadinResources.getString(ERRORPAGE_FORM_SUBJECT));
    private final TextArea userDescription = new TextArea(VaadinResources.getString(ERRORPAGE_FORM_USER_DESCRIPTION));

    public TerminalErrorWindow(final com.vaadin.terminal.Terminal.ErrorEvent event) {
	this.event = event;

	setCaption(VaadinResources.getString(ERRORPAGE_FORM_PAGE_TITLE));
	addComponent(new Label(VaadinResources.getString(ERRORPAGE_FORM_CAPTION)));
	setModal(true);
	setWidth(600, UNITS_PIXELS);
	center();

	final AbstractOrderedLayout layout = (AbstractOrderedLayout) getContent();
	layout.setSpacing(true);
	layout.setMargin(true);
    }

    @Override
    public void attach() {
	super.attach();

	final int columns = 45;
	final int rows = 20;

	emailAddress.setColumns(columns);
	final User user = UserView.getCurrentUser();
	emailAddress.setValue(getFromAddress(user));
	addComponent(emailAddress);

	subject.setColumns(columns);
	addComponent(subject);

	userDescription.setColumns(columns);
	userDescription.setRows(rows);
	addComponent(userDescription);

	final Button submit = new Button(VaadinResources.getString(ERRORPAGE_FORM_SUBMIT));
	submit.addListener(this);
	addComponent(submit);
    }

    @Override
    public void buttonClick(final ClickEvent event) {
	sendEmail();

	final Application application = getApplication();
	final Window mainWindow = application.getMainWindow();

	getParent().removeWindow(this);

	mainWindow
	.showNotification(VaadinResources.getString(ERRORPAGE_FORM_SUBMIT_SUCCESS), Notification.TYPE_HUMANIZED_MESSAGE);
    }

    protected String getFromAddress(final User user) {
	return "";
    }

    protected void sendEmail() {
	final User user = UserView.getCurrentUser();

	final String fromName = user.getPresentationName() + " (" + user.getUsername() + ")";
	final String fromAddress = (String) emailAddress.getValue();

	final VirtualHost virtualHost = VirtualHost.getVirtualHostForThread();
	final String supportEmailAddress = virtualHost.getSupportEmailAddress();

	final String subject = "Error: " + this.subject.getValue();

	final StringBuilder builder = new StringBuilder();
	builder.append("User Comment: \n");
	builder.append(userDescription.getValue());
	builder.append("\n\n");

	fillErrorInfo(builder);

	EmailSender.send(fromName, fromAddress, null, Collections.singleton(supportEmailAddress), null, null, subject,
		builder.toString());

    }

    private void fillErrorInfo(final StringBuilder builder) {
	final Throwable cause = event.getThrowable();
	fillErrorInfo(builder, cause);
    }

    private void fillErrorInfo(final StringBuilder builder, final Throwable cause) {
	if (cause != null) {
	    builder.append("Caused by: ");
	    builder.append(cause.getClass().getName());
	    builder.append("\n");
	    builder.append("   message: ");
	    builder.append(cause.getMessage());
	    builder.append("\n");
	    builder.append("   localized message: ");
	    builder.append(cause.getLocalizedMessage());
	    builder.append("\n");
	    builder.append("   stack trace:\n");
	    for (final StackTraceElement stackTraceElement : cause.getStackTrace()) {
		builder.append(stackTraceElement.toString());
		builder.append("\n");
	    }
	    builder.append("\n\n");
	    fillErrorInfo(builder, cause.getCause());
	}
    }

}
