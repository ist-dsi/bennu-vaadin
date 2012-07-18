/*
 * @(#)VaadinResources.java
 *
 * Copyright 2011 Instituto Superior Tecnico
 * Founding Authors: Pedro Santos
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the Bennu-Vadin Integration Module.
 *
 *   The Bennu-Vadin Integration Module is free software: you can
 *   redistribute it and/or modify it under the terms of the GNU Lesser General
 *   Public License as published by the Free Software Foundation, either version 
 *   3 of the License, or (at your option) any later version.
 *
 *   The Bennu-Vadin Module is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with the Bennu-Vadin Module. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package module.vaadin.resources;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import pt.utl.ist.fenix.tools.util.i18n.Language;

/**
 * 
 * @author Pedro Santos
 * 
 */
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
