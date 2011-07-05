package module.vaadin.data.util;

import jvstm.VBox;
import pt.ist.fenixWebFramework.services.Service;

import com.vaadin.data.util.AbstractProperty;

public class VBoxProperty extends AbstractProperty {
    private VBox<Object> instance;

    private final Class<?> type;

    public VBoxProperty(Object instance) {
	initVBox(instance);
	this.type = instance.getClass();
    }

    public VBoxProperty(Class<?> type) {
	initVBox(null);
	this.type = type;
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
    }

    @Override
    public Class<?> getType() {
	return type;
    }

}
