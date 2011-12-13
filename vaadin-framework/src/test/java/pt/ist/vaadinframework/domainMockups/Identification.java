/*
 * Copyright 2011 Instituto Superior Tecnico
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
package pt.ist.vaadinframework.domainMockups;

/**
 * @author Pedro Santos (pedro.miguel.santos@ist.utl.pt)
 * 
 */
public class Identification {
    public static enum Type {
	CITIZEN_CARD, PASSPORT;
    }

    private Type type;

    private String value;

    public Identification(Type type, String value) {
	this.type = type;
	this.value = value;
    }

    public Type getType() {
	return type;
    }

    public void setType(Type type) {
	this.type = type;
    }

    public String getValue() {
	return value;
    }

    public void setValue(String value) {
	this.value = value;
    }

    @Override
    public String toString() {
	return type.name() + ": " + value;
    }
}
