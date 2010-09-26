package pt.ist.bennu.vaadin.ui.components;

import myorg.applicationTier.Authenticate.UserView;
import myorg.domain.User;

import org.vaadin.navigator7.NavigableApplication;
import org.vaadin.navigator7.Navigator;

import pt.ist.bennu.vaadin.ui.pages.LoginPage;
import pt.ist.bennu.vaadin.ui.pages.ServersPage;
import pt.ist.bennu.vaadin.ui.pages.SystemInfoPage;

import com.vaadin.Application;
import com.vaadin.Application.UserChangeEvent;
import com.vaadin.ui.MenuBar;

@SuppressWarnings("serial")
public class ActionsMenu extends MenuBar {
    private static final long serialVersionUID = -3810165030118823629L;

    @Override
    public void attach() {
	super.attach();
	getApplication().addListener(new Application.UserChangeListener() {
	    @Override
	    public void applicationUserChanged(UserChangeEvent event) {
		refresh();
	    }
	});
	refresh();
    }

    private void refresh() {
	removeItems();
	User user = UserView.getCurrentUser();
	if (user == null) {
	    addItem("login", new MenuBar.Command() {
		@Override
		public void menuSelected(MenuItem selectedItem) {
		    getNavigator().navigateTo(LoginPage.class);
		}
	    });
	}
	if (user != null) {
	    MenuItem configurations = addItem("configuration", null);
	    configurations.addItem("servers", new MenuBar.Command() {
		@Override
		public void menuSelected(MenuItem selectedItem) {
		    getNavigator().navigateTo(ServersPage.class);
		}
	    });
	    configurations.addItem("server info", new MenuBar.Command() {
		@Override
		public void menuSelected(MenuItem selectedItem) {
		    getNavigator().navigateTo(SystemInfoPage.class);
		}
	    });
	    MenuItem userInfo = addItem(user.getShortPresentationName(), null);
	    userInfo.setEnabled(false);
	    addItem("logout", new MenuBar.Command() {
		@Override
		public void menuSelected(MenuItem selectedItem) {
		    getApplication().close();
		}
	    });
	}
	MenuItem language = addItem("language", null);
	language.addItem("english", new MenuBar.Command() {
	    @Override
	    public void menuSelected(MenuItem selectedItem) {

	    }
	});
	language.addItem("portuguese", new MenuBar.Command() {
	    @Override
	    public void menuSelected(MenuItem selectedItem) {

	    }
	});
	setSizeUndefined();
    }

    private Navigator getNavigator() {
	return NavigableApplication.getCurrentNavigableAppLevelWindow().getNavigator();
    }
}
