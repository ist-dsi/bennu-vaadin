package pt.ist.vaadinframework.data.hints;

import pt.ist.vaadinframework.data.HintedProperty.Hint;

import com.vaadin.ui.Field;

public class Disable implements Hint {
    @Override
    public Field applyHint(Field field) {
	field.setEnabled(false);
	return field;
    }

    @Override
    public boolean appliesTo(Field field) {
	return true;
    }
}
