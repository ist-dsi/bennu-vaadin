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
package pt.ist.vaadinframework.ui.layout;

import java.util.Collection;

import pt.ist.vaadinframework.ui.DefaultViewerFactory;
import pt.ist.vaadinframework.ui.ViewerFactory;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;

/**
 * @author Pedro Santos (pedro.miguel.santos@ist.utl.pt)
 * 
 */
public class TabularLayout extends GridLayout implements Item.Viewer {
    private static final long serialVersionUID = -1819919667085496492L;

    private Item dataSource;

    private final ViewerFactory viewerFactory;

    public TabularLayout() {
	super(2, 1);
	addStyleName("tabular");
	setSpacing(true);
	viewerFactory = new DefaultViewerFactory();
    }

    /**
     * @see com.vaadin.data.Item.Viewer#setItemDataSource(com.vaadin.data.Item)
     */
    @Override
    public void setItemDataSource(Item newDataSource) {
	setItemDataSource(newDataSource, newDataSource != null ? newDataSource.getItemPropertyIds() : null);
    }

    public void setItemDataSource(Item itemDatasource, Collection<?> propertyIds) {
	this.dataSource = itemDatasource;
	removeAllComponents();
	if (itemDatasource == null) {
	    return;
	}
	for (Object id : propertyIds) {
	    Property property = itemDatasource.getItemProperty(id);
	    if (id != null && property != null) {
		Property.Viewer viewer = viewerFactory.createViewer(itemDatasource, id, this);
		if (viewer != null) {
		    viewer.setPropertyDataSource(property);
		    // this cast is ugly but unfortunately viewers are not
		    // components by interface extension, although this is
		    // always true in practice
		    addComponent((Component) viewer);
		}
	    }
	}
    }

    /**
     * @see com.vaadin.data.Item.Viewer#getItemDataSource()
     */
    @Override
    public Item getItemDataSource() {
	return dataSource;
    }
}
