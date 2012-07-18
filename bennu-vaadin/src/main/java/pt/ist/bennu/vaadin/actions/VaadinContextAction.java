/*
 * @(#)VaadinContextAction.java
 *
 * Copyright 2010 Instituto Superior Tecnico
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
package pt.ist.bennu.vaadin.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pt.ist.bennu.core.presentationTier.Context;
import pt.ist.bennu.core.presentationTier.actions.ContextBaseAction;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import pt.ist.fenixWebFramework.struts.annotations.Mapping;

@Mapping(path = "/vaadinContext")
/**
 * 
 * @author Pedro Santos
 * @author Luis Cruz
 * 
 */
public class VaadinContextAction extends ContextBaseAction {

    public static class FullVaadinLayoutContext extends Context {

	@Override
	public ActionForward forward(final String body) {
	    return new ActionForward("/embedded/vaadin-embedded-full.jsp");
	}

	public FullVaadinLayoutContext() {
	    super(null);
	}
    }

    public final ActionForward forwardToVaadin(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	return forwardToVaadin(request, true);
    }

    public final ActionForward forwardToFullVaadin(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	return forwardToVaadin(request, false);
    }

    public static ActionForward forwardToVaadin(final HttpServletRequest request, final boolean useBennuLayout) {
	final Context context = useBennuLayout ? getContext(request) : new FullVaadinLayoutContext();
	setContext(request, context);
	return forward(request, "/embedded/vaadin-embedded.jsp");
    }
}
