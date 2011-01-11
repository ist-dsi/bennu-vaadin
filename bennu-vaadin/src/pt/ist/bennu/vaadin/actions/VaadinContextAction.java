package pt.ist.bennu.vaadin.actions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import myorg.presentationTier.Context;
import myorg.presentationTier.LayoutContext;
import myorg.presentationTier.actions.ContextBaseAction;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import pt.ist.fenixWebFramework.struts.annotations.Mapping;
import pt.ist.vaadinframework.EmbeddedApplication;

@Mapping(path = "/vaadinContext")
public class VaadinContextAction extends ContextBaseAction {

    public static class VaadinLayoutContext extends LayoutContext {
	public VaadinLayoutContext(String path) {
	    super(path);
	    addHead("/embedded/head.jsp");
	}
    }

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
	final String argument = request.getParameter("argument");
	return forwardToVaadin(request, argument, true);
    }

    public final ActionForward forwardToFullVaadin(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	final String argument = request.getParameter("argument");
	return forwardToVaadin(request, argument, false);
    }

    public static ActionForward forwardToVaadin(final HttpServletRequest request, final String argument,
	    final boolean useBennuLayout) {
	request.getSession().setAttribute(EmbeddedApplication.VAADIN_PARAM, argument);
	final Context context = useBennuLayout ? new VaadinLayoutContext(getContext(request).getPath()) : new FullVaadinLayoutContext();
	setContext(request, context);
	return forward(request, "/embedded/vaadin-embedded.jsp");
    }
}
