//package pt.ist.bennu.vaadin.errorHandling;
//
//import java.net.SocketException;
//import java.util.ArrayList;
//
//import org.apache.log4j.Logger;
//
//import pt.ist.vaadinframework.ApplicationErrorListener;
//import pt.ist.vaadinframework.UserErrorException;
//import pt.ist.vaadinframework.VaadinFrameworkLogger;
//import pt.ist.vaadinframework.terminal.DomainExceptionErrorMessage;
//
//import com.vaadin.Application;
//import com.vaadin.data.Buffered;
//import com.vaadin.data.Buffered.SourceException;
//import com.vaadin.data.Validator.InvalidValueException;
//import com.vaadin.terminal.ErrorMessage;
//import com.vaadin.terminal.ParameterHandler;
//import com.vaadin.terminal.Terminal.ErrorEvent;
//import com.vaadin.terminal.URIHandler;
//import com.vaadin.terminal.VariableOwner;
//import com.vaadin.terminal.gwt.server.ChangeVariablesErrorEvent;
//import com.vaadin.ui.AbstractComponent;
//import com.vaadin.ui.Window.Notification;
//
//public class VirtualHostAwareErrorHandler implements ApplicationErrorListener {
//
//    @Override
//    public void terminalError(final ErrorEvent event, final Application application) {
//	final Logger logger = VaadinFrameworkLogger.getLogger();
//
//	final Throwable throwable = event.getThrowable();
//	if (throwable instanceof SocketException) {
//	    // Most likely client browser closed socket
//	    logger.info("SocketException in CommunicationManager. Most likely client (browser) closed socket.");
//	    return;
//	}
//
//	if (throwable instanceof InvalidValueException
//		|| (throwable.getCause() != null && throwable.getCause() instanceof InvalidValueException)) {
//	    // ignore, validation errors are handled by the fields
//	} else if (throwable instanceof UserErrorException) {
//	    application.getMainWindow().showNotification(throwable.getLocalizedMessage(), Notification.TYPE_ERROR_MESSAGE);
//	} else if (throwable.getCause() != null && throwable.getCause() instanceof UserErrorException) {
//	    application.getMainWindow().showNotification(throwable.getCause().getLocalizedMessage(),
//		    Notification.TYPE_ERROR_MESSAGE);
//	} else if (throwable.getCause() != null && throwable.getCause() instanceof Buffered.SourceException) {
//	    Buffered.SourceException se = (SourceException) throwable.getCause();
//	    ErrorMessage t = null;
//	    for(Throwable c : getAllCauses(se)) {
//		if (c instanceof DomainExceptionErrorMessage) {
//		    t = (ErrorMessage) c;
//		}
//	    }
//	    
//	    Object owner = null;
//	    if (event instanceof VariableOwner.ErrorEvent) {
//		owner = ((VariableOwner.ErrorEvent) event).getVariableOwner();
//	    } else if (event instanceof URIHandler.ErrorEvent) {
//		owner = ((URIHandler.ErrorEvent) event).getURIHandler();
//	    } else if (event instanceof ParameterHandler.ErrorEvent) {
//		owner = ((ParameterHandler.ErrorEvent) event).getParameterHandler();
//	    } else if (event instanceof ChangeVariablesErrorEvent) {
//		owner = ((ChangeVariablesErrorEvent) event).getComponent();
//	    }
//	    if (owner instanceof AbstractComponent) {
//		((AbstractComponent) owner).setComponentError(t);
//	    }
//	} else {
//	    final TerminalErrorWindow terminalErrorWindow = createTerminalErrorWindow(event);
//	    application.getMainWindow().addWindow(terminalErrorWindow);
//	    logger.error("Terminal error:", throwable);
//	}
//    }
//    
//    private ArrayList<Throwable> getAllCauses(Throwable t) {
//	System.out.println("getAllCauses");
//	final ArrayList<Throwable> causes = new ArrayList<Throwable>();
//	causes.add(t);
//	if (t instanceof Buffered.SourceException) {
//	    for(Throwable sec : ((Buffered.SourceException) t).getCauses()) {
//		causes.addAll(getAllCauses(sec));
//	    }
//	} else {
//	    if (t.getCause() != null) {
//		    causes.addAll(getAllCauses(t.getCause()));
//	    }
//	}
//	return causes;
//    }
//
//    protected TerminalErrorWindow createTerminalErrorWindow(final ErrorEvent event) {
//	return new TerminalErrorWindow(event);
//    }
//
//}

package pt.ist.bennu.vaadin.errorHandling;

import java.net.SocketException;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import pt.ist.vaadinframework.ApplicationErrorListener;
import pt.ist.vaadinframework.UserErrorException;
import pt.ist.vaadinframework.VaadinFrameworkLogger;

import com.vaadin.Application;
import com.vaadin.data.Buffered;
import com.vaadin.data.Buffered.SourceException;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.terminal.Terminal.ErrorEvent;
import com.vaadin.ui.Window.Notification;

public class VirtualHostAwareErrorHandler implements ApplicationErrorListener {
    
  private ArrayList<Throwable> getAllCauses(Throwable t) {
	System.out.println("getAllCauses");
	final ArrayList<Throwable> causes = new ArrayList<Throwable>();
	causes.add(t);
	if (t instanceof Buffered.SourceException) {
	    for(Throwable sec : ((Buffered.SourceException) t).getCauses()) {
		causes.addAll(getAllCauses(sec));
	    }
	} else {
	    if (t.getCause() != null) {
		    causes.addAll(getAllCauses(t.getCause()));
	    }
	}
	return causes;
  }
    
    @Override
    public void terminalError(final ErrorEvent event, final Application application) {
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
	} else if (throwable.getCause() != null && throwable.getCause() instanceof Buffered.SourceException) {
	    logger.error("Source error: " + getSourceExceptionDescription((SourceException) throwable.getCause()));
	} else {
	    final TerminalErrorWindow terminalErrorWindow = createTerminalErrorWindow(event);
	    application.getMainWindow().addWindow(terminalErrorWindow);
	    logger.error("Terminal error:", throwable);
	}
    }
    
    public String getSourceExceptionDescription(Buffered.SourceException se) {
	ArrayList<String> messages = new ArrayList<String>();
	for (Throwable t : getAllCauses(se)) {
	    messages.add(t.getLocalizedMessage());
	}
	final String join = StringUtils.join(messages, ",");
	return String.format("[%s]", join);
    }
    
    protected TerminalErrorWindow createTerminalErrorWindow(final ErrorEvent event) {
	return new TerminalErrorWindow(event);
    }

}

