package pt.ist.bennu.vaadin.ui.components;

import java.util.Set;

import myorg.domain.VirtualHost;
import myorg.domain.contents.Node;

import org.vaadin.navigator7.NavigableApplication;
import org.vaadin.navigator7.Navigator;

import pt.ist.bennu.vaadin.domain.PageNode;
import pt.ist.bennu.vaadin.resources.VaadinResources;
import pt.ist.bennu.vaadin.ui.BennuWebApplication;

import com.vaadin.Application;
import com.vaadin.Application.UserChangeEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.MenuBar;

public class ContentMenu extends MenuBar {
    private static final long serialVersionUID = -1916679844353100777L;

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
	try {
	    processNodes(null, VirtualHost.getVirtualHostForThread().getTopLevelNodesSet());
	} catch (ClassNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    private void processNodes(MenuItem parent, Set<Node> nodes) throws ClassNotFoundException {
	for (Node node : nodes) {
	    if (node instanceof PageNode) {
		final PageNode page = (PageNode) node;
		final Class<? extends Component> clazz = (Class<? extends Component>) Class.forName(page.getClassname());
		BennuWebApplication.ensureRegisteredClass(clazz);
		Command command = new MenuBar.Command() {
		    @Override
		    public void menuSelected(MenuItem selectedItem) {
			if (page.isAccessible()) {
			    getNavigator().navigateTo(clazz);
			} else {
			    getWindow().showNotification(VaadinResources.getString(VaadinResources.Keys.PAGE_ACCESS_DENIED));
			}
		    }
		};
		MenuItem item;
		if (parent == null) {
		    item = addItem(page.getLink().getContent(), command);
		} else {
		    item = parent.addItem(page.getLink().getContent(), command);
		}
		processNodes(item, node.getChildNodesSet());
	    }
	}
    }

    private Navigator getNavigator() {
	return NavigableApplication.getCurrentNavigableAppLevelWindow().getNavigator();
    }
}
