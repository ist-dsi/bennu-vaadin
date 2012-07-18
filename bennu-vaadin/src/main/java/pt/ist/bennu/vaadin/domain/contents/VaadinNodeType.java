/*
 * @(#)VaadinNodeType.java
 *
 * Copyright 2011 Instituto Superior Tecnico
 * Founding Authors: Pedro Santos
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the Bennu-Vadin Integration Module.
 *
 *   The Bennu-Vadin Integration Module is free software: you can
 *   redistribute it and/or modify it under the terms of the GNU Lesser General
 *   Public License as published by the Free Software Foundation, either version 
 *   3 of the License, or (at your option) any later version.
 *
 *   The Bennu-Vadin Module is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with the Bennu-Vadin Module. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package pt.ist.bennu.vaadin.domain.contents;

import pt.ist.bennu.core.domain.VirtualHost;
import pt.ist.bennu.core.domain.contents.Node;
import pt.ist.bennu.core.domain.contents.NodeBean;
import pt.ist.bennu.core.domain.contents.NodeType;

/**
 * 
 * @author Nuno Diegues
 * 
 */
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
