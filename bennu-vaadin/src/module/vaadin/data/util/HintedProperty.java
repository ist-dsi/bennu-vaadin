package module.vaadin.data.util;

import java.util.Collection;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ReadOnlyStatusChangeNotifier;
import com.vaadin.data.Property.ValueChangeNotifier;
import com.vaadin.ui.Field;

public interface HintedProperty extends Property, ValueChangeNotifier, ReadOnlyStatusChangeNotifier {
    public interface Hint {
	public Field applyHint(Field field);

	public boolean appliesTo(Field field);
    }

    public Collection<Hint> getHints();
}
