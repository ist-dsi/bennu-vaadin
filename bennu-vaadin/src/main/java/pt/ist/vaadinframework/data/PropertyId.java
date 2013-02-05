/*
 * Copyright 2011 Instituto Superior Tecnico
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the vaadin-frameworklatest.
 *
 *   The vaadin-frameworklatest Infrastructure is free software: you can 
 *   redistribute it and/or modify it under the terms of the GNU Lesser General 
 *   Public License as published by the Free Software Foundation, either version 
 *   3 of the License, or (at your option) any later version.*
 *
 *   vaadin-frameworklatest is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with vaadin-frameworklatest. If not, see <http://www.gnu.org/licenses/>.
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
    private final List<Object> path;

    public PropertyId(Object piece) {
        path = new ArrayList<Object>();
        path.add(piece);
    }

    protected PropertyId(PropertyId propertyId, Object piece) {
        this(propertyId.path);
        path.add(piece);
    }

    protected PropertyId(List<Object> path) {
        this.path = new ArrayList<Object>(path);
    }

    public List<Object> getPath() {
        return path;
    }

    public PropertyId first() {
        return new PropertyId(path.get(0));
    }

    public PropertyId rest() {
        return new PropertyId(path.subList(1, path.size()));
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof PropertyId) {
            return path.equals(((PropertyId) object).path);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return path.hashCode();
    }
}
