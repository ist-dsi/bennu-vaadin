package pt.ist.vaadinframework.data.hints;

import pt.ist.vaadinframework.data.HintedProperty.Hint;

import com.vaadin.ui.DateField;
import com.vaadin.ui.Field;

public class DateRange implements Hint {
    private final int resolution;

    public DateRange(int resolution) {
	this.resolution = resolution;
    }

    @Override
    public Field applyHint(Field field) {
	((DateField) field).setResolution(resolution);
	return field;
    }

    @Override
    public boolean appliesTo(Field field) {
	return field instanceof DateField;
    }
}
