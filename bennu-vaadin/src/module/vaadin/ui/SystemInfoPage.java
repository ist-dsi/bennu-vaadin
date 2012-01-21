package module.vaadin.ui;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.HttpSession;

import myorg.applicationTier.Authenticate.UserView;
import myorg.domain.RoleType;
import myorg.domain.groups.Role;
import pt.ist.vaadinframework.annotation.EmbeddedComponent;
import pt.ist.vaadinframework.ui.EmbeddedComponentContainer;

import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

@EmbeddedComponent(path = "sysinfo")
public class SystemInfoPage extends VerticalLayout implements EmbeddedComponentContainer {
    public SystemInfoPage() {
	setSpacing(true);

	final Label status = new Label((String) null, Label.CONTENT_PREFORMATTED);
	addComponent(status);

	Button serialize = new Button("serialize session", new ClickListener() {
	    @Override
	    public void buttonClick(ClickEvent event) {
		try {
		    StringBuilder result = new StringBuilder();
		    HttpSession session = ((WebApplicationContext) getApplication().getContext()).getHttpSession();
		    for (Enumeration att = session.getAttributeNames(); att.hasMoreElements();) {
			String key = (String) att.nextElement();
			ByteArrayOutputStream array = new ByteArrayOutputStream();
			ObjectOutputStream stream = new ObjectOutputStream(array);
			stream.writeObject(session.getAttribute(key));
			result.append(key + ":" + array.size() + " bytes\n");
		    }
		    status.setValue(result.toString());
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    }
	});
	addComponent(serialize);
    }

    @Override
    public boolean isAllowedToOpen(Map<String, String> arguments) {
	return UserView.getCurrentUser() != null && Role.getRole(RoleType.MANAGER).isMember(UserView.getCurrentUser());
    }

    @Override
    public void setArguments(Map<String, String> arguments) {
    }

}
