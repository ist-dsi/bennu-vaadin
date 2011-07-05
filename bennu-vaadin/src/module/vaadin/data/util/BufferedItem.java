package module.vaadin.data.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jvstm.CommitException;
import jvstm.cps.ConsistencyException;
import pt.ist.fenixWebFramework.services.Service;
import pt.ist.fenixframework.pstm.AbstractDomainObject.UnableToDetermineIdException;
import pt.ist.fenixframework.pstm.IllegalWriteException;

import com.vaadin.data.Buffered;
import com.vaadin.data.BufferedValidatable;
import com.vaadin.data.Property;
import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.AbstractProperty;
import com.vaadin.data.util.PropertysetItem;

public abstract class BufferedItem<PropertyId, Type> extends PropertysetItem implements Property, BufferedValidatable,
	Property.ReadOnlyStatusChangeNotifier, Property.ValueChangeNotifier {
    public class BufferedProperty extends AbstractProperty {
	private final PropertyId propertyId;

	private final Class<?> type;

	public BufferedProperty(PropertyId propertyId, Class<?> type) {
	    this.propertyId = propertyId;
	    this.type = type;
	}

	@Override
	public Object getValue() {
	    return getPropertyValue(propertyId);
	}

	@Override
	public void setValue(Object newValue) throws ReadOnlyException, ConversionException {
	    if (isReadOnly()) {
		throw new ReadOnlyException();
	    }
	    setPropertyValue(propertyId, newValue);
	    fireValueChange();
	}

	@Override
	public Class<?> getType() {
	    return type;
	}

	@Override
	public String toString() {
	    return getValue() != null ? getValue().toString() : null;
	}
    }

    private final Property value;

    private final Map<Object, Object> propertyValues = new HashMap<Object, Object>();

    private ItemConstructor<PropertyId, Type> constructor;

    private ItemWriter<PropertyId, Type> writer;

    private boolean readThrough = true;

    private boolean writeThrough = true;

    private boolean invalidAllowed = true;

    private boolean invalidCommited = false;

    private boolean modified = false;

    private List<Validator> validators;

    public BufferedItem(Property value) {
	this.value = value;
    }

    protected Object getPropertyValue(PropertyId propertyId) {
	if (isReadThrough() && !isModified()) {
	    Type value = getValue();
	    propertyValues.put(propertyId, value == null ? null : readPropertyValue(value, propertyId));
	}
	return propertyValues.get(propertyId);
    }

    protected abstract Object readPropertyValue(Type host, PropertyId propertyId);

    protected void setPropertyValue(PropertyId propertyId, Object newValue) throws SourceException, InvalidValueException {
	propertyValues.put(propertyId, newValue);
	if (isWriteThrough()) {
	    commit(Collections.singletonList(propertyId));
	} else {
	    modified = true;
	}
    }

    protected abstract void writePropertyValue(Type host, PropertyId propertyId, Object newValue);

    @Override
    public Type getValue() {
	return (Type) value.getValue();
    }

    @Override
    public void setValue(Object newValue) throws ReadOnlyException, ConversionException {
	value.setValue(newValue);
    }

    @Override
    public Class<? extends Type> getType() {
	return (Class<? extends Type>) value.getType();
    }

    @Override
    public boolean isReadOnly() {
	return value.isReadOnly();
    }

    @Override
    public void setReadOnly(boolean newStatus) {
	value.setReadOnly(newStatus);
    }

    @Override
    public void addListener(ReadOnlyStatusChangeListener listener) {
	if (value instanceof ReadOnlyStatusChangeNotifier) {
	    ((ReadOnlyStatusChangeNotifier) value).addListener(listener);
	} else {
	    throw new UnsupportedOperationException("Underlying property is not a ReadOnlyStatusChangeNotifier");
	}
    }

    @Override
    public void removeListener(ReadOnlyStatusChangeListener listener) {
	if (value instanceof ReadOnlyStatusChangeNotifier) {
	    ((ReadOnlyStatusChangeNotifier) value).removeListener(listener);
	} else {
	    throw new UnsupportedOperationException("Underlying property is not a ReadOnlyStatusChangeNotifier");
	}
    }

    @Override
    public void addListener(ValueChangeListener listener) {
	if (value instanceof ValueChangeNotifier) {
	    ((ValueChangeNotifier) value).addListener(listener);
	} else {
	    throw new UnsupportedOperationException("Underlying property is not a ValueChangeNotifier");
	}
    }

    @Override
    public void removeListener(ValueChangeListener listener) {
	if (value instanceof ValueChangeNotifier) {
	    ((ValueChangeNotifier) value).removeListener(listener);
	} else {
	    throw new UnsupportedOperationException("Underlying property is not a ValueChangeNotifier");
	}
    }

    @Override
    public boolean addItemProperty(Object propertyId, Property property) {
	Type value = getValue();
	propertyValues.put(propertyId, value == null ? null : readPropertyValue(value, (PropertyId) propertyId));
	return super.addItemProperty(propertyId, property);
    }

    @Override
    public boolean removeItemProperty(Object propertyId) {
	propertyValues.remove(propertyId);
	return super.removeItemProperty(propertyId);
    }

    @Override
    public Collection<PropertyId> getItemPropertyIds() {
	return (Collection<PropertyId>) super.getItemPropertyIds();
    }

    public void setConstructor(ItemConstructor<PropertyId, Type> constructor) {
	this.constructor = constructor;
    }

    public void setWriter(ItemWriter<PropertyId, Type> writer) {
	this.writer = writer;
    }

    @Override
    public void commit() throws SourceException, InvalidValueException {
	commit(getItemPropertyIds());
    }

    @Service
    private void commit(Collection<PropertyId> savingPropertyIds) throws SourceException, InvalidValueException {
	LinkedList<PropertyId> savingIds = new LinkedList<PropertyId>(savingPropertyIds);
	if (getValue() == null) {
	    // construction
	    if (constructor != null) {
		try {
		    Method method = constructor.getClass().getMethod("construct",
			    getArgumentTypes(constructor.getOrderedArguments()));
		    setValue(method.invoke(constructor, readArguments(constructor.getOrderedArguments())));
		    savingIds.removeAll(Arrays.asList(constructor.getOrderedArguments()));
		} catch (SecurityException e) {
		    handleException(e);
		    throw new SourceException(this, e);
		} catch (NoSuchMethodException e) {
		    handleException(e);
		    throw new SourceException(this, e);
		} catch (IllegalArgumentException e) {
		    handleException(e);
		    throw new SourceException(this, e);
		} catch (IllegalAccessException e) {
		    handleException(e);
		    throw new SourceException(this, e);
		} catch (InvocationTargetException e) {
		    handleException(e);
		    throw new SourceException(this, e);
		}
	    } else {
		try {
		    setValue(getType().newInstance());
		} catch (InstantiationException e) {
		    handleException(e);
		    throw new SourceException(this, e);
		} catch (IllegalAccessException e) {
		    handleException(e);
		    throw new SourceException(this, e);
		}
	    }
	} else {
	    if (writer != null) {
		try {
		    LinkedList<Class<?>> argumentTypes = new LinkedList<Class<?>>();
		    argumentTypes.add(getType());
		    argumentTypes.addAll(Arrays.asList(getArgumentTypes(writer.getOrderedArguments())));
		    Method method = writer.getClass().getMethod("write", argumentTypes.toArray(new Class<?>[0]));
		    LinkedList<Object> argumentValues = new LinkedList<Object>();
		    argumentValues.add(getValue());
		    argumentValues.addAll(Arrays.asList(readArguments(writer.getOrderedArguments())));
		    method.invoke(writer, argumentValues.toArray(new Object[0]));
		    savingIds.removeAll(Arrays.asList(writer.getOrderedArguments()));
		} catch (IllegalArgumentException e) {
		    handleException(e);
		    throw new SourceException(this, e);
		} catch (IllegalAccessException e) {
		    handleException(e);
		    throw new SourceException(this, e);
		} catch (InvocationTargetException e) {
		    handleException(e);
		    throw new SourceException(this, e);
		} catch (SecurityException e) {
		    handleException(e);
		    throw new SourceException(this, e);
		} catch (NoSuchMethodException e) {
		    handleException(e);
		    throw new SourceException(this, e);
		}
	    }
	}
	LinkedList<Throwable> problems = null;
	for (PropertyId propertyId : savingIds) {
	    try {
		writePropertyValue(getValue(), propertyId, propertyValues.get(propertyId));
	    } catch (Throwable e) {
		handleException(e);
		if (problems == null) {
		    problems = new LinkedList<Throwable>();
		}
		problems.add(e);
	    }
	}
	if (problems != null) {
	    throw new SourceException(this, problems.toArray(new Throwable[0]));
	}
    }

    private void handleException(Throwable throwable) {
	// This is a little hackish but is somewhat forced by the
	// combination of architectures of both vaadin and the jvstm
	if (throwable instanceof IllegalWriteException) {
	    throw (IllegalWriteException) throwable;
	} else if (throwable instanceof ConsistencyException) {
	    throw (ConsistencyException) throwable;
	} else if (throwable instanceof UnableToDetermineIdException) {
	    throw (UnableToDetermineIdException) throwable;
	} else if (throwable instanceof CommitException) {
	    throw (CommitException) throwable;
	} else if (throwable instanceof Buffered.SourceException) {
	    for (Throwable cause : ((Buffered.SourceException) throwable).getCauses()) {
		handleException(cause);
	    }
	} else if (throwable.getCause() != null) {
	    handleException(throwable.getCause());
	}
    }

    private Class<?>[] getArgumentTypes(PropertyId[] argumentIds) {
	Class<?>[] types = new Class<?>[argumentIds.length];
	for (int i = 0; i < argumentIds.length; i++) {
	    types[i] = getItemProperty(argumentIds[i]).getType();
	}
	return types;
    }

    private Object[] readArguments(PropertyId[] argumentIds) {
	Object[] arguments = new Object[argumentIds.length];
	for (int i = 0; i < argumentIds.length; i++) {
	    arguments[i] = propertyValues.get(argumentIds[i]);
	}
	return arguments;
    }

    @Override
    public void discard() throws SourceException {
	Type value = getValue();
	for (PropertyId propertyId : getItemPropertyIds()) {
	    propertyValues.put(propertyId, value == null ? null : readPropertyValue(value, propertyId));
	}
	modified = false;
    }

    @Override
    public boolean isWriteThrough() {
	return writeThrough;
    }

    @Override
    public void setWriteThrough(boolean writeThrough) throws SourceException, InvalidValueException {
	if (writeThrough != this.writeThrough) {
	    this.writeThrough = writeThrough;
	}
    }

    @Override
    public boolean isReadThrough() {
	return readThrough;
    }

    @Override
    public void setReadThrough(boolean readThrough) throws SourceException {
	if (readThrough != this.readThrough) {
	    this.readThrough = readThrough;
	}
    }

    @Override
    public boolean isModified() {
	return modified;
    }

    @Override
    public void addValidator(Validator validator) {
	if (validators == null) {
	    validators = new LinkedList<Validator>();
	}
	validators.add(validator);
    }

    @Override
    public void removeValidator(Validator validator) {
	if (validators != null) {
	    validators.remove(validator);
	}
    }

    @Override
    public Collection<Validator> getValidators() {
	if (validators == null || validators.isEmpty()) {
	    return null;
	}
	return Collections.unmodifiableCollection(validators);
    }

    @Override
    public boolean isValid() {
	if (validators != null) {
	    for (Validator validator : validators) {
		if (!validator.isValid(this)) {
		    return false;
		}
	    }
	}
	return true;
    }

    @Override
    public void validate() throws InvalidValueException {
	LinkedList<InvalidValueException> errors = null;
	if (validators != null) {
	    for (Validator validator : validators) {
		try {
		    validator.validate(this);
		} catch (InvalidValueException e) {
		    if (errors == null) {
			errors = new LinkedList<InvalidValueException>();
		    }
		    errors.add(e);
		}
	    }
	}
	if (errors != null) {
	    throw new InvalidValueException(null, errors.toArray(new InvalidValueException[0]));
	}
    }

    @Override
    public boolean isInvalidAllowed() {
	return invalidAllowed;
    }

    @Override
    public void setInvalidAllowed(boolean invalidAllowed) throws UnsupportedOperationException {
	this.invalidAllowed = invalidAllowed;
    }

    @Override
    public boolean isInvalidCommitted() {
	return invalidCommited;
    }

    @Override
    public void setInvalidCommitted(boolean isCommitted) {
	this.invalidCommited = isCommitted;
    }
}
