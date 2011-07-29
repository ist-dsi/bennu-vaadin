package module.vaadin.data.util.hints;

import module.vaadin.data.util.HintedProperty.Hint;

import com.vaadin.ui.Field;

public class Required implements Hint {
    @Override
    public Field applyHint(Field field) {
	field.setRequired(true);
	return field;
    }

    @Override
    public boolean appliesTo(Field field) {
	return true;
    }

}
