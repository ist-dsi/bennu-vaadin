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

import pt.ist.vaadinframework.VaadinResourceConstants;
import pt.ist.vaadinframework.VaadinResources;

import com.vaadin.data.Buffered;
import com.vaadin.data.Item;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Form;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Layout.SpacingHandler;

/**
 * @author Pedro Santos (pedro.miguel.santos@ist.utl.pt)
 */
@SuppressWarnings("serial")
public class BufferedSourceForm extends Form implements VaadinResourceConstants {
    public BufferedSourceForm() {
	Layout controls = getFooter();
	if (controls instanceof SpacingHandler) {
	    ((SpacingHandler) controls).setSpacing(true);
	}
	controls.setMargin(true, false, false, false);
	Button save = new Button(VaadinResources.getString(BUTTON_SAVE));
	controls.addComponent(save);
	Button discard = new Button(VaadinResources.getString(BUTTON_DISCARD));
	controls.addComponent(discard);
	setFooter(controls);
	save.addListener(new Button.ClickListener() {
	    @Override
	    public void buttonClick(ClickEvent event) {
		commit();
	    }
	});
	discard.addListener(new Button.ClickListener() {
	    @Override
	    public void buttonClick(ClickEvent event) {
		discard();
	    }
	});
    }

    /**
     * @see com.vaadin.ui.Form#setItemDataSource(com.vaadin.data.Item)
     */
    @Override
    public void setItemDataSource(Item newDataSource) {
	if (newDataSource instanceof Buffered) {
	    super.setItemDataSource(newDataSource);
	} else {
	    throw new UnsupportedOperationException("data source for BufferedSourceForm must implement the Buffered interface.");
	}
    }

    /**
     * @see com.vaadin.ui.Form#commit()
     */
    @Override
    public void commit() throws SourceException {
	super.commit();
	((Buffered) getItemDataSource()).commit();
    }

    /**
     * @see com.vaadin.ui.Form#discard()
     */
    @Override
    public void discard() throws SourceException {
	super.discard();
	((Buffered) getItemDataSource()).discard();
    }
}
