package pt.ist.vaadinframework.data.hints;

import pt.ist.vaadinframework.data.HintedProperty.Hint;

import com.vaadin.terminal.Resource;
import com.vaadin.ui.Field;

public class Decorate implements Hint {
	private Resource icon = null;
	private String styles = "";

	public Decorate() {
	}

	public Decorate icon(Resource icon) {
		Decorate decorate = new Decorate();
		decorate.icon = icon;
		decorate.styles = styles;
		return decorate;
	}

	public Decorate style(String style) {
		Decorate decorate = new Decorate();
		decorate.icon = icon;
		decorate.styles = (styles + " " + style).trim();
		return decorate;
	}

	@Override
	public Field applyHint(Field field) {
		field.setIcon(icon);
		field.setStyleName(styles);
		return field;
	}

	@Override
	public boolean appliesTo(Field field) {
		return true;
	}
}
