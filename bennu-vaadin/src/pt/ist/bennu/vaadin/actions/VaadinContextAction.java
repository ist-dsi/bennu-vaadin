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
	return forwardToVaadin(request, argument, true);
    }

    public final ActionForward forwardToFullVaadin(final ActionMapping mapping, final ActionForm form,
	    final HttpServletRequest request, final HttpServletResponse response) throws Exception {
	final String argument = request.getParameter("argument");
	return forwardToVaadin(request, argument, false);
    }

    public static ActionForward forwardToVaadin(final HttpServletRequest request, final String argument, final boolean useBennuLayout) {
	request.getSession().setAttribute(EmbeddedApplication.VAADIN_PARAM, argument);
	final VaadinLayoutContext vaadinLayoutContext = new VaadinLayoutContext(getContext(request).getPath());
	if (!useBennuLayout) {
	    vaadinLayoutContext.setPageHeader("/layout/blank.jsp");
	    vaadinLayoutContext.setSideBar("/layout/blank.jsp");
	    vaadinLayoutContext.setMenuTop("/layout/blank.jsp");
	    vaadinLayoutContext.setSubMenuTop("/layout/blank.jsp");
	    vaadinLayoutContext.setPageOperations("/layout/blank.jsp");
	    vaadinLayoutContext.setBreadCrumbs("/layout/blank.jsp");
	    vaadinLayoutContext.setFooter("/layout/blank.jsp");
	}
	setContext(request, vaadinLayoutContext);
	return forward(request, "/embedded/vaadin-embedded.jsp");
    }
}
