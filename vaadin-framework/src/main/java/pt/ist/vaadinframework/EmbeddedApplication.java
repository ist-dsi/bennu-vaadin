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
import java.util.Map.Entry;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pt.ist.vaadinframework.ui.EmbeddedComponentContainer;

import com.vaadin.Application;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.UriFragmentUtility;
import com.vaadin.ui.UriFragmentUtility.FragmentChangedEvent;
import com.vaadin.ui.VerticalLayout;
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

    private final UriFragmentUtility fragmentUtility = new UriFragmentUtility();

    private Component current;

    @Override
    public void init() {
	setTheme("reindeer");
	Window window = new Window();
	final VerticalLayout layout = new VerticalLayout();
	layout.addComponent(fragmentUtility);
	fragmentUtility.addListener(new UriFragmentUtility.FragmentChangedListener() {
	    @Override
	    public void fragmentChanged(FragmentChangedEvent source) {
		String fragment = source.getUriFragmentUtility().getFragment();
		for (Entry<Pattern, Class<? extends EmbeddedComponentContainer>> entry : resolver.entrySet()) {
		    Matcher matcher = entry.getKey().matcher(fragment);
		    if (matcher.find()) {
			try {
			    EmbeddedComponentContainer container = entry.getValue().newInstance();
			    Vector<String> arguments = new Vector<String>(matcher.groupCount() + 1);
			    for (int i = 0; i <= matcher.groupCount(); i++) {
				arguments.add(matcher.group(i));
			    }
			    container.setArguments(arguments.toArray(new String[0]));
			    layout.replaceComponent(current, container);
			    current = container;
			    return;
			} catch (InstantiationException e) {
			    VaadinFrameworkLogger.getLogger().error(
				    "Embedded component resolver could not instantiate matched pattern: <"
					    + entry.getKey().pattern() + ", " + entry.getValue().getName() + ">", e);
			} catch (IllegalAccessException e) {
			    VaadinFrameworkLogger.getLogger().error(
				    "Embedded component resolver could not instantiate matched pattern: <"
					    + entry.getKey().pattern() + ", " + entry.getValue().getName() + ">", e);
			}
		    }
		}
		Component container = new NoMatchingPatternFoundComponent();
		layout.replaceComponent(current, container);
		current = container;
	    }
	});
	current = new VerticalLayout();
	layout.addComponent(current);
	window.setContent(layout);
	setMainWindow(window);
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

    private class NoMatchingPatternFoundComponent extends VerticalLayout implements EmbeddedComponentContainer {
	@Override
	public void attach() {
	    super.attach();
	    addComponent(new Label("No matching component found"));
	}

	@Override
	public void setArguments(String... arguments) {
	    // Not expecting any arguments
	}
    }

    /**
     * @return the current
     */
    public Component getCurrent() {
        return current;
    }
}
