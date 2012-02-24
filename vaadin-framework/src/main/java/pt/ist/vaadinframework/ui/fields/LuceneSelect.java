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
package pt.ist.vaadinframework.ui.fields;

import pt.ist.vaadinframework.data.LuceneContainer;
import pt.ist.vaadinframework.ui.TimeoutSelect;

import com.vaadin.data.Container;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;

/**
 * @author Sérgio Silva (sergio.silva@ist.utl.pt)
 */
public class LuceneSelect extends TimeoutSelect implements TextChangeListener {
    public LuceneSelect() {
	super();
	addListener((TextChangeListener) this);
    }

    public LuceneSelect(String caption, Container dataSource) {
	super(caption, dataSource);
	addListener((TextChangeListener) this);
    }

    @Override
    public void textChange(TextChangeEvent event) {
	final LuceneContainer luceneContainer = (LuceneContainer) getContainerDataSource();
	if (luceneContainer == null) {
	    throw new UnsupportedOperationException("You must set the container datasource first.");
	}
	luceneContainer.search(event.getText());
    }
}
