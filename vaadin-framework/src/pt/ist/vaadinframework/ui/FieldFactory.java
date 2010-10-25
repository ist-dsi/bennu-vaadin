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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.joda.time.Instant;
import org.joda.time.Partial;

import pt.ist.fenixframework.pstm.AbstractDomainObject;
import pt.ist.vaadinframework.data.validator.BigDecimalValidator;
import pt.ist.vaadinframework.data.validator.ByteValidator;
import pt.ist.vaadinframework.data.validator.CharacterValidator;
import pt.ist.vaadinframework.data.validator.DoubleValidator;
import pt.ist.vaadinframework.data.validator.FloatValidator;
import pt.ist.vaadinframework.data.validator.IntegerValidator;
import pt.ist.vaadinframework.data.validator.LongValidator;
import pt.ist.vaadinframework.data.validator.ShortValidator;
import pt.ist.vaadinframework.ui.fields.EnumField;
import pt.ist.vaadinframework.ui.fields.InstantField;
import pt.ist.vaadinframework.ui.fields.MultiLanguageStringField;
import pt.ist.vaadinframework.ui.fields.PartialField;
import pt.ist.vaadinframework.ui.fields.PrimitiveField;
import pt.utl.ist.fenix.tools.util.i18n.Language;
import pt.utl.ist.fenix.tools.util.i18n.MultiLanguageString;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.OptionGroup;
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

    static {
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
		return new OptionGroup();
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
    }

    /**
     * @see com.vaadin.ui.TableFieldFactory#createField(com.vaadin.data.Container,
     *      java.lang.Object, java.lang.Object, com.vaadin.ui.Component)
     */
    @Override
    public Field createField(Container container, Object itemId, Object propertyId, Component uiContext) {
	Property containerProperty = container.getContainerProperty(itemId, propertyId);
	Class<?> type = containerProperty.getType();
	Field field = createFieldByPropertyType(type);
	field.setSizeFull();
	field.setCaption(DefaultFieldFactory.createCaptionByPropertyId(propertyId));
	return field;
    }

    /**
     * @see com.vaadin.ui.FormFieldFactory#createField(com.vaadin.data.Item,
     *      java.lang.Object, com.vaadin.ui.Component)
     */
    @Override
    public Field createField(Item item, Object propertyId, Component uiContext) {
	Class<?> type = item.getItemProperty(propertyId).getType();
	Field field = createFieldByPropertyType(type);
	field.setSizeFull();
	field.setCaption(DefaultFieldFactory.createCaptionByPropertyId(propertyId));
	return field;
    }

    public static Field createFieldByPropertyType(Class<?> type) {
	// Null typed properties can not be edited
	if (type == null) {
	    return null;
	}

	if (AbstractDomainObject.class.isAssignableFrom(type)) {
	    Select select = new Select();
	    for (Object option : SelectOptionProvider.provideFor(type)) {
		select.addItem(option);
	    }
	    return select;
	}

	// Item field
	if (Item.class.isAssignableFrom(type)) {
	    return new Form();
	}

	// Enum field
	if (Enum.class.isAssignableFrom(type)) {
	    return new EnumField((Class<? extends Enum<?>>) type);
	}

	// Multilanguage field
	if (MultiLanguageString.class.isAssignableFrom(type)) {
	    return new MultiLanguageStringField(Language.pt, Language.en);
	}

	// Date field
	if (Date.class.isAssignableFrom(type)) {
	    final DateField df = new DateField();
	    df.setResolution(DateField.RESOLUTION_DAY);
	    return df;
	}

	// joda-time types
	if (Instant.class.isAssignableFrom(type)) {
	    InstantField field = new InstantField();
	    field.setResolution(DateField.RESOLUTION_SEC);
	    return field;
	}

	if (Partial.class.isAssignableFrom(type)) {
	    PartialField field = new PartialField();
	    field.setResolution(DateField.RESOLUTION_DAY);
	    return field;
	}

	// TODO: Interval, Duration, Period

	// Boolean field
	if (Boolean.class.isAssignableFrom(type)) {
	    return new CheckBox();
	}

	TextField text = new TextField();
	text.setNullSettingAllowed(true);
	text.setNullRepresentation(StringUtils.EMPTY);
	return text;
    }

    public static boolean addClassFactory(Class<?> type, FieldMaker maker) {
	if (!factories.containsKey(type)) {
	    factories.put(type, maker);
	    return true;
	}
	return false;
    }
}
