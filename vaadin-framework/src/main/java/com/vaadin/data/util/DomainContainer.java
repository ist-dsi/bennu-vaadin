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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import pt.ist.fenixframework.pstm.AbstractDomainObject;

import com.vaadin.data.util.metamodel.MetaModel;
import com.vaadin.data.util.metamodel.PropertyDescriptor;

/**
 * @author Pedro Santos (pedro.miguel.santos@ist.utl.pt)
 * 
 */
public class DomainContainer extends AbstractDomainContainer {
    private PropertyDescriptor descriptor;

    private Class<? extends AbstractDomainObject> elementType;

    public DomainContainer(AbstractDomainItem host, PropertyDescriptor descriptor) {
	super(host, descriptor.getPropertyType());
	this.descriptor = descriptor;
    }

    public DomainContainer(Set<? extends AbstractDomainObject> valueSet, Class<? extends AbstractDomainObject> elementType) {
	super(valueSet);
	this.elementType = elementType;
    }

    /**
     * @see com.vaadin.data.util.AbstractDomainProperty#isRequired()
     */
    @Override
    public boolean isRequired() {
	return descriptor != null ? descriptor.isRequired() : false;
    }

    /**
     * @see com.vaadin.data.util.AbstractDomainProperty#getNullValue()
     */
    @Override
    protected Object getNullValue() {
	return new HashSet();
    }

    /**
     * @see com.vaadin.data.util.AbstractDomainProperty#getValueFrom(pt.ist.
     *      fenixframework.pstm.AbstractDomainObject)
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

    public void setContainerProperties(String... propertyIds) {
	for (String propertyId : propertyIds) {
	    PropertyDescriptor propertyDescriptor = MetaModel.findMetaModelForType(getElementType()).getPropertyDescriptor(
		    propertyId);
	    addContainerProperty(propertyId, propertyDescriptor.getPropertyType(), propertyDescriptor.getDefaultValue());
	}
    }

    public void discoverAllContainerProperties() {
	Collection<PropertyDescriptor> descriptors = MetaModel.findMetaModelForType(getElementType()).getPropertyDescriptors();
	for (PropertyDescriptor descriptor : descriptors) {
	    addContainerProperty(descriptor.getPropertyId(), descriptor.getPropertyType(), descriptor.getDefaultValue());
	}
    }

    protected Class<? extends AbstractDomainObject> getElementType() {
	return descriptor != null ? descriptor.getCollectionElementType() : elementType;
    }

    /**
     * @see com.vaadin.data.util.AbstractDomainContainer#addItem()
     */
    @Override
    public Object addItem() throws UnsupportedOperationException {
	if (descriptor != null) {
	    return addItem(descriptor.getCollectionElementType());
	}
	return addItem(elementType);
    }

    /**
     * @see com.vaadin.data.util.AbstractDomainContainer#lazyCreateItem(pt.ist.
     *      fenixframework.pstm.AbstractDomainObject)
     */
    @Override
    protected AbstractDomainItem lazyCreateItem(AbstractDomainObject itemId) {
	return new DomainItem(itemId);
    }

    /**
     * @see com.vaadin.data.util.AbstractDomainContainer#createItemForType(java.lang.Class)
     */
    @Override
    protected AbstractDomainItem createItemForType(Class<? extends AbstractDomainObject> type) {
	return new DomainItem(type);
    }
}
