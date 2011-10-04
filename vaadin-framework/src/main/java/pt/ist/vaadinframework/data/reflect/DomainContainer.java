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
package pt.ist.vaadinframework.data.reflect;

import java.util.Collection;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import pt.ist.fenixframework.plugins.luceneIndexing.DomainIndexer;
import pt.ist.fenixframework.plugins.luceneIndexing.queryBuilder.dsl.BuildingState;
import pt.ist.fenixframework.plugins.luceneIndexing.queryBuilder.dsl.DSLState;
import pt.ist.fenixframework.pstm.AbstractDomainObject;
import pt.ist.vaadinframework.VaadinFrameworkLogger;
import pt.ist.vaadinframework.data.BufferedContainer;
import pt.ist.vaadinframework.data.HintedProperty;
import pt.ist.vaadinframework.data.LuceneContainer;
import pt.ist.vaadinframework.data.VBoxProperty;
import pt.ist.vaadinframework.data.metamodel.MetaModel;
import pt.ist.vaadinframework.data.metamodel.PropertyDescriptor;

import com.vaadin.data.util.ItemSorter;

public class DomainContainer<Type extends AbstractDomainObject> extends BufferedContainer<Type, Object, DomainItem<Type>>
implements LuceneContainer {
    private final int maxHits = 1000000;

    public DomainContainer(HintedProperty value, Class<? extends Type> elementType) {
	super(value, elementType);
    }

    public DomainContainer(Class<? extends Type> elementType) {
	super(new VBoxProperty(Collection.class), elementType);
    }

    public DomainContainer(Collection<? extends Type> elements, Class<? extends Type> elementType) {
	super(new VBoxProperty(elements), elementType);
    }

    @Override
    public DomainItem<Type> makeItem(HintedProperty itemId) {
	return new DomainItem<Type>(itemId);
    }

    public void setContainerProperties(String... propertyIds) {
	for (String propertyId : propertyIds) {
	    PropertyDescriptor propertyDescriptor = MetaModel.findMetaModelForType(getElementType()).getPropertyDescriptor(
		    propertyId);
	    addContainerProperty(propertyId, propertyDescriptor.getPropertyType(), propertyDescriptor.getDefaultValue());
	}
    }

    @Override
    public void setItemSorter(ItemSorter itemSorter) {
	super.setItemSorter(itemSorter);
    }

    @Override
    public void search(String filterText) {
	removeAllItems();
	final DSLState expr = createFilterExpression(filterText);
	DateTime start = new DateTime();
	final List<? extends AbstractDomainObject> searchResult = DomainIndexer.getInstance().search(getElementType(), expr,
		maxHits);
	DateTime check1 = new DateTime();
	addItemBatch(searchResult);
	DateTime check2 = new DateTime();
	VaadinFrameworkLogger.getLogger().debug(
		"container search: " + expr.toString() + " took: " + new Interval(start, check1).toDuration() + " "
			+ new Interval(check1, check2).toDuration() + "(" + new Interval(start, check2).toDuration() + ")");
    }

    protected DSLState createFilterExpression(String filterText) {
	return new BuildingState().matches(filterText);
    }
}
