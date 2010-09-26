package pt.ist.bennu.vaadin.ui.fieldfactories;

import org.apache.commons.lang.StringUtils;

import com.vaadin.data.Property;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.TextField;

public class TextFieldFactory implements FieldFactory {
    @Override
    public Field createField(Object propertyId, Property property, Component uiContext) {
	TextField text = new TextField();
	text.setNullRepresentation(StringUtils.EMPTY);
	text.setNullSettingAllowed(true);
	return text;
    }
}
