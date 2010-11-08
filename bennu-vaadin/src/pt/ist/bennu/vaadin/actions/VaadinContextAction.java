package pt.ist.bennu.vaadin.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import myorg.presentationTier.LayoutContext;
import myorg.presentationTier.actions.ContextBaseAction;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import pt.ist.fenixWebFramework.struts.annotations.Mapping;
import pt.ist.vaadinframework.EmbeddedApplication;

@Mapping(path="/vaadinContext")
public class VaadinContextAction extends ContextBaseAction {

    public static class VaadinLayoutContext extends LayoutContext {
	public VaadinLayoutContext(String path) {
	    super(path);
	    addHead("/embedded/head.jsp");
	}
    }

    public final ActionForward forwardToVaadin(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	final String argument = request.getParameter("argument");
	return forwardToVaadin(request, argument);
    }

    public static ActionForward forwardToVaadin(final HttpServletRequest request, final String argument) {
	request.getSession().setAttribute(EmbeddedApplication.VAADIN_PARAM, argument);
	setContext(request, new VaadinLayoutContext(getContext(request).getPath()));
	return forward(request, "/embedded/vaadin-embedded.jsp");
    }
}
