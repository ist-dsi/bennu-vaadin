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

import java.lang.reflect.Field;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.regex.Pattern;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import pt.ist.bennu.core.applicationTier.Authenticate.UserView;
import pt.ist.bennu.core.domain.VirtualHost;
import pt.ist.bennu.core.domain.contents.Node;
import pt.ist.bennu.vaadin.domain.contents.VaadinNode;
import pt.ist.fenixWebFramework.servlets.filters.SetUserViewFilter;
import pt.ist.fenixframework.FFDomainException;
import pt.ist.vaadinframework.annotation.EmbeddedComponentUtils;
import pt.ist.vaadinframework.fragment.FragmentQuery;
import pt.ist.vaadinframework.terminal.DefaultSystemErrorWindow;
import pt.ist.vaadinframework.terminal.DomainExceptionErrorMessage;
import pt.ist.vaadinframework.terminal.SystemErrorWindow;
import pt.ist.vaadinframework.ui.EmbeddedComponentContainer;
import pt.utl.ist.fenix.tools.util.i18n.Language;

import com.vaadin.Application;
import com.vaadin.data.Buffered;
import com.vaadin.data.Buffered.SourceException;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.service.ApplicationContext.TransactionListener;
import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Form;
import com.vaadin.ui.Window;

/**
 * <p>
 * Application to manage components embedded in JSPs sharing the same session. This application needs to be configured at startup
 * with the mappings between textual patterns and the {@link EmbeddedComponentContainer}'s that will be attached to the embedded
 * {@link Window}. On embedding a component the hostItem application must set the {@link EmbeddedApplication#VAADIN_PARAM } to a
 * textual parameter that will be matched to the patterns specified, the first pattern to match will define the
 * {@link EmbeddedComponentContainer} that will be instantiated. The parameter will also be sent to the newly instantiated
 * container.
 * </p>
 * 
 * <p>
 * For example: Lets say we have a component that displays information about a Product given its ID. First add the pattern to the
 * resolver by doing:
 * 
 * <pre>
 * {@code EmbeddedApplication.addResolutionPattern(Pattern.compile("product-(.*)"), ProductContainer.class);}
 * </pre>
 * 
 * In the hostItem application set the {@link EmbeddedApplication#VAADIN_PARAM } like:
 * 
 * <pre>
 * {@code request.getSession().setAttribute(EmbeddedApplication.VAADIN_PARAM, "product-10042786");}
 * </pre>
 * 
 * As you defined a group in the pattern the group will be sent as an argument to
 * {@link EmbeddedComponentContainer#setArguments(String...)}.
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
 * 
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
 * 
 * </ul>
 * </li>
 * 
 * @author Pedro Santos (pedro.miguel.santos@ist.utl.pt)
 * @version 1.0
 */
@SuppressWarnings("serial")
public class EmbeddedApplication extends Application implements VaadinResourceConstants {
	private static final Map<String, Class<? extends EmbeddedComponentContainer>> pages = new HashMap<>();

	private static SystemErrorWindow errorWindow = new DefaultSystemErrorWindow();

	@Override
	public void init() {
		getContext().addTransactionListener(new TransactionListener() {
			@Override
			public void transactionStart(Application application, Object transactionData) {
				application.setLocale(Language.getLocale());
			}

			@Override
			public void transactionEnd(Application application, Object transactionData) {
				application.setLocale(null);
			}
		});
		setTheme(VirtualHost.getVirtualHostForThread().getTheme().getName());
		setMainWindow(new EmbeddedWindow());
	}

	public static void open(Application application, Class<? extends EmbeddedComponentContainer> clazz, String... args) {
		((EmbeddedApplication) application).open(clazz, args);
	}

	public static void open(Application application, String fragment) {
		((EmbeddedApplication) application).open(fragment);
	}

	public void open(Class<? extends EmbeddedComponentContainer> clazz, String... args) {
		final FragmentQuery fragmentQuery = new FragmentQuery(clazz, args);
		open(fragmentQuery.getQueryString());
	}

	public void open(String fragment) {
		((EmbeddedWindow) getMainWindow()).open(fragment);
	}

	public void back() {
		((EmbeddedWindow) getMainWindow()).back();
	}

	public static void back(Application application) {
		((EmbeddedApplication) application).back();
	}

	public void refresh() {
		((EmbeddedWindow) getMainWindow()).refresh();
	}

	public static void refresh(Application application) {
		((EmbeddedApplication) application).refresh();
	}

	@Override
	public void close() {
		HttpSession session = ((WebApplicationContext) getContext()).getHttpSession();
		final UserView userView = (UserView) session.getAttribute(SetUserViewFilter.USER_SESSION_ATTRIBUTE);

		if (userView != null) {
			userView.getUser().setLastLogoutDateTime(new DateTime());
		}

		pt.ist.fenixWebFramework.security.UserView.setUser(null);
		session.removeAttribute(SetUserViewFilter.USER_SESSION_ATTRIBUTE);
		session.invalidate();
		super.close();
	}

	/**
	 * @see com.vaadin.Application#getWindow(java.lang.String)
	 */
	@Override
	public Window getWindow(String name) {
		// If the window is identified by name, we are good to go
		Window window = super.getWindow(name);

		// If not, we must create a new window for this new browser window/tab
		if (window == null) {
			window = new EmbeddedWindow();
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
	 * captured will be supplied to the corresponding {@link EmbeddedComponentContainer} using
	 * {@link EmbeddedComponentContainer#setArguments(String...)}.
	 * 
	 * @param pattern
	 *            The compiled {@link Pattern} instance.
	 * @param type
	 *            The container that will be instantiated if the pattern matches
	 *            the {@link EmbeddedApplication#VAADIN_PARAM}.
	 */
	public static void addPage(Class<? extends EmbeddedComponentContainer> page) {
		pages.put(EmbeddedComponentUtils.getAnnotationPath(EmbeddedComponentUtils.getAnnotation(page)), page);
	}

	public static Class<? extends EmbeddedComponentContainer> getPage(String path) {
		if (path == null || StringUtils.isEmpty(path)) {
			final VirtualHost virtualHost = VirtualHost.getVirtualHostForThread();
			final SortedSet<Node> nodes = virtualHost.getOrderedTopLevelNodes();
			for (final Node node : nodes) {
				if (node.isAccessible() && node instanceof VaadinNode) {
					return pages.get(((VaadinNode) node).getArgument());
				}
			}
		}
		return pages.get(path);
	}

	public static SystemMessages getSystemMessages() {
		return new CustomizedSystemMessages() {
			@Override
			public String getSessionExpiredCaption() {
				return VaadinResources.getString(SYSTEM_TITLE_SESSION_EXPIRED);
			}

			@Override
			public String getSessionExpiredMessage() {
				return VaadinResources.getString(SYSTEM_MESSAGE_SESSION_EXPIRED);
			}

			@Override
			public String getCommunicationErrorCaption() {
				return VaadinResources.getString(SYSTEM_TITLE_COMMUNICATION_ERROR);
			}

			@Override
			public String getCommunicationErrorMessage() {
				return VaadinResources.getString(SYSTEM_MESSAGE_COMMUNICATION_ERROR);
			}

			@Override
			public String getAuthenticationErrorCaption() {
				return VaadinResources.getString(SYSTEM_TITLE_AUTHENTICATION_ERROR);
			}

			@Override
			public String getAuthenticationErrorMessage() {
				return VaadinResources.getString(SYSTEM_MESSAGE_AUTHENTICATION_ERROR);
			}

			@Override
			public String getInternalErrorCaption() {
				return VaadinResources.getString(SYSTEM_TITLE_INTERNAL_ERROR);
			}

			@Override
			public String getInternalErrorMessage() {
				return VaadinResources.getString(SYSTEM_MESSAGE_INTERNAL_ERROR);
			}

			@Override
			public String getOutOfSyncCaption() {
				return VaadinResources.getString(SYSTEM_TITLE_OUTOFSYNC_ERROR);
			}

			@Override
			public String getOutOfSyncMessage() {
				return VaadinResources.getString(SYSTEM_MESSAGE_OUTOFSYNC_ERROR);
			}

			@Override
			public String getCookiesDisabledCaption() {
				return VaadinResources.getString(SYSTEM_TITLE_COOKIES_DISABLED_ERROR);
			}

			@Override
			public String getCookiesDisabledMessage() {
				return VaadinResources.getString(SYSTEM_MESSAGE_COOKIES_DISABLED_ERROR);
			}
		};
	}

	public static void registerErrorWindow(SystemErrorWindow customErrorWindow) {
		errorWindow = customErrorWindow;
	}

	@Override
	public void terminalError(com.vaadin.terminal.Terminal.ErrorEvent event) {
		final Throwable t = event.getThrowable();
		if (t instanceof SocketException) {
			// Most likely client browser closed socket
			VaadinFrameworkLogger.getLogger().info(
					"SocketException in CommunicationManager." + " Most likely client (browser) closed socket.");
			return;
		}

		if (findInvalidValueException(t) != null) {
			// validation errors are handled by their fields
			return;
		}

		FFDomainException de = findDomainExceptionCause(t);
		Buffered source = findSource(t);
		if (de != null && source != null) {
			setErrorsOn(source, de);
		} else {
			setErrorsOn(source, null);
			VaadinFrameworkLogger.getLogger().error("Uncaught Error", t);
			errorWindow.showError(getMainWindow(), t);
		}
	}

	private Buffered findSource(Throwable t) {
		if (t instanceof SourceException) {
			return ((SourceException) t).getSource();
		}
		if (t.getCause() != null) {
			return findSource(t.getCause());
		}
		return null;
	}

	private FFDomainException findDomainExceptionCause(Throwable t) {
		if (t instanceof FFDomainException) {
			return (FFDomainException) t;
		}
		if (t.getCause() != null) {
			return findDomainExceptionCause(t.getCause());
		}
		return null;
	}

	private InvalidValueException findInvalidValueException(Throwable t) {
		if (t instanceof InvalidValueException) {
			return (InvalidValueException) t;
		}
		if (t.getCause() != null) {
			return findInvalidValueException(t.getCause());
		}
		return null;
	}

	private static void setErrorsOn(Buffered source, FFDomainException message) {
		DomainExceptionErrorMessage se = null;
		if (message != null) {
			se = new DomainExceptionErrorMessage(source, message);
		}
		if (source != null) {
			if (source instanceof Form) {
				Form form = (Form) source;
				try {
					Field formError = Form.class.getDeclaredField("currentBufferedSourceException");
					formError.setAccessible(true);
					formError.set(form, se);
				} catch (IllegalArgumentException e) {
				} catch (IllegalAccessException e) {
				} catch (SecurityException e) {
				} catch (NoSuchFieldException e) {
				}
				for (Object propertyId : form.getItemPropertyIds()) {
					((AbstractField) form.getField(propertyId)).setCurrentBufferedSourceException(null);
				}
			} else if (source instanceof AbstractField) {
				AbstractField field = (AbstractField) source;
				field.setCurrentBufferedSourceException(se);
			}
		}
	}
}
