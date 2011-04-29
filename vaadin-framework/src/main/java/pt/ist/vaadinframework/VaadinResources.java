/*
 * Copyright 2010 Instituto Superior Tecnico
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the vaadin-framework.
 *
 *   The vaadin-framework Infrastructure is free software: you can 
 *   redistribute it and/or modify it under the terms of the GNU Lesser General 
 *   Public License as published by the Free Software Foundation, either version 
 *   3 of the License, or (at your option) any later version.*
 *
 *   vaadin-framework is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with vaadin-framework. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package pt.ist.vaadinframework;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import pt.utl.ist.fenix.tools.util.i18n.Language;

/**
 * @author Pedro Santos (pedro.miguel.santos@ist.utl.pt)
 * 
 */
public class VaadinResources {
    public static enum CommonKeys implements MessageBundleKey {
	COMMONS_ACTION_SUBMIT("pt.ist.vaadinframework.commons.action.submit"), COMMONS_ACTION_DISCARD(
		"pt.ist.vaadinframework.commons.action.discard"), COMMONS_ACTION_CANCEL(
		"pt.ist.vaadinframework.commons.action.cancel"), COMMONS_MESSAGE_SUBMIT(
		"pt.ist.vaadinframework.commons.message.submit"), COMMONS_MESSAGE_DISCARD(
		"pt.ist.vaadinframework.commons.message.discard"), COMMONS_MESSAGE_CANCEL(
		"pt.ist.vaadinframework.commons.message.cancel");

	private String key;

	private CommonKeys(String key) {
	    this.key = key;
	}

	/**
	 * @see pt.ist.vaadinframework.MessageBundleKey#getKey()
	 */
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

    private static ResourceBundle getBundle() {
	return RESOURCE_BUNDLE;
    }
}
