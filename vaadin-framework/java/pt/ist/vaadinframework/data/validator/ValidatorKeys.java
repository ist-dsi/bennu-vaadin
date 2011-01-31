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
package pt.ist.vaadinframework.data.validator;

import pt.ist.vaadinframework.MessageBundleKey;

/**
 * @author Pedro Santos (pedro.miguel.santos@ist.utl.pt)
 */
public enum ValidatorKeys implements MessageBundleKey {
    BYTE_VALIDATOR_ERROR("pt.ist.vaadinframework.data.validator.byte.error"), LONG_VALIDATOR_ERROR(
	    "pt.ist.vaadinframework.data.validator.byte.error"), SHORT_VALIDATOR_ERROR(
	    "pt.ist.vaadinframework.data.validator.short.error"), INTEGER_VALIDATOR_ERROR(
	    "pt.ist.vaadinframework.data.validator.integer.error"), FLOAT_VALIDATOR_ERROR(
	    "pt.ist.vaadinframework.data.validator.float.error"), DOUBLE_VALIDATOR_ERROR(
	    "pt.ist.vaadinframework.data.validator.double.error"), CHARACTER_VALIDATOR_ERROR(
	    "pt.ist.vaadinframework.data.validator.character.error"), BIG_DECIMAL_VALIDATOR_ERROR(
	    "pt.ist.vaadinframework.data.validator.bigdecimal.error"), URL_VALIDATOR_ERROR(
	    "pt.ist.vaadinframework.data.validator.url.error");

    private String key;

    private ValidatorKeys(String key) {
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
