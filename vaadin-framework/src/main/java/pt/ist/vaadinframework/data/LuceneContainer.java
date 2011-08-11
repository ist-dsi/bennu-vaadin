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

import java.util.HashSet;
import java.util.List;

import pt.ist.fenixframework.plugins.luceneIndexing.DomainIndexer;
import pt.ist.fenixframework.plugins.luceneIndexing.queryBuilder.dsl.BuildingState;
import pt.ist.fenixframework.plugins.luceneIndexing.queryBuilder.dsl.DSLState;
import pt.ist.fenixframework.pstm.AbstractDomainObject;
import pt.ist.vaadinframework.data.reflect.DomainContainer;
import pt.ist.vaadinframework.ui.fields.LuceneSelect;

/**
 * Container for use with {@link LuceneSelect}.
 * 
 * @author Sérgio Silva (sergio.silva@ist.utl.pt)
 */
public class LuceneContainer<Type extends AbstractDomainObject> extends DomainContainer<Type> {

    private int maxHits = 1000000;

    public LuceneContainer(Class<? extends Type> elementType) {
	super(new HashSet<Type>(), elementType);
    }

    public void setMaxHits(int maxHits) {
	this.maxHits = maxHits;
    }

    protected DSLState createFilterExpression(String filterText) {
	final String[] split = filterText.trim().split("\\s+");
	if (split.length > 1) {
	    BuildingState expr = new BuildingState();

	    for (int i = 0; i < split.length - 1; i++) {
		expr = expr.matches(split[i]).and();
	    }

	    return expr.matches(split[split.length - 1]);

	} else {
	    return new BuildingState().matches(split[0]);
	}
    }

    public void search(String filterText) {
	removeAllItems();
	final DSLState expr = createFilterExpression(filterText);
	final List<? extends AbstractDomainObject> searchResult = DomainIndexer.getInstance().search(getElementType(), expr,
		maxHits);
	for (AbstractDomainObject domainObject : searchResult) {
	    addItem(domainObject);
	}
    }
}
