/*
 * @(#)ModuleInitializer.java
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
package pt.ist.bennu.vaadin;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import pt.ist.bennu.core.domain.contents.NodeBean;
import pt.ist.bennu.vaadin.domain.contents.VaadinNodeType;

/**
 * 
 * @author Nuno Diegues
 * 
 */
@WebListener
public class ModuleInitializer implements ServletContextListener {
	@Override
	public void contextInitialized(ServletContextEvent event) {
		NodeBean.registerNodeType(new VaadinNodeType());
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
	}
}
