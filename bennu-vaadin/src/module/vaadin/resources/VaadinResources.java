package module.vaadin.resources;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import pt.utl.ist.fenix.tools.util.i18n.Language;

public class VaadinResources {
    private static final String BUNDLE_NAME = "resources/BennuVaadinResources"; //$NON-NLS-1$

    private static final Map<Locale, ResourceBundle> bundleMap = new HashMap<Locale, ResourceBundle>();

    private static final Logger logger = Logger.getLogger(VaadinResources.class);

    private VaadinResources() {
    }

    public static String getString(String key, String... args) {
	try {
	    if (!bundleMap.containsKey(Language.getLanguage())) {
		bundleMap.put(Language.getLocale(), ResourceBundle.getBundle(BUNDLE_NAME, Language.getLocale()));
	    }
	    String message = bundleMap.get(Language.getLocale()).getString(key);
	    for (int i = 0; i < args.length; i++) {
		message = message.replaceAll("\\{" + i + "\\}", args[i]);
	    }
	    return message;
	} catch (MissingResourceException e) {
	    logger.warn(e.getMessage());
	    return '!' + key + '!';
	}
    }

}
