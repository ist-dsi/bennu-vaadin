package pt.ist.bennu.vaadin.ui;

import myorg.applicationTier.Authenticate.UserView;
import myorg.domain.VirtualHost;

import org.vaadin.navigator7.NavigableApplication;
import org.vaadin.navigator7.window.NavigableAppLevelWindow;

@SuppressWarnings("serial")
public class BennuApplication extends NavigableApplication {
    public BennuApplication() {
	// setTheme(VirtualHost.getVirtualHostForThread().getTheme().getName());
	System.out.println(VirtualHost.getVirtualHostForThread().getTheme().getName());
	setTheme("default");
	setUser(UserView.getCurrentUserView());
    }

    @Override
    public NavigableAppLevelWindow createNewNavigableAppLevelWindow() {
	return new ApplicationWindow();
    }
}
