package pt.ist.bennu.vaadin;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.PatternSyntaxException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import pt.ist.bennu.vaadin.errorHandling.ReporterErrorWindow;
import pt.ist.vaadinframework.EmbeddedApplication;
import pt.ist.vaadinframework.annotation.EmbeddedAnnotationProcessor;
import pt.ist.vaadinframework.annotation.EmbeddedComponent;
import pt.ist.vaadinframework.ui.EmbeddedComponentContainer;
import pt.utl.ist.fenix.tools.util.FileUtils;

@SuppressWarnings("serial")
public class EmbeddedApplicationInitializer extends HttpServlet {
    private static final Set<Class<? extends EmbeddedComponentContainer>> embeddedComponentClasses = new HashSet<Class<? extends EmbeddedComponentContainer>>();

    @Override
    public void init(ServletConfig config) throws ServletException {
	super.init(config);

	loadEmbeddedComponentsFromFile(embeddedComponentClasses);
	for (Class<? extends EmbeddedComponentContainer> embeddedComponentClass : embeddedComponentClasses) {
	    EmbeddedComponent embeddedComponent = embeddedComponentClass.getAnnotation(EmbeddedComponent.class);
	    if (embeddedComponent == null) {
		continue;
	    }

	    String[] paths = embeddedComponent.path();

	    for (String path : paths) {
		try {
		    EmbeddedApplication.addPage(embeddedComponentClass);
		} catch (PatternSyntaxException e) {
		    throw new Error("Error interpreting pattern: " + path, e);
		}
	    }
	}

	EmbeddedApplication.registerErrorWindow(new ReporterErrorWindow());
    }

    private void loadEmbeddedComponentsFromFile(final Set<Class<? extends EmbeddedComponentContainer>> embeddedComponentClasses) {
	final InputStream inputStream = this.getClass().getResourceAsStream("/" + EmbeddedAnnotationProcessor.LOG_FILENAME);
	if (inputStream != null) {
	    try {
		final String contents = FileUtils.readFile(inputStream);
		for (final String classname : contents.split(EmbeddedAnnotationProcessor.ENTRY_SEPERATOR)) {
		    try {
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			Class<? extends EmbeddedComponentContainer> type = (Class<? extends EmbeddedComponentContainer>) loader
				.loadClass(classname);
			embeddedComponentClasses.add(type);
		    } catch (final ClassNotFoundException e) {
			e.printStackTrace();
		    }
		}
	    } catch (final IOException e) {
		e.printStackTrace();
	    }
	} else {
	    throw new Error("Error opening file: " + EmbeddedAnnotationProcessor.LOG_FILENAME);
	}
    }
}
