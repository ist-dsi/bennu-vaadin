package pt.ist.bennu.vaadin.ui.pages;

import java.io.IOException;
import java.util.Map.Entry;
import java.util.Properties;

import pt.utl.ist.fenix.tools.util.FileUtils;

import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.terminal.gwt.server.WebBrowser;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class SystemInfoPage extends CustomComponent {
    private static final long serialVersionUID = -879244468499337336L;

    private boolean populated = false;

    @Override
    public void attach() {
	if (populated) {
	    // Only populate the layout once
	    return;
	}

	VerticalLayout layout = new VerticalLayout();
	layout.setSpacing(true);

	// Find the context we are running in and get the browser information
	// from that.
	WebApplicationContext context = ((WebApplicationContext) getApplication().getContext());
	WebBrowser webBrowser = context.getBrowser();

	// Create a text to show based on the browser.
	String browserText = getBrowserAndVersion(webBrowser);
	browserText = browserText + " in " + getOperatingSystem(webBrowser);

	// Create labels for the information and add them to the application
	Label ipAddresslabel = new Label("Hello user from <b>" + webBrowser.getAddress() + "</b>.", Label.CONTENT_XHTML);
	Label browser = new Label("You are running <b>" + browserText + "</b>.", Label.CONTENT_XHTML);
	Label screenSize = new Label("Your screen resolution is <b>" + webBrowser.getScreenWidth() + "x"
		+ webBrowser.getScreenHeight() + "</b>.", Label.CONTENT_XHTML);
	Label locale = new Label("Your browser is set to primarily use the <b>" + webBrowser.getLocale() + "</b> locale.",
		Label.CONTENT_XHTML);

	layout.addComponent(ipAddresslabel);
	layout.addComponent(browser);
	layout.addComponent(screenSize);
	layout.addComponent(locale);

	String buildVersion;
	try {
	    buildVersion = FileUtils.readFile(getClass().getResourceAsStream("/.build.version")).toString();
	} catch (IOException e) {
	    buildVersion = "unknown";
	}
	layout.addComponent(new Label("<b>Build Version:</b> " + buildVersion, Label.CONTENT_XHTML));

	layout.addComponent(getSystemProperties());
	setCompositionRoot(layout);
	populated = true;
    }

    private String getOperatingSystem(WebBrowser webBrowser) {
	if (webBrowser.isWindows()) {
	    return "Windows";
	} else if (webBrowser.isMacOSX()) {
	    return "Mac OSX";
	} else if (webBrowser.isLinux()) {
	    return "Linux";
	} else {
	    return "an unknown operating system";
	}
    }

    private String getBrowserAndVersion(WebBrowser webBrowser) {
	if (webBrowser.isChrome()) {
	    return "Chrome " + webBrowser.getBrowserMajorVersion() + "." + webBrowser.getBrowserMinorVersion();
	} else if (webBrowser.isOpera()) {
	    return "Opera " + webBrowser.getBrowserMajorVersion() + "." + webBrowser.getBrowserMinorVersion();
	} else if (webBrowser.isFirefox()) {
	    return "Firefox " + webBrowser.getBrowserMajorVersion() + "." + webBrowser.getBrowserMinorVersion();
	} else if (webBrowser.isSafari()) {
	    return "Safari " + webBrowser.getBrowserMajorVersion() + "." + webBrowser.getBrowserMinorVersion();
	} else if (webBrowser.isIE()) {
	    return "Internet Explorer " + webBrowser.getBrowserMajorVersion();
	} else {
	    return "an unknown browser";
	}
    }

    private Component getSystemProperties() {
	Properties properties = System.getProperties();
	StringBuilder builder = new StringBuilder();
	for (Entry<Object, Object> property : properties.entrySet()) {
	    builder.append("<li>" + property.getKey() + "=" + property.getValue() + "</li>\n");
	}
	return new Label("<ul>\n" + builder.toString() + "</ul>", Label.CONTENT_XHTML);
    }
}
