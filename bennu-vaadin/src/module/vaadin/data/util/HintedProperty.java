package module.vaadin.data.util;

import java.util.Collection;

import com.vaadin.data.Property;
import com.vaadin.ui.Field;

public interface HintedProperty extends Property {
    public interface Hint {
	public Field applyHint(Field field);

	public boolean appliesTo(Field field);
    }

    public Collection<Hint> getHints();
}
