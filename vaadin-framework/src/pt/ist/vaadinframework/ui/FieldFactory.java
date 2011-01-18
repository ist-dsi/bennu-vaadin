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
package pt.ist.vaadinframework.ui;

import java.math.BigDecimal;
import java.net.URL;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import pt.ist.fenixframework.pstm.AbstractDomainObject;
import pt.ist.vaadinframework.data.validator.BigDecimalValidator;
import pt.ist.vaadinframework.data.validator.ByteValidator;
import pt.ist.vaadinframework.data.validator.CharacterValidator;
import pt.ist.vaadinframework.data.validator.DoubleValidator;
import pt.ist.vaadinframework.data.validator.FloatValidator;
import pt.ist.vaadinframework.data.validator.IntegerValidator;
import pt.ist.vaadinframework.data.validator.LongValidator;
import pt.ist.vaadinframework.data.validator.ShortValidator;
import pt.ist.vaadinframework.data.validator.URLValidator;
import pt.ist.vaadinframework.ui.fields.EnumField;
import pt.ist.vaadinframework.ui.fields.MultiLanguageStringField;
import pt.ist.vaadinframework.ui.fields.PopupDateTimeField;
import pt.ist.vaadinframework.ui.fields.PopupLocalDateField;
import pt.ist.vaadinframework.ui.fields.PrimitiveField;
import pt.utl.ist.fenix.tools.util.i18n.Language;
import pt.utl.ist.fenix.tools.util.i18n.MultiLanguageString;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Validator;
import com.vaadin.data.util.DomainProperty;
import com.vaadin.data.util.DomainRelation;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.Select;
import com.vaadin.ui.TableFieldFactory;
import com.vaadin.ui.TextField;

/**
 * @author Pedro Santos (pedro.miguel.santos@ist.utl.pt)
 */
@SuppressWarnings("serial")
public class FieldFactory implements FormFieldFactory, TableFieldFactory {
    public interface FieldMaker {
	public Field createField(Object propertyId, Property property, Component uiContext);
    }

    private static Map<Class<?>, FieldMaker> factories = new HashMap<Class<?>, FieldMaker>();

    private final Map<Class<?>, FieldMaker> customFactories = new HashMap<Class<?>, FieldMaker>();

    static {
	addClassFactory(AbstractDomainObject.class, new FieldMaker() {
	    @Override
	    public Field createField(Object propertyId, Property property, Component uiContext) {
		Select select = new Select();
		if (property instanceof DomainProperty<?>) {
		    DomainProperty<?> domainProperty = (DomainProperty<?>) property;
		    if (domainProperty.getPossibleValues() != null) {
			for (Object object : domainProperty.getPossibleValues()) {
			    select.addItem(object);
			}
		    }
		    if (domainProperty instanceof DomainRelation) {
			select.setMultiSelect(true);
		    }
		    select.setNullSelectionAllowed(!domainProperty.isRequired());
		}
		return select;
	    }
	});
	addClassFactory(Byte.class, new FieldMaker() {
	    @Override
	    public Field createField(Object propertyId, Property property, Component uiContext) {
		return new PrimitiveField(new ByteValidator(), Byte.toString(Byte.MAX_VALUE).length() + 1);
	    }
	});
	addClassFactory(Short.class, new FieldMaker() {
	    @Override
	    public Field createField(Object propertyId, Property property, Component uiContext) {
		return new PrimitiveField(new ShortValidator(), Short.toString(Short.MAX_VALUE).length() + 1);
	    }
	});
	addClassFactory(Integer.class, new FieldMaker() {
	    @Override
	    public Field createField(Object propertyId, Property property, Component uiContext) {
		return new PrimitiveField(new IntegerValidator(), Integer.toString(Integer.MAX_VALUE).length() + 1);
	    }
	});
	addClassFactory(Long.class, new FieldMaker() {
	    @Override
	    public Field createField(Object propertyId, Property property, Component uiContext) {
		return new PrimitiveField(new LongValidator(), Long.toString(Long.MAX_VALUE).length() + 1);
	    }
	});
	addClassFactory(Float.class, new FieldMaker() {
	    @Override
	    public Field createField(Object propertyId, Property property, Component uiContext) {
		return new PrimitiveField(new FloatValidator(), Float.toString(Float.MAX_VALUE).length() + 1);
	    }
	});
	addClassFactory(Double.class, new FieldMaker() {
	    @Override
	    public Field createField(Object propertyId, Property property, Component uiContext) {
		return new PrimitiveField(new DoubleValidator(), Double.toString(Double.MAX_VALUE).length() + 1);
	    }
	});
	addClassFactory(Boolean.class, new FieldMaker() {
	    @Override
	    public Field createField(Object propertyId, Property property, Component uiContext) {
		return new CheckBox();
	    }
	});
	addClassFactory(Character.class, new FieldMaker() {
	    @Override
	    public Field createField(Object propertyId, Property property, Component uiContext) {
		return new PrimitiveField(new CharacterValidator(), 1);
	    }
	});
	addClassFactory(String.class, new FieldMaker() {
	    @Override
	    public Field createField(Object propertyId, Property property, Component uiContext) {
		TextField field = new TextField();
		field.setNullSettingAllowed(true);
		field.setNullRepresentation(StringUtils.EMPTY);
		return field;
	    }
	});
	addClassFactory(BigDecimal.class, new FieldMaker() {
	    @Override
	    public Field createField(Object propertyId, Property property, Component uiContext) {
		return new PrimitiveField(new BigDecimalValidator(), -1);
	    }
	});
	addClassFactory(Enum.class, new FieldMaker() {
	    @Override
	    @SuppressWarnings("unchecked")
	    public Field createField(Object propertyId, Property property, Component uiContext) {
		return new EnumField((Class<? extends Enum<?>>) property.getType());
	    }
	});
	addClassFactory(Date.class, new FieldMaker() {
	    @Override
	    public Field createField(Object propertyId, Property property, Component uiContext) {
		DateField field = new DateField();
		field.setResolution(DateField.RESOLUTION_DAY);
		return field;
	    }
	});
	addClassFactory(Item.class, new FieldMaker() {
	    @Override
	    public Field createField(Object propertyId, Property property, Component uiContext) {
		Form form = new Form();
		form.setImmediate(true);
		form.setFormFieldFactory(new FieldFactory());
		form.setItemDataSource((Item) property.getValue());
		return form;
	    }
	});
	addClassFactory(MultiLanguageString.class, new FieldMaker() {
	    @Override
	    public Field createField(Object propertyId, Property property, Component uiContext) {
		return new MultiLanguageStringField(Language.pt, Language.en);
	    }
	});
	addClassFactory(DateTime.class, new FieldMaker() {
	    @Override
	    public Field createField(Object propertyId, Property property, Component uiContext) {
		PopupDateTimeField field = new PopupDateTimeField();
		field.setResolution(DateField.RESOLUTION_SEC);
		return field;
	    }
	});
	// addClassFactory(Partial.class, new FieldMaker() {
	// @Override
	// public Field createField(Object propertyId, Property property,
	// Component uiContext) {
	// PartialField field = new PartialField();
	// field.setResolution(DateField.RESOLUTION_DAY);
	// return field;
	// }
	// });
	addClassFactory(LocalDate.class, new FieldMaker() {
	    @Override
	    public Field createField(Object propertyId, Property property, Component uiContext) {
		PopupLocalDateField field = new PopupLocalDateField();
		field.setResolution(DateField.RESOLUTION_DAY);
		return field;
	    }
	});
	addClassFactory(URL.class, new FieldMaker() {
	    @Override
	    public Field createField(Object propertyId, Property property, Component uiContext) {
		TextField field = new TextField();
		field.setNullSettingAllowed(true);
		field.setNullRepresentation(StringUtils.EMPTY);
		field.addValidator(new URLValidator());
		return field;
	    }
	});
    }

    /**
     * @see com.vaadin.ui.TableFieldFactory#createField(com.vaadin.data.Container,
     *      java.lang.Object, java.lang.Object, com.vaadin.ui.Component)
     */
    @Override
    public Field createField(Container container, Object itemId, Object propertyId, Component uiContext) {
	Property property = container.getContainerProperty(itemId, propertyId);
	Field field = createField(propertyId, property, uiContext);
	setCommonProperties(field, propertyId, property);
	return field;
    }

    /**
     * @see com.vaadin.ui.FormFieldFactory#createField(com.vaadin.data.Item,
     *      java.lang.Object, com.vaadin.ui.Component)
     */
    @Override
    public Field createField(Item item, Object propertyId, Component uiContext) {
	Property property = item.getItemProperty(propertyId);
	Field field = createField(propertyId, property, uiContext);
	setCommonProperties(field, propertyId, property);
	return field;
    }

    private Field createField(Object propertyId, Property property, Component uiContext) {
	Class<?> type = property.getType();
	if (property instanceof DomainProperty) {
	    DomainProperty<?> domainProperty = (DomainProperty<?>) property;
	    if (domainProperty.getPossibleValues() != null) {
		Select select = new Select();
		for (Object object : domainProperty.getPossibleValues()) {
		    select.addItem(object);
		}
		if (domainProperty instanceof DomainRelation) {
		    select.setMultiSelect(true);
		}
		select.setNullSelectionAllowed(!domainProperty.isRequired());
		return select;
	    }
	}
	if (type != null) {
	    if (Collection.class.isAssignableFrom(type)) {
		if (property instanceof DomainRelation) {
		    type = ((DomainRelation) property).getElementType();
		}
	    }
	    if (customFactories.containsKey(type)) {
		return customFactories.get(type).createField(propertyId, property, uiContext);
	    }
	    if (factories.containsKey(type)) {
		return factories.get(type).createField(propertyId, property, uiContext);
	    }
	    for (Class<?> clazz : customFactories.keySet()) {
		if (clazz.isAssignableFrom(type)) {
		    customFactories.put(type, customFactories.get(clazz));
		    return customFactories.get(type).createField(propertyId, property, uiContext);
		}
	    }
	    for (Class<?> clazz : factories.keySet()) {
		if (clazz.isAssignableFrom(type)) {
		    factories.put(type, factories.get(clazz));
		    return factories.get(type).createField(propertyId, property, uiContext);
		}
	    }
	}
	return new TextField();
    }

    private void setCommonProperties(Field field, Object propertyId, Property property) {
	if (field != null) {
	    if (property instanceof DomainProperty<?>) {
		DomainProperty<?> domainProperty = (DomainProperty<?>) property;
		field.setRequired(domainProperty.isRequired());
		for (Validator validator : domainProperty.getValidators()) {
		    field.addValidator(validator);
		}
	    }
	    setCaption(field, propertyId, property);
	    setDescription(field, propertyId, property);
	}
    }

    private void setCaption(Field field, Object propertyId, Property property) {
	if (property instanceof DomainProperty) {
	    String label = ((DomainProperty) property).getLabel();
	    if (label != null) {
		field.setCaption(label);
	    }
	} else {
	    // String key = property.getType().getName() + "." + propertyId;
	    // if (bundle.containsKey(key)) {
	    // field.setCaption(bundle.getString(key));
	    // } else {
	    field.setCaption(DefaultFieldFactory.createCaptionByPropertyId(propertyId));
	    // }
	}
    }

    private void setDescription(Field field, Object propertyId, Property property) {
	// String key = property.getType().getName() + "." + propertyId +
	// ".description";
	// if (bundle.containsKey(key)) {
	// field.setDescription(bundle.getString(key));
	// }
    }

    public static boolean addClassFactory(Class<?> type, FieldMaker maker) {
	if (!factories.containsKey(type)) {
	    factories.put(type, maker);
	    return true;
	}
	return false;
    }

    public boolean addCustomClassFactory(Class<?> type, FieldMaker maker) {
	if (!customFactories.containsKey(type)) {
	    customFactories.put(type, maker);
	    return true;
	}
	return false;
    }
}
