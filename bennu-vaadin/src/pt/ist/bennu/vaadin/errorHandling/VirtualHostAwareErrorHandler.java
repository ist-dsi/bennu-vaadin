package pt.ist.bennu.vaadin.errorHandling;

import java.net.SocketException;

import org.apache.log4j.Logger;

import pt.ist.vaadinframework.ApplicationErrorListener;
import pt.ist.vaadinframework.UserErrorException;
import pt.ist.vaadinframework.VaadinFrameworkLogger;

import com.vaadin.Application;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.terminal.Terminal.ErrorEvent;
import com.vaadin.ui.Window.Notification;

public class VirtualHostAwareErrorHandler implements ApplicationErrorListener {

    @Override
    public void terminalError(final ErrorEvent event, final Application application) {
	System.out.println("handleing....");
	final Logger logger = VaadinFrameworkLogger.getLogger();

	final Throwable throwable = event.getThrowable();
	if (throwable instanceof SocketException) {
	    // Most likely client browser closed socket
	    logger.info("SocketException in CommunicationManager. Most likely client (browser) closed socket.");
	    return;
	}

	if (throwable instanceof InvalidValueException
		|| (throwable.getCause() != null && throwable.getCause() instanceof InvalidValueException)) {
	    // ignore, validation errors are handled by the fields
	} else if (throwable instanceof UserErrorException) {
	    application.getMainWindow().showNotification(throwable.getLocalizedMessage(), Notification.TYPE_ERROR_MESSAGE);
	} else if (throwable.getCause() != null && throwable.getCause() instanceof UserErrorException) {
	    application.getMainWindow().showNotification(throwable.getCause().getLocalizedMessage(), Notification.TYPE_ERROR_MESSAGE);
	} else {
	    final TerminalErrorWindow terminalErrorWindow = createTerminalErrorWindow(event);
	    application.getMainWindow().addWindow(terminalErrorWindow);
	    logger.error("Terminal error:", throwable);
	}
    }

    protected TerminalErrorWindow createTerminalErrorWindow(final ErrorEvent event) {
	return new TerminalErrorWindow(event);
    }

}
