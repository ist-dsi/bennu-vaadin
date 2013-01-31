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
package pt.ist.vaadinframework.ui;

import java.util.ResourceBundle;

import pt.ist.vaadinframework.VaadinResourceConstants;
import pt.ist.vaadinframework.data.HintedProperty;
import pt.ist.vaadinframework.data.HintedProperty.Hint;
import pt.utl.ist.fenix.tools.util.i18n.Language;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.TableFieldFactory;

public abstract class AbstractFieldFactory implements FormFieldFactory, TableFieldFactory, VaadinResourceConstants {
	protected final String bundlename;

	private transient ResourceBundle bundle;

	public AbstractFieldFactory(String bundlename) {
		this.bundlename = bundlename;
	}

	protected ResourceBundle getBundle() {
		if (bundle == null) {
			bundle = ResourceBundle.getBundle(bundlename, Language.getLocale());
		}
		return bundle;
	}

	@Override
	public final Field createField(Container container, Object itemId, Object propertyId, Component uiContext) {
		Item item = container.getItem(itemId);
		Field field = makeField(item, propertyId, uiContext);
		return initField(field, item, propertyId, uiContext);
	}

	@Override
	public final Field createField(Item item, Object propertyId, Component uiContext) {
		Field field = makeField(item, propertyId, uiContext);
		return initField(field, item, propertyId, uiContext);
	}

	protected Field initField(Field field, Item item, Object propertyId, Component uiContext) {
		String caption = makeCaption(item, propertyId, uiContext);
		field.setCaption(caption);
		field.setDescription(makeDescription(item, propertyId, uiContext));
		if (item.getItemProperty(propertyId) instanceof HintedProperty) {
			for (Hint hint : ((HintedProperty<?>) item.getItemProperty(propertyId)).getHints()) {
				if (hint.appliesTo(field)) {
					field = hint.applyHint(field);
				}
			}
		}
		return field;
	}

	protected abstract Field makeField(Item item, Object propertyId, Component uiContext);

	protected String makeCaption(Item item, Object propertyId, Component uiContext) {
		return CaptionUtils.makeCaption(getBundle(), item, propertyId, uiContext);
	}

	protected String makeDescription(Item item, Object propertyId, Component uiContext) {
		return CaptionUtils.makeDescription(getBundle(), item, propertyId, uiContext);
	}
}