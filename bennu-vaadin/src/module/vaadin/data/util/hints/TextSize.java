package module.vaadin.data.util.hints;

import module.vaadin.data.util.HintedProperty.Hint;

import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Field;
import com.vaadin.ui.TextArea;

public class TextSize implements Hint {
    private final int rows;

    public TextSize(int rows) {
	this.rows = rows;
    }

    @Override
    public Field applyHint(Field field) {
	if (field instanceof TextArea) {
	    ((TextArea) field).setRows(rows);
	}
	return field;
    }

    @Override
    public boolean appliesTo(Field field) {
	return field instanceof AbstractTextField;
    }

}
