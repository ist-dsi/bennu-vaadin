package pt.ist.bennu.vaadin.domain;

import myorg.util.BundleUtil;

public class VaadinUtils {

    public static String getBundle() {
	return "resources.BennuVaadinResources";
    }

    public static String getMessage(final String key, String... args) {
	return BundleUtil.getFormattedStringFromResourceBundle(getBundle(), key, args);
    }

}
