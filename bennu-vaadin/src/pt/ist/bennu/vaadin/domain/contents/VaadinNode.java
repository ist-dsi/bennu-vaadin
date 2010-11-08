package pt.ist.bennu.vaadin.domain.contents;

import myorg.domain.VirtualHost;
import myorg.domain.contents.Node;
import myorg.domain.groups.PersistentGroup;
import pt.ist.fenixWebFramework.services.Service;

public class VaadinNode extends VaadinNode_Base {

    public VaadinNode(final VirtualHost virtualHost, final Node parentNode, final String linkBundle,
	    final String linkKey, final String argument, final PersistentGroup accessibilityGroup) {
        super();
        init(virtualHost, parentNode, linkBundle, linkKey, argument, accessibilityGroup);
    }

    protected void init(final VirtualHost virtualHost, final Node parentNode, final String linkBundle,
	    final String linkKey, final String argument, final PersistentGroup accessibilityGroup) {
	init(virtualHost, parentNode, "/vaadinContext", "forwardToVaadin", linkBundle, linkKey, accessibilityGroup);
	setArgument(argument);
    }

    @Service
    public static VaadinNode createVaadinNode(final VirtualHost virtualHost, final Node parentNode, final String linkBundle,
	    final String linkKey, final String argument, final PersistentGroup accessibilityGroup) {
	return new VaadinNode(virtualHost, parentNode, linkBundle, linkKey, argument, accessibilityGroup);
    }

    @Override
    protected void appendUrlPrefix(StringBuilder stringBuilder) {
	super.appendUrlPrefix(stringBuilder);
	stringBuilder.append("&argument=");
	stringBuilder.append(getArgument());
    }

}
