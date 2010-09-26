package pt.ist.bennu.vaadin.domain;

import myorg.domain.VirtualHost;
import myorg.domain.contents.Node;
import myorg.domain.groups.PersistentGroup;
import pt.ist.fenixWebFramework.services.Service;
import pt.utl.ist.fenix.tools.util.i18n.MultiLanguageString;

import com.vaadin.ui.Component;

public class PageNode extends PageNode_Base {

    public PageNode(final VirtualHost virtualHost, final Node node) {
	super();
	init(virtualHost, node, null);
    }

    @Service
    public static PageNode createActionNode(final VirtualHost virtualHost, final Node node,
	    final Class<? extends Component> page, final PersistentGroup accessibilityGroup) {
	final PageNode pageNode = new PageNode(virtualHost, node);
	pageNode.setClassname(page.getName());
	pageNode.setAccessibilityGroup(accessibilityGroup);
	return pageNode;
    }

    @Override
    public Object getElement() {
	return null;
    }

    @Override
    public MultiLanguageString getLink() {
	return null;
    }

    @Override
    protected void appendUrlPrefix(StringBuilder stringBuilder) {
    }
}
