package pt.ist.vaadinframework.data.hints;

import pt.ist.vaadinframework.data.HintedProperty.Hint;

import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Field;
import com.vaadin.ui.PopupDateField;

public class InputPrompt implements Hint {
	private final String inputPrompt;

	public InputPrompt(String inputPrompt) {
		this.inputPrompt = inputPrompt;
	}

	@Override
	public Field applyHint(Field field) {
		if (field instanceof AbstractTextField) {
			((AbstractTextField) field).setInputPrompt(inputPrompt);
		}
		if (field instanceof PopupDateField) {
			((PopupDateField) field).setInputPrompt(inputPrompt);
		}
		return field;
	}

	@Override
	public boolean appliesTo(Field field) {
		return field instanceof AbstractTextField || field instanceof PopupDateField;
	}
}
