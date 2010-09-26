package pt.ist.bennu.vaadin.ui.fieldfactories;

import com.vaadin.data.Property;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;

public interface FieldFactory {
    public Field createField(Object propertyId, Property property, Component uiContext);
}
