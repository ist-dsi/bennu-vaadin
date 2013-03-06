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

import java.math.BigDecimal;
import java.net.URL;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import pt.ist.fenixframework.pstm.AbstractDomainObject;
import pt.ist.vaadinframework.data.AbstractBufferedContainer;
import pt.ist.vaadinframework.data.validator.BigDecimalValidator;
import pt.ist.vaadinframework.data.validator.ByteValidator;
import pt.ist.vaadinframework.data.validator.CharacterValidator;
import pt.ist.vaadinframework.data.validator.DoubleValidator;
import pt.ist.vaadinframework.data.validator.FloatValidator;
import pt.ist.vaadinframework.data.validator.IntegerValidator;
import pt.ist.vaadinframework.data.validator.LongValidator;
import pt.ist.vaadinframework.data.validator.ShortValidator;
import pt.ist.vaadinframework.data.validator.URLValidator;
import pt.ist.vaadinframework.ui.fields.ContainerEditor;
import pt.ist.vaadinframework.ui.fields.EnumField;
import pt.ist.vaadinframework.ui.fields.MultiLanguageStringField;
import pt.ist.vaadinframework.ui.fields.PopupDateTimeField;
import pt.ist.vaadinframework.ui.fields.PopupLocalDateField;
import pt.ist.vaadinframework.ui.fields.PrimitiveField;
import pt.utl.ist.fenix.tools.util.i18n.Language;
import pt.utl.ist.fenix.tools.util.i18n.MultiLanguageString;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Select;
import com.vaadin.ui.TextField;

/**
 * @author Pedro Santos (pedro.miguel.santos@ist.utl.pt)
 * 
 */
public class DefaultFieldFactory extends AbstractFieldFactory {
    public DefaultFieldFactory(String bundlename) {
        super(bundlename);
    }

    /**
     * @see pt.ist.vaadinframework.ui.AbstractFieldFactory#makeField(com.vaadin.data.Item, java.lang.Object,
     *      com.vaadin.ui.Component)
     */
    @Override
    protected Field makeField(Item item, Object propertyId, Component uiContext) {
        Class<?> type = item.getItemProperty(propertyId).getType();
        if (String.class.isAssignableFrom(type)) {
            TextField field = new TextField();
            field.setNullSettingAllowed(true);
            field.setNullRepresentation(StringUtils.EMPTY);
            return field;
        }
        if (Collection.class.isAssignableFrom(type) && item.getItemProperty(propertyId) instanceof AbstractBufferedContainer) {
            return new ContainerEditor<Object>(this, bundlename,
                    ((AbstractBufferedContainer<?, ?, ?>) item.getItemProperty(propertyId)).getElementType());
        }
        if (AbstractDomainObject.class.isAssignableFrom(type)) {
            Select select = new Select();
            select.setWidth(100, Sizeable.UNITS_PERCENTAGE);
            select.setImmediate(true);
            return select;
        }
        if (Collection.class.isAssignableFrom(type)) {
            OptionGroup group = new OptionGroup();
            group.setMultiSelect(true);
            group.setWidth(100, Sizeable.UNITS_PERCENTAGE);
            group.setImmediate(true);
            return group;
        }
        if (Byte.class.isAssignableFrom(type)) {
            return new PrimitiveField(new ByteValidator(), Byte.toString(Byte.MAX_VALUE).length() + 1);
        }
        if (Short.class.isAssignableFrom(type)) {
            return new PrimitiveField(new ShortValidator(), Short.toString(Short.MAX_VALUE).length() + 1);
        }
        if (Integer.class.isAssignableFrom(type)) {
            return new PrimitiveField(new IntegerValidator(), Integer.toString(Integer.MAX_VALUE).length() + 1);
        }
        if (Long.class.isAssignableFrom(type)) {
            return new PrimitiveField(new LongValidator(), Long.toString(Long.MAX_VALUE).length() + 1);
        }
        if (Float.class.isAssignableFrom(type)) {
            return new PrimitiveField(new FloatValidator(), Float.toString(Float.MAX_VALUE).length() + 1);
        }
        if (Double.class.isAssignableFrom(type)) {
            return new PrimitiveField(new DoubleValidator(), Double.toString(Double.MAX_VALUE).length() + 1);
        }
        if (Boolean.class.isAssignableFrom(type)) {
            return new CheckBox();
        }
        if (Character.class.isAssignableFrom(type)) {
            return new PrimitiveField(new CharacterValidator(), 1);
        }
        if (BigDecimal.class.isAssignableFrom(type)) {
            return new PrimitiveField(new BigDecimalValidator(), -1);
        }
        if (Enum.class.isAssignableFrom(type)) {
            return new EnumField((Class<? extends Enum<?>>) item.getItemProperty(propertyId).getType());
        }
        if (Date.class.isAssignableFrom(type)) {
            DateField field = new DateField();
            field.setResolution(DateField.RESOLUTION_DAY);
            return field;
        }
        if (Item.class.isAssignableFrom(type)) {
            Form form = new Form() {
                @Override
                public void setPropertyDataSource(Property newDataSource) {
                    setItemDataSource((Item) newDataSource);
                };
            };
            form.setImmediate(true);
            form.setFormFieldFactory(this);
            return form;
        }
        if (MultiLanguageString.class.isAssignableFrom(type)) {
            return new MultiLanguageStringField(bundlename, Language.pt, Language.en);
        }
        if (DateTime.class.isAssignableFrom(type)) {
            PopupDateTimeField field = new PopupDateTimeField();
            field.setResolution(DateField.RESOLUTION_SEC);
            return field;
        }
        if (LocalDate.class.isAssignableFrom(type)) {
            PopupLocalDateField field = new PopupLocalDateField();
            field.setResolution(DateField.RESOLUTION_DAY);
            return field;
        }
        if (URL.class.isAssignableFrom(type)) {
            TextField field = new TextField();
            field.setNullSettingAllowed(true);
            field.setNullRepresentation(StringUtils.EMPTY);
            field.addValidator(new URLValidator());
            return field;
        }
        Select select = new Select();
        select.setWidth(100, Sizeable.UNITS_PERCENTAGE);
        select.setImmediate(true);
        return select;
    }
}
