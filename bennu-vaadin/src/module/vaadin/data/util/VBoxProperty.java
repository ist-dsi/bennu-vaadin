package module.vaadin.data.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import jvstm.VBox;
import pt.ist.fenixWebFramework.services.Service;

import com.vaadin.data.util.AbstractProperty;

public class VBoxProperty extends AbstractProperty implements HintedProperty {
    private VBox<Object> instance;

    private final Class<?> type;

    private final Collection<Hint> hints;

    public VBoxProperty(Object instance, Hint... hints) {
	initVBox(instance);
	this.type = instance.getClass();
	this.hints = Arrays.asList(hints);
    }

    public VBoxProperty(Class<?> type, Hint... hints) {
	initVBox(null);
	this.type = type;
	this.hints = Arrays.asList(hints);
    }

    @Service
    private void initVBox(Object instance) {
	this.instance = new VBox<Object>(instance);
    }

    @Override
    public Object getValue() {
	return instance.get();
    }

    @Override
    public void setValue(Object newValue) throws ReadOnlyException, ConversionException {
	if (isReadOnly()) {
	    throw new ReadOnlyException();
	}
	instance.put(newValue);
	fireValueChange();
    }

    @Override
    public Collection<Hint> getHints() {
	return Collections.unmodifiableCollection(hints);
    }

    @Override
    public Class<?> getType() {
	return type;
    }

}
