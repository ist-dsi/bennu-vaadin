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
package pt.ist.vaadinframework.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import jvstm.VBox;
import pt.ist.fenixWebFramework.services.Service;
import pt.ist.vaadinframework.VaadinFrameworkLogger;

import com.vaadin.data.util.AbstractProperty;

public class VBoxProperty extends AbstractProperty implements HintedProperty {
    private VBox<Object> instance;

    private final Class<?> type;

    private Collection<Hint> hints;

    public VBoxProperty(Object instance, Hint... hints) {
	writeVBox(instance);
	this.type = instance.getClass();
	this.hints = new ArrayList<Hint>(Arrays.asList(hints));
    }

    public VBoxProperty(Class<?> type, Hint... hints) {
	writeVBox(null);
	this.type = type;
	this.hints = new ArrayList<Hint>(Arrays.asList(hints));
    }

    @Service
    private void writeVBox(Object instance) {
	this.instance = new VBox<Object>(instance);
    }

    @Override
    public Object getValue() {
	return instance.get();
    }

    @Override
    public void setValue(Object newValue) throws ReadOnlyException, ConversionException {
	if (isReadOnly()) {
	    throw new ReadOnlyException();
	}
	VaadinFrameworkLogger.getLogger().debug("writting vbox property with value: " + newValue);
	writeVBox(newValue);
	fireValueChange();
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

    @Override
    public Class<?> getType() {
	return type;
    }

}
