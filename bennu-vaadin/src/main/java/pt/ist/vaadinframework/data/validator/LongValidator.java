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

import pt.ist.vaadinframework.VaadinResourceConstants;
import pt.ist.vaadinframework.VaadinResources;

import com.vaadin.data.validator.AbstractStringValidator;

/**
 * String validator for long. See {@link com.vaadin.data.validator.AbstractStringValidator} for more
 * information.
 * 
 * @author Pedro Santos (pedro.miguel.santos@ist.utl.pt)
 */
@SuppressWarnings("serial")
public class LongValidator extends AbstractStringValidator implements VaadinResourceConstants {
    public LongValidator() {
        super(VaadinResources.getString(LONG_VALIDATOR_ERROR));
    }

    @Override
    protected boolean isValidString(String value) {
        try {
            Long.parseLong(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
