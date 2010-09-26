package pt.ist.bennu.vaadin.ui;

import myorg.domain.VirtualHost;

import org.joda.time.DateTimeFieldType;
import org.joda.time.LocalDate;
import org.vaadin.navigator7.window.NavigableAppLevelWindow;

import pt.ist.bennu.vaadin.ui.components.ActionsMenu;
import pt.ist.bennu.vaadin.ui.components.ContentMenu;

import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Label;

@SuppressWarnings("serial")
public class ApplicationWindow extends NavigableAppLevelWindow {
    private ContentMenu contentMenu;
    private ActionsMenu actionMenu;
    private CustomLayout main;

    @Override
    protected ComponentContainer createComponents() {
	main = new CustomLayout("default");
	// main.addStyleName(Reindeer.LAYOUT_BLUE);
	main.addComponent(new Label(VirtualHost.getVirtualHostForThread().getApplicationTitle().getContent()), "title");
	main.addComponent(new Label(VirtualHost.getVirtualHostForThread().getApplicationSubTitle().getContent()), "subtitle");
	actionMenu = new ActionsMenu();
	main.addComponent(actionMenu, "actionMenu");
	contentMenu = new ContentMenu();
	contentMenu.setWidth("100%");
	main.addComponent(contentMenu, "contentMenu");
	CssLayout bodyContainer = new CssLayout();
	main.addComponent(bodyContainer, "body");
	main.addComponent(new Label("©" + new LocalDate().get(DateTimeFieldType.year()) + " "
		+ VirtualHost.getVirtualHostForThread().getApplicationCopyright()), "footer");
	this.getContent().addComponent(main);
	return bodyContainer;
    }
}
