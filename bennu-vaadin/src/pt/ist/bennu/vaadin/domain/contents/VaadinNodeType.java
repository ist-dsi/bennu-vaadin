package pt.ist.bennu.vaadin.domain.contents;

import myorg.domain.VirtualHost;
import myorg.domain.contents.Node;
import myorg.domain.contents.NodeBean;
import myorg.domain.contents.NodeType;

public class VaadinNodeType extends NodeType {

    @Override
    public String getName() {
	return "VaadinNode";
    }

    @Override
    public Node instantiateNode(VirtualHost virtualHost, Node parentNode, NodeBean nodeBean) {
	return VaadinNode.createVaadinNode(virtualHost, parentNode, nodeBean.getLinkBundle(), nodeBean.getLinkKey(),
		nodeBean.getArgument(), nodeBean.getPersistentGroup(), nodeBean.isUseBennuLayout());
    }

}
