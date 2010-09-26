package pt.ist.bennu.vaadin.ui.pages;

import myorg.applicationTier.Authenticate;
import myorg.applicationTier.Authenticate.UserView;
import myorg.domain.User;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.LoginForm;
import com.vaadin.ui.LoginForm.LoginEvent;
import com.vaadin.ui.LoginForm.LoginListener;
import com.vaadin.ui.Window.Notification;

@SuppressWarnings("serial")
public class LoginPage extends CustomComponent {
    private static final long serialVersionUID = 3981796963568273339L;

    @Override
    public void attach() {
	LoginForm login = new LoginForm();
	login.addListener(new LoginListener() {
	    @Override
	    public void onLogin(LoginEvent event) {
		String username = event.getLoginParameter("username");
		if (User.findByUsername(username) == null) {
		    getWindow().showNotification("Invalid user/password!");
		} else {
		    UserView authenticatedUser = Authenticate.authenticate(username, event.getLoginParameter("password"));
		    getApplication().setUser(authenticatedUser);
		    getWindow().showNotification("successful login", "Authenticated as " + username,
			    Notification.TYPE_TRAY_NOTIFICATION);
		}
	    }
	});
	setCompositionRoot(login);
    }
}
