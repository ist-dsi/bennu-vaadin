package pt.ist.bennu.vaadin.ui.fieldfactories;

import pt.ist.bennu.vaadin.data.ExtendedProperty;
import pt.ist.bennu.vaadin.data.ExtendedProperty.LayoutHint;

import com.vaadin.data.Property;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Field;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.PopupDateField;

public class DateFieldFactory implements FieldFactory {
    @Override
    public Field createField(Object propertyId, Property property, Component uiContext) {
	DateField field;
	if (property instanceof ExtendedProperty) {
	    ExtendedProperty extended = (ExtendedProperty) property;
	    if (extended.getLayoutHints().contains(LayoutHint.LOTS_OF_SPACE)) {
		field = new InlineDateField();
	    } else {
		field = new PopupDateField();
	    }
	    if (extended.getLayoutHints().contains(LayoutHint.DATE_RESOLUTION_MSEC)) {
		field.setResolution(DateField.RESOLUTION_MSEC);
	    } else if (extended.getLayoutHints().contains(LayoutHint.DATE_RESOLUTION_SEC)) {
		field.setResolution(DateField.RESOLUTION_SEC);
	    } else if (extended.getLayoutHints().contains(LayoutHint.DATE_RESOLUTION_MIN)) {
		field.setResolution(DateField.RESOLUTION_MIN);
	    } else if (extended.getLayoutHints().contains(LayoutHint.DATE_RESOLUTION_HOUR)) {
		field.setResolution(DateField.RESOLUTION_HOUR);
	    } else if (extended.getLayoutHints().contains(LayoutHint.DATE_RESOLUTION_DAY)) {
		field.setResolution(DateField.RESOLUTION_DAY);
	    } else if (extended.getLayoutHints().contains(LayoutHint.DATE_RESOLUTION_MONTH)) {
		field.setResolution(DateField.RESOLUTION_MONTH);
	    } else if (extended.getLayoutHints().contains(LayoutHint.DATE_RESOLUTION_YEAR)) {
		field.setResolution(DateField.RESOLUTION_YEAR);
	    }
	} else {
	    field = new PopupDateField();
	}
	return field;
    }
}
