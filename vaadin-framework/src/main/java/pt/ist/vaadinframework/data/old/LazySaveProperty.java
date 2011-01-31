package pt.ist.vaadinframework.data.old;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import com.vaadin.data.util.MethodProperty;

public class LazySaveProperty extends MethodProperty implements ExtendedProperty {
    private static final long serialVersionUID = 4886219001633647364L;

    private Object value = null;

    private boolean required = false;

    private final Set<LayoutHint> hints = new HashSet<LayoutHint>();

    public LazySaveProperty(Object instance, String beanPropertyName) {
	super(instance, beanPropertyName);
    }

    public LazySaveProperty(Class type, Object instance, String getMethodName, String setMethodName) {
	super(type, instance, getMethodName, setMethodName);
    }

    public LazySaveProperty(Class type, Object instance, Method getMethod, Method setMethod) {
	super(type, instance, getMethod, setMethod);
    }

    public LazySaveProperty(Class type, Object instance, String getMethodName, String setMethodName, Object[] getArgs,
	    Object[] setArgs, int setArgumentIndex) {
	super(type, instance, getMethodName, setMethodName, getArgs, setArgs, setArgumentIndex);
    }

    public LazySaveProperty(Class type, Object instance, Method getMethod, Method setMethod, Object[] getArgs, Object[] setArgs,
	    int setArgumentIndex) {
	super(type, instance, getMethod, setMethod, getArgs, setArgs, setArgumentIndex);
    }

    @Override
    public Object getValue() {
	if (value == null) {
	    value = super.getValue();
	}
	return value;
    }

    @Override
    public void setValue(Object newValue) throws ReadOnlyException, ConversionException {
	value = newValue;
    }

    public void save() {
	super.setValue(value);
    }

    @Override
    public boolean isRequired() {
	return required;
    }

    @Override
    public void setRequired(boolean required) {
	this.required = required;
    }

    @Override
    public Set<LayoutHint> getLayoutHints() {
	return hints;
    }

    @Override
    public void addLayoutHint(LayoutHint hint) {
	hints.add(hint);
    }

    @Override
    public void removeLayoutHint(LayoutHint hint) {
	hints.remove(hint);
    }
}
