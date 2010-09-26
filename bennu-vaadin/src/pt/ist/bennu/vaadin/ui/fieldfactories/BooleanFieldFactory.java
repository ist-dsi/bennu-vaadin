package pt.ist.bennu.vaadin.ui.fieldfactories;

import com.vaadin.data.Property;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;

public class BooleanFieldFactory implements FieldFactory {
    @Override
    public Field createField(Object propertyId, Property property, Component uiContext) {
	return new CheckBox();
    }
}
