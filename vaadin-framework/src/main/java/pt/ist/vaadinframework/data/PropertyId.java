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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Pedro Santos (pedro.miguel.santos@ist.utl.pt)
 */
public class PropertyId implements Serializable {
    private final List<String> path;

    public PropertyId() {
	path = new ArrayList<String>();
    }

    public PropertyId(String piece) {
	path = new ArrayList<String>();
	path.add(piece);
    }

    protected PropertyId(PropertyId propertyId, String piece) {
	path = new ArrayList<String>(propertyId.getPath());
	path.add(piece);
    }

    public List<String> getPath() {
	return path;
    }
}
