package pt.ist.vaadinframework.ui.factory;

import pt.ist.bennu.core.domain.User;
import pt.ist.bennu.core.domain.groups.PersistentGroup;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.Resource;
import com.vaadin.terminal.ThemeResource;

/**
 * A resource factory that given a domain object returns a correspoding resource.
 * @author David Martinho (davidmartinho@gmail.com)
 *
 */
public class ResourceFactory {
    
    public static Resource getPictureFor(User user) {
	return new ExternalResource(
		"https://fenix.ist.utl.pt/publico/retrievePersonalPhoto.do?method=retrieveByUUID&contentContextPath_PATH=/homepage&uuid="
			+ user.getUsername(), "image/jpeg");
    }
    
    public static Resource getPictureFor(PersistentGroup group) {
	return new ThemeResource("../runo/icons/32/users.png");
    }
    
    public static Resource getAvatarFor(Object obj) {
	if(obj instanceof User) {
	    return getPictureFor((User)obj);
	} else if(obj instanceof PersistentGroup) {
	    return getPictureFor((PersistentGroup)obj);
	} else {
	    return new ThemeResource("../runo/icons/32/users.png");
	}
    }
    
}
