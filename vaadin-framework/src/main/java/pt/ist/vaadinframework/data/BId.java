/*
 * Copyright 2011 Instituto Superior Tecnico
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the vaadin-framework-latest.
 *
 *   The vaadin-framework-latest Infrastructure is free software: you can 
 *   redistribute it and/or modify it under the terms of the GNU Lesser General 
 *   Public License as published by the Free Software Foundation, either version 
 *   3 of the License, or (at your option) any later version.*
 *
 *   vaadin-framework-latest is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with vaadin-framework-latest. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package pt.ist.vaadinframework.data;

/**
 * @author Pedro Santos (pedro.miguel.santos@ist.utl.pt)
 * 
 */
public class BId extends PropertyId {
    public BId() {
	super();
    }

    public BId(String piece) {
	super(piece);
    }

    public BId(PropertyId propertyId, String piece) {
	super(propertyId, piece);
    }

    public AId a() {
	return new AId(this, "a");
    }

    public static final AId A = new BId().a();

    static {
	A.name();
    }
}
