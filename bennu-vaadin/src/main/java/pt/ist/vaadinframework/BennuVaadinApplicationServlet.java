package pt.ist.vaadinframework;

import java.io.BufferedWriter;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import com.vaadin.terminal.gwt.server.ApplicationServlet;

public class BennuVaadinApplicationServlet extends ApplicationServlet {
	@Override
	protected void writeAjaxPageHtmlHeader(BufferedWriter page, String title, String themeUri, HttpServletRequest request)
			throws IOException {
		page.write("<link href='http://fonts.googleapis.com/css?family=Lato' rel='stylesheet' type='text/css'>");
		super.writeAjaxPageHtmlHeader(page, title, themeUri, request);
	}
}
