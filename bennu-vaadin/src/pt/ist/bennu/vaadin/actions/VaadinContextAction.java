package pt.ist.bennu.vaadin.actions;

import javax.servlet.http.HttpServletRequest;

import myorg.presentationTier.LayoutContext;
import myorg.presentationTier.actions.ContextBaseAction;

import org.apache.struts.action.ActionForward;

import pt.ist.vaadinframework.EmbeddedApplication;

public class VaadinContextAction extends ContextBaseAction {
    public static class VaadinLayoutContext extends LayoutContext {
	public VaadinLayoutContext(String path) {
	    super(path);
	    addHead("/embedded/head.jsp");
	}
    }

    public static ActionForward forwardToVaadin(final HttpServletRequest request, final String argument) {
	request.getSession().setAttribute(EmbeddedApplication.VAADIN_PARAM, argument);
	setContext(request, new VaadinLayoutContext(getContext(request).getPath()));
	return forward(request, "/embedded/vaadin-embedded.jsp");
    }
}
