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
package com.vaadin.data.util;

import pt.ist.fenixframework.pstm.AbstractDomainObject;

import com.vaadin.data.util.metamodel.PropertyDescriptor;

/**
 * @author Pedro Santos (pedro.miguel.santos@ist.utl.pt)
 */
public class DomainProperty extends AbstractDomainProperty {
    private final PropertyDescriptor descriptor;

    public DomainProperty(AbstractDomainObject host, PropertyDescriptor descriptor) {
	super(host);
	this.descriptor = descriptor;
    }

    public DomainProperty(Class<? extends AbstractDomainObject> type, PropertyDescriptor descriptor) {
	super(type);
	this.descriptor = descriptor;
    }

    public DomainProperty(AbstractDomainItem host, PropertyDescriptor descriptor) {
	super(host, descriptor.getPropertyType());
	this.descriptor = descriptor;
    }

    /**
     * @see com.vaadin.data.util.AbstractDomainProperty#isRequired()
     */
    @Override
    public boolean isRequired() {
	return descriptor.isRequired();
    }

    /**
     * @see com.vaadin.data.util.AbstractDomainProperty#getValueFrom(pt.ist.fenixframework.pstm.AbstractDomainObject)
     */
    @Override
    protected Object getValueFrom(AbstractDomainObject host) {
	return descriptor.read(host);
    }

    /**
     * @see com.vaadin.data.util.AbstractDomainProperty#setValueOn(pt.ist.fenixframework
     *      .pstm.AbstractDomainObject, java.lang.Object)
     */
    @Override
    protected void setValueOn(AbstractDomainObject host, Object newValue) throws ConversionException {
	descriptor.write(host, newValue);
    }

    /**
     * @see com.vaadin.data.util.AbstractDomainProperty#getNullValue()
     */
    @Override
    protected Object getNullValue() {
	if (Boolean.class.isAssignableFrom(getType())) {
	    return Boolean.FALSE;
	}
	return null;
    }
}
