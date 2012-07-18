package pt.ist.vaadinframework.data.hints;

import pt.ist.vaadinframework.data.HintedProperty.Hint;

import com.vaadin.data.Validator;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Field;

public class Range implements Hint {
    public class RangeValidator implements Validator {
	public RangeValidator(int min, int max) {
	}

	@Override
	public void validate(Object value) throws InvalidValueException {
	    // TODO Auto-generated method stub

	}

	@Override
	public boolean isValid(Object value) {
	    // TODO Auto-generated method stub
	    return false;
	}

    }

    private int min;
    private int max;

    @Override
    public Field applyHint(Field field) {
	if (field instanceof AbstractTextField) {
	    ((AbstractTextField) field).setColumns(Integer.toString(max).length());
	    ((AbstractTextField) field).setMaxLength(Integer.toString(max).length());
	}
	field.addValidator(new RangeValidator(min, max));
	return field;
    }

    @Override
    public boolean appliesTo(Field field) {
	return true;
    }
}
