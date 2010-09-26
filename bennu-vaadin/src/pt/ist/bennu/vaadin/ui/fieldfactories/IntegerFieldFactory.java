package pt.ist.bennu.vaadin.ui.fieldfactories;

import org.apache.commons.lang.StringUtils;

import pt.ist.bennu.vaadin.resources.VaadinResources;

import com.vaadin.data.Property;
import com.vaadin.data.validator.IntegerValidator;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.TextField;

public class IntegerFieldFactory implements FieldFactory {
    @Override
    public Field createField(Object propertyId, Property property, Component uiContext) {
	TextField field = new TextField();
	field.addValidator(new IntegerValidator(VaadinResources.getString(VaadinResources.Keys.INTEGER_VALIDATOR_MESSAGE)));
	field.setNullRepresentation(StringUtils.EMPTY);
	field.setNullSettingAllowed(true);
	field.setMaxLength(Integer.toString(Integer.MAX_VALUE).length() + 1);
	return field;
    }
}
