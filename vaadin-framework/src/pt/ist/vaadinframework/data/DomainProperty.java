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
package pt.ist.vaadinframework.data;

import com.vaadin.data.Property;

/**
 * @author Pedro Santos (pedro.miguel.santos@ist.utl.pt)
 */
public class DomainProperty implements Property, Property.ValueChangeNotifier, Property.ReadOnlyStatusChangeNotifier {

    public DomainProperty() {

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Property#getValue()
     */
    @Override
    public Object getValue() {
	// TODO Auto-generated method stub
	return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Property#setValue(java.lang.Object)
     */
    @Override
    public void setValue(Object newValue) throws ReadOnlyException, ConversionException {
	// TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Property#getType()
     */
    @Override
    public Class<?> getType() {
	// TODO Auto-generated method stub
	return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Property#isReadOnly()
     */
    @Override
    public boolean isReadOnly() {
	// TODO Auto-generated method stub
	return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.data.Property#setReadOnly(boolean)
     */
    @Override
    public void setReadOnly(boolean newStatus) {
	// TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.data.Property.ValueChangeNotifier#addListener(com.vaadin.data
     * .Property.ValueChangeListener)
     */
    @Override
    public void addListener(ValueChangeListener listener) {
	// TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.data.Property.ValueChangeNotifier#removeListener(com.vaadin
     * .data.Property.ValueChangeListener)
     */
    @Override
    public void removeListener(ValueChangeListener listener) {
	// TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.data.Property.ReadOnlyStatusChangeNotifier#addListener(com
     * .vaadin.data.Property.ReadOnlyStatusChangeListener)
     */
    @Override
    public void addListener(ReadOnlyStatusChangeListener listener) {
	// TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.data.Property.ReadOnlyStatusChangeNotifier#removeListener(
     * com.vaadin.data.Property.ReadOnlyStatusChangeListener)
     */
    @Override
    public void removeListener(ReadOnlyStatusChangeListener listener) {
	// TODO Auto-generated method stub

    }
}
