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
package pt.ist.vaadinframework;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import pt.ist.vaadinframework.ui.EmbeddedComponentContainer;

import com.vaadin.Application;
import com.vaadin.ui.Window;

/**
 * <p>
 * Application to manage components embedded in JSPs sharing the same session.
 * This application needs to be configured at startup with the mappings between
 * textual patterns and the {@link EmbeddedComponentContainer}'s that will be
 * attached to the embedded {@link Window}. On embedding a component the host
 * application must set the {@link EmbeddedApplication#VAADIN_PARAM } to a
 * textual parameter that will be matched to the patterns specified, the first
 * pattern to match will define the {@link EmbeddedComponentContainer} that will
 * be instantiated. The parameter will also be sent to the newly instantiated
 * container.
 * </p>
 * 
 * <p>
 * For example: Lets say we have a component that displays information about a
 * Product given its ID. First add the pattern to the resolver by doing:
 * 
 * <pre>{@code EmbeddedApplication.addResolutionPattern(Pattern.compile("product-(.*)"), ProductContainer.class);}</pre>
 * 
 * In the host application set the {@link EmbeddedApplication#VAADIN_PARAM }
 * like:
 * 
 * <pre>{@code request.getSession().setAttribute(EmbeddedApplication.VAADIN_PARAM, "product-10042786");}</pre>
 * 
 * As you defined a group in the pattern the group will be sent as an argument
 * to {@link EmbeddedComponentContainer#setArguments(String...)}.
 * </p>
 * 
 * To use this feature client applications have to do to following:
 * 
 * <li>
 * <ul>
 * Add EmbeddedApplication servlet to web.xml:
 * 
 * <pre>
 * {@code
 * <servlet>
 *   <servlet-name>VaadinEmbedded</servlet-name>
 *   <servlet-class>com.vaadin.terminal.gwt.server.ApplicationServlet</servlet-class>
 *   <init-param>
 *     <description>Vaadin Embedded Application class</description>
 *     <param-name>application</param-name>
 *     <param-value>pt.ist.vaadinframework.EmbeddedApplication</param-value>
 *   </init-param>
 * </servlet>
 * 
 * <servlet-mapping>
 *   <servlet-name>VaadinEmbedded</servlet-name>
 *     <url-pattern>/vaadin/*</url-pattern>
 * </servlet-mapping>
 * }
 * </pre>
 * </ul>
 * <ul>
 * 
 * Then redirect to a jsp with the following code:
 * 
 * <pre>
 * {@code
 * <script type="text/javascript">
 *   var vaadin = { vaadinConfigurations: { 'vaadin': {appUri:'<%= request.getContextPath() + "/vaadin" %>', pathInfo: '/', themeUri:'<%= request.getContextPath() + "/VAADIN/themes/default" %>'}}};
 * </script>
 * <script language='javascript' src='<%= request.getContextPath() + "/VAADIN/widgetsets/com.vaadin.terminal.gwt.DefaultWidgetSet/com.vaadin.terminal.gwt.DefaultWidgetSet.nocache.js" %>'></script>
 * <div id="vaadin"/>
 * }
 * </pre>
 * </ul>
 * </li>
 * 
 * @author Pedro Santos (pedro.miguel.santos@ist.utl.pt)
 * @version 1.0
 */
@SuppressWarnings("serial")
public class EmbeddedApplication extends Application {
    private static final Map<Pattern, Class<? extends EmbeddedComponentContainer>> resolver = new HashMap<Pattern, Class<? extends EmbeddedComponentContainer>>();

    @Override
    public void init() {
	setTheme("reindeer");
	setMainWindow(new EmbeddedWindow(resolver));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.Application#getWindow(java.lang.String)
     */
    @Override
    public Window getWindow(String name) {
	// If the window is identified by name, we are good to go
	Window window = super.getWindow(name);

	// If not, we must create a new window for this new browser window/tab
	if (window == null) {
	    window = new EmbeddedWindow(resolver);

	    // Use the random name given by the framework to identify this
	    // window in future
	    window.setName(name);
	    addWindow(window);

	    // Move to the url to remember the name in the future
	    // window.open(new ExternalResource(window.getURL()));
	}
	return window;
    }

    /**
     * Adds a new pattern to the resolver, the pattern can have groups that when
     * captured will be supplied to the corresponding
     * {@link EmbeddedComponentContainer} using
     * {@link EmbeddedComponentContainer#setArguments(String...)}.
     * 
     * @param pattern The compiled {@link Pattern} instance.
     * @param type The container that will be instantiated if the pattern
     *            matches the {@link EmbeddedApplication#VAADIN_PARAM}.
     */
    public static void addResolutionPattern(Pattern pattern, Class<? extends EmbeddedComponentContainer> type) {
	resolver.put(pattern, type);
    }

    // /**
    // * @see
    // com.vaadin.Application#terminalError(com.vaadin.terminal.Terminal.ErrorEvent)
    // */
    // @Override
    // public void terminalError(com.vaadin.terminal.Terminal.ErrorEvent event)
    // {
    // final Throwable t = event.getThrowable();
    // if (t instanceof SocketException) {
    // // Most likely client browser closed socket
    // VaadinFrameworkLogger.getLogger().error(
    // "Warning: SocketException in CommunicationManager. Most likely client (browser) closed socket.",
    // t);
    // return;
    // }
    //
    // // // Finds the original source of the error/exception
    // // Object owner = null;
    // // if (event instanceof VariableOwner.ErrorEvent) {
    // // owner = ((VariableOwner.ErrorEvent) event).getVariableOwner();
    // // } else if (event instanceof URIHandler.ErrorEvent) {
    // // owner = ((URIHandler.ErrorEvent) event).getURIHandler();
    // // } else if (event instanceof ParameterHandler.ErrorEvent) {
    // // owner = ((ParameterHandler.ErrorEvent) event).getParameterHandler();
    // // } else if (event instanceof ChangeVariablesErrorEvent) {
    // // owner = ((ChangeVariablesErrorEvent) event).getComponent();
    // // }
    // //
    // // // Shows the error in AbstractComponent
    // // if (owner instanceof AbstractComponent) {
    // // if (t instanceof ErrorMessage) {
    // // ((AbstractComponent) owner).setComponentError((ErrorMessage) t);
    // // } else {
    // // ((AbstractComponent) owner).setComponentError(new SystemError(t));
    // // }
    // // }
    // VaadinFrameworkLogger.getLogger().error("Unhandled exception", t);
    // getMainWindow().showNotification("An Unexpected error occured!",
    // t.toString(), Notification.TYPE_ERROR_MESSAGE);
    //
    // }
}
