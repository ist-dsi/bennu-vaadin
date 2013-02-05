/*
 * @(#)ObjectHintedProperty.java
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
package module.vaadin.data.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import pt.ist.vaadinframework.data.HintedProperty;

import com.vaadin.data.util.ObjectProperty;

/**
 * 
 * @author Pedro Santos
 * @author Sérgio Silva
 * 
 */
public class ObjectHintedProperty<T> extends ObjectProperty<T> implements HintedProperty {

    private Collection<Hint> hints;

    public ObjectHintedProperty(Object value, Class<T> type, Collection<Hint> hints) {
        super(value, type);
        this.hints = hints;
    }

    public ObjectHintedProperty(Object value, Class<T> type, Hint... hints) {
        super(value, type);
        this.hints = new ArrayList<Hint>(Arrays.asList(hints));
    }

    @Override
    public void addHint(Hint hint) {
        if (hints == null) {
            hints = new ArrayList<Hint>();
        }
        hints.add(hint);
    }

    @Override
    public Collection<Hint> getHints() {
        if (hints != null) {
            return Collections.unmodifiableCollection(hints);
        }
        return Collections.emptyList();
    }
}
