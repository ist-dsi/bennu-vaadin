package pt.ist.bennu.vaadin.resources;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import pt.utl.ist.fenix.tools.util.i18n.Language;

public class VaadinResources {
    public static enum Keys implements MessageBundleKey {
	INTEGER_VALIDATOR_MESSAGE("com.vaadin.data.validator.IntegerValidator.message"), PAGE_ACCESS_DENIED(
		"pt.ist.bennu.vaadin.error.pageAccessDenied"), SERVERS_MENU_CONFIGURATION_PAGE_TITLE(
		"pt.ist.bennu.vaadin.ui.pages.MenuConfigurationPage.title"), SERVERS_MENU_CONFIGURATION_PAGE_NODE_ACCESS_GROUP(
		"pt.ist.bennu.vaadin.ui.pages.MenuConfigurationPage.node.accessGroup");

	private String key;

	private Keys(String key) {
	    this.key = key;
	}

	@Override
	public String getKey() {
	    return key;
	}
    }

    private static final String BUNDLE_NAME = "resources/VaadinResources"; //$NON-NLS-1$

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME, Language.getLocale());

    private VaadinResources() {
    }

    public static String getString(MessageBundleKey key) {
	return getString(key.getKey());
    }

    private static String getString(String key) {
	try {
	    return RESOURCE_BUNDLE.getString(key);
	} catch (MissingResourceException e) {
	    return '!' + key + '!';
	}
    }

    public static ResourceBundle getBundle() {
	return RESOURCE_BUNDLE;
    }

}
