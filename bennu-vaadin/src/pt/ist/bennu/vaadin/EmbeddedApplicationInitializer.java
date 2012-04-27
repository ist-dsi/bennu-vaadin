/*
 * @(#)EmbeddedApplicationInitializer.java
 *
 * Copyright 2010 Instituto Superior Tecnico
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
package pt.ist.bennu.vaadin;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.PatternSyntaxException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import pt.ist.bennu.vaadin.errorHandling.ReporterErrorWindow;
import pt.ist.fenixWebFramework.servlets.filters.contentRewrite.RequestChecksumFilter;
import pt.ist.fenixWebFramework.servlets.filters.contentRewrite.RequestChecksumFilter.ChecksumPredicate;
import pt.ist.vaadinframework.EmbeddedApplication;
import pt.ist.vaadinframework.annotation.EmbeddedAnnotationProcessor;
import pt.ist.vaadinframework.annotation.EmbeddedComponent;
import pt.ist.vaadinframework.ui.EmbeddedComponentContainer;
import pt.utl.ist.fenix.tools.util.FileUtils;

@SuppressWarnings("serial")
/**
 * 
 * @author Pedro Santos
 * @author Sérgio Silva
 * @author João Marques
 * @author Luis Cruz
 * 
 */
public class EmbeddedApplicationInitializer extends HttpServlet {
    static {
	RequestChecksumFilter.registerFilterRule(new ChecksumPredicate() {
	    @Override
	    public boolean shouldFilter(HttpServletRequest httpServletRequest) {
		return !httpServletRequest.getRequestURI().endsWith("/vaadinContext.do");
	    }
	});
    }

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
			System.err.printf(
				"WARN: EmbeddedComponentContainer %s is not available in this installation. ignored. \n",
				classname);
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
