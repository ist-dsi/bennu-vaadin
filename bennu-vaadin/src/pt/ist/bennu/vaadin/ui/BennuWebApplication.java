package pt.ist.bennu.vaadin.ui;

import org.vaadin.navigator7.NavigatorConfig;
import org.vaadin.navigator7.WebApplication;
import org.vaadin.navigator7.uri.ParamUriAnalyzer;

import pt.ist.bennu.vaadin.ui.pages.LoginPage;
import pt.ist.bennu.vaadin.ui.pages.ServersPage;
import pt.ist.bennu.vaadin.ui.pages.SystemInfoPage;
import pt.ist.bennu.vaadin.ui.pages.WelcomePage;

import com.vaadin.ui.Component;

public class BennuWebApplication extends WebApplication {
    public BennuWebApplication() {
	registerPages(new Class[] { WelcomePage.class, LoginPage.class, ServersPage.class, SystemInfoPage.class });
	setUriAnalyzer(new ParamUriAnalyzer());
    }

    public void addPageClass(Class<? extends Component> page) {
	getNavigatorConfig().addPageClass(page);
    }

    public static void ensureRegisteredClass(Class<? extends Component> clazz) {
	NavigatorConfig config = BennuWebApplication.getCurrent().getNavigatorConfig();
	if (!config.getPagesClass().contains(clazz)) {
	    config.addPageClass(clazz);
	}
    }
}