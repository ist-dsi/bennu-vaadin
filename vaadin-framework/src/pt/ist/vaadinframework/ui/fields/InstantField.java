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
package pt.ist.vaadinframework.ui.fields;

import org.joda.time.Instant;

import com.vaadin.data.Property;
import com.vaadin.ui.DateField;

/**
 * @author Pedro Santos (pedro.miguel.santos@ist.utl.pt)
 * 
 */
public class InstantField extends DateField {
    /**
     * Constructs an empty <code>InstantField</code> with no caption.
     */
    public InstantField() {
	super();
    }

    /**
     * Constructs an empty <code>InstantField</code> with caption.
     * 
     * @param caption
     *            the caption of the datefield.
     */
    public InstantField(String caption) {
	super(caption);
    }

    /**
     * Constructs a new <code>InstantField</code> that's bound to the specified
     * <code>Property</code> and has the given caption <code>String</code>.
     * 
     * @param caption
     *            the caption <code>String</code> for the editor.
     * @param dataSource
     *            the Property to be edited with this editor.
     */
    public InstantField(String caption, Property dataSource) {
	this(dataSource);
	setCaption(caption);
    }

    /**
     * Constructs a new <code>InstantField</code> that's bound to the specified
     * <code>Property</code> and has no caption.
     * 
     * @param dataSource
     *            the Property to be edited with this editor.
     */
    public InstantField(Property dataSource) throws IllegalArgumentException {
	setInvalidAllowed(false);
	if (!Instant.class.isAssignableFrom(dataSource.getType())) {
	    throw new IllegalArgumentException("Can't use " + dataSource.getType().getName() + " typed property as datasource");
	}
	setPropertyDataSource(dataSource);
    }

    /**
     * Constructs a new <code>InstantField</code> with the given caption and
     * initial text contents. The editor constructed this way will not be bound
     * to a Property unless
     * {@link com.vaadin.data.Property.Viewer#setPropertyDataSource(Property)}
     * is called to bind it.
     * 
     * @param caption
     *            the caption <code>String</code> for the editor.
     * @param value
     *            the Instant value.
     */
    public InstantField(String caption, Instant value) {
	setInvalidAllowed(false);
	setValue(value);
	setCaption(caption);
    }

    @Override
    public Class getType() {
	return getPropertyDataSource().getType();
    }
}
