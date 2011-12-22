/*
 * Copyright 2011 Instituto Superior Tecnico
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the vaadin-framework.
 *
 *   The vaadin-framework Infrastructure is free software: you can
 *   redistribute it and/or modify it under the terms of the GNU Lesser General
 *   Public License as published by the Free Software Foundation, either version
 *   3 of the License, or (at your option) any later version.*
 *
 *   vaadin-framework is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with vaadin-framework. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package pt.ist.vaadinframework.data;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EventObject;
import java.util.HashMap;
import java.util.LinkedList;

import jvstm.CommitException;
import jvstm.cps.ConsistencyException;

import org.apache.commons.lang.StringUtils;

import pt.ist.fenixWebFramework.services.ServiceManager;
import pt.ist.fenixWebFramework.services.ServicePredicate;
import pt.ist.fenixframework.pstm.AbstractDomainObject.UnableToDetermineIdException;
import pt.ist.fenixframework.pstm.IllegalWriteException;
import pt.ist.vaadinframework.data.util.ServiceUtils;

import com.vaadin.data.Buffered;
import com.vaadin.data.BufferedValidatable;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Validatable;
import com.vaadin.data.Validator.InvalidValueException;

public abstract class AbstractBufferedItem<Id, Type> extends BufferedProperty<Type> implements Item,
	Item.PropertySetChangeNotifier {
    private final LinkedList<Id> list = new LinkedList<Id>();

    private final HashMap<Id, Property> map = new HashMap<Id, Property>();

    private ItemConstructor<Id> constructor;

    private ItemWriter<Id> writer;

    private LinkedList<Item.PropertySetChangeListener> propertySetChangeListeners = null;

    private boolean propertySetChangePropagationEnabled = true;

    private Item.PropertySetChangeEvent lastEvent;

    public AbstractBufferedItem(Property wrapped, Hint... hints) {
	super(wrapped, hints);
    }

    public AbstractBufferedItem(Class<? extends Type> type, Hint... hints) {
	super(type, hints);
    }

    public AbstractBufferedItem(Type value, Hint... hints) {
	super(value, hints);
    }

    public AbstractBufferedItem(Type value, Class<? extends Type> type, Hint... hints) {
	super(value, type, hints);
    }

    @Override
    protected void processNewCacheValue() {
	for (Id propertyId : getItemPropertyIds()) {
	    if (getItemProperty(propertyId) instanceof Buffered) {
		((Buffered) getItemProperty(propertyId)).discard();
	    }
	}
    }

    @Override
    public boolean addItemProperty(Object propertyId, Property property) {
	// Null ids are not accepted
	if (propertyId == null) {
	    throw new NullPointerException("Item property id can not be null");
	}

	// Cant add a property twice
	if (map.containsKey(propertyId)) {
	    return false;
	}

	// Put the property to map
	map.put((Id) propertyId, property);
	list.add((Id) propertyId);

	// Send event
	fireItemPropertySetChange();
	return true;
    }

    @Override
    public boolean removeItemProperty(Object propertyId) {
	// Cant remove missing properties
	if (map.remove(propertyId) == null) {
	    return false;
	}
	list.remove(propertyId);
	// propertyValues.remove(propertyId);

	// Send change events
	fireItemPropertySetChange();

	return true;
    }

    @Override
    public Collection<Id> getItemPropertyIds() {
	return list != null ? Collections.unmodifiableCollection(list) : (Collection<Id>) Collections.emptyList();
    }

    public void setConstructor(ItemConstructor<Id> constructor) {
	this.constructor = constructor;
    }

    public void setWriter(ItemWriter<Id> writer) {
	this.writer = writer;
    }

    @Override
    public Property getItemProperty(Object propertyId) {
	Property property = map.get(propertyId);
	if (property == null) {
	    property = makeProperty((Id) propertyId);
	}
	return property;
    }

    /**
     * Lazy creation of properties, this method is invoked for every propertyId
     * that is requested of the Item. The created properties are not
     * automatically registered in the item, you have to invoke
     * {@link #addItemProperty(Object, Property)} yourself. You also need to
     * ensure that the returned properties are of {@link BufferedProperty}s or
     * {@link Item}s or {@link Collection}s over {@link BufferedProperty}s.
     * 
     * @param propertyId The key of the property.
     * @return A {@link Property} instance.
     */
    protected abstract Property makeProperty(Id propertyId);

    @Override
    public void commit() throws SourceException, InvalidValueException {
	ServiceManager.execute(new ServicePredicate() {
	    @Override
	    public void execute() {
		try {
		    if (!isInvalidCommitted() && !isValid()) {
			validate();
		    }
		    if (cache == null) {
			construct(true);
			fireValueChange();
		    } else {
			applyWriter();
		    }
		    for (Id propertyId : getItemPropertyIds()) {
			if (getItemProperty(propertyId) instanceof Buffered) {
			    ((Buffered) getItemProperty(propertyId)).commit();
			}
		    }
		    if (isModified()) {
			wrapped.setValue(cache);
		    }
		    modified = false;
		} catch (Throwable e) {
		    ServiceUtils.handleException(e);
		}
	    }
	});
    }

    private void construct(boolean taint) {
	if (constructor != null) {
	    try {
		Method method = findMethod(constructor.getClass(), getArgumentTypes(constructor.getOrderedArguments()));
		Object[] argumentValues = readArguments(constructor.getOrderedArguments());
		// VaadinFrameworkLogger.getLogger().debug(
		// "persisting item with constructor with properties: ["
		// + StringUtils.join(constructor.getOrderedArguments(), ", ") +
		// "] with values: ["
		// + StringUtils.join(argumentValues, ", ") + "]");
		cache = convertValue(method.invoke(constructor, argumentValues));
		for (Id id : constructor.getOrderedArguments()) {
		    if (getItemProperty(id) instanceof Buffered) {
			((Buffered) getItemProperty(id)).discard();
		    }
		}
	    } catch (Throwable e) {
		handleException(e);
		throw new SourceException(this, e);
	    }
	} else {
	    try {
		cache = convertValue(getType().newInstance());
	    } catch (Throwable e) {
		handleException(e);
		throw new SourceException(this, e);
	    }
	}
	modified = taint;
    }

    private void applyWriter() {
	if (writer != null) {
	    try {
		if (fieldDiffer(writer.getOrderedArguments())) {
		    LinkedList<Class<?>> argumentTypes = new LinkedList<Class<?>>();
		    argumentTypes.add(getType());
		    argumentTypes.addAll(Arrays.asList(getArgumentTypes(writer.getOrderedArguments())));
		    Method method = findMethod(writer.getClass(), argumentTypes.toArray(new Class<?>[0]));
		    LinkedList<Object> argumentValues = new LinkedList<Object>();
		    argumentValues.add(cache);
		    argumentValues.addAll(Arrays.asList(readArguments(writer.getOrderedArguments())));
		    // VaadinFrameworkLogger.getLogger().debug(
		    // "persisting item with writer with properties: ["
		    // + StringUtils.join(writer.getOrderedArguments(), ", ") +
		    // "] with values: ["
		    // + StringUtils.join(argumentValues.subList(1,
		    // argumentValues.size()), ", ") + "]");
		    method.invoke(writer, argumentValues.toArray(new Object[0]));
		    for (Id id : writer.getOrderedArguments()) {
			if (getItemProperty(id) instanceof Buffered) {
			    ((Buffered) getItemProperty(id)).discard();
			}
		    }
		}
	    } catch (Throwable e) {
		handleException(e);
		throw new SourceException(this, e);
	    }
	}
    }

    private ArrayList<Throwable> getAllCauses(Throwable t) {
	final ArrayList<Throwable> causes = new ArrayList<Throwable>();
	causes.add(t);
	if (t instanceof Buffered.SourceException) {
	    for (Throwable sec : ((Buffered.SourceException) t).getCauses()) {
		causes.addAll(getAllCauses(sec));
	    }
	} else {
	    if (t.getCause() != null) {
		causes.addAll(getAllCauses(t.getCause()));
	    }
	}
	return causes;
    }

    // private Buffered.SourceException
    // handleDomainException(Buffered.SourceException se) {
    // final ArrayList<Throwable> causes = new ArrayList<Throwable>();
    // for (Throwable throwable : getAllCauses(se)) {
    // if (throwable instanceof FFDomainException) {
    // return new Buffered.SourceException(se.getSource(),
    // new Throwable[] { new DomainExceptionErrorMessage(throwable) });
    // }
    // causes.add(throwable);
    // }
    // return new Buffered.SourceException(se.getSource(), causes.toArray(new
    // Throwable[0]));
    // }

    private boolean fieldDiffer(Id[] arguments) {
	for (Id propertyId : arguments) {
	    if (getItemProperty(propertyId) instanceof Buffered) {
		if (((Buffered) getItemProperty(propertyId)).isModified()) {
		    return true;
		}
	    } else {
		return true;
	    }
	}
	return false;
    }

    private Method findMethod(Class<?> type, Class<?>[] types) throws NoSuchMethodException {
	for (Method method : type.getMethods()) {
	    Class<?>[] mTypes = method.getParameterTypes();
	    boolean match = true;
	    for (int i = 0; i < types.length; i++) {
		if (i >= mTypes.length) {
		    match = false;
		    break;
		}
		if (!mTypes[i].isAssignableFrom(Object.class) && !mTypes[i].isAssignableFrom(types[i])) {
		    match = false;
		    break;
		}
	    }
	    if (!getType().isAssignableFrom(method.getReturnType())) {
		match = false;
	    }
	    if (match) {
		return method;
	    }
	}
	final String message = "Must specify a method in class %s with a signature compatible with the arguments in getOrderedArguments() [%s]";
	throw new NoSuchMethodException(String.format(message, type.getName(), StringUtils.join(types, ",")));
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

    private Class<?>[] getArgumentTypes(Id[] argumentIds) {
	Class<?>[] types = new Class<?>[argumentIds.length];
	for (int i = 0; i < argumentIds.length; i++) {
	    types[i] = getItemProperty(argumentIds[i]).getType();
	}
	return types;
    }

    private Object[] readArguments(Id[] argumentIds) {
	Object[] arguments = new Object[argumentIds.length];
	for (int i = 0; i < argumentIds.length; i++) {
	    if (getItemProperty(argumentIds[i]) instanceof AbstractBufferedItem) {
		((AbstractBufferedItem<?, ?>) getItemProperty(argumentIds[i])).construct(false);
	    }
	    arguments[i] = getItemProperty(argumentIds[i]).getValue();
	}
	return arguments;
    }

    @Override
    public void discard() throws SourceException {
	super.discard();
	for (Id propertyId : getItemPropertyIds()) {
	    if (getItemProperty(propertyId) instanceof Buffered) {
		((Buffered) getItemProperty(propertyId)).discard();
	    }
	}
    }

    @Override
    public void setWriteThrough(boolean writeThrough) throws SourceException, InvalidValueException {
	for (Id propertyId : getItemPropertyIds()) {
	    if (getItemProperty(propertyId) instanceof Buffered) {
		((Buffered) getItemProperty(propertyId)).setWriteThrough(writeThrough);
	    }
	}
	super.setWriteThrough(writeThrough);
    }

    @Override
    public void setReadThrough(boolean readThrough) throws SourceException {
	for (Id propertyId : getItemPropertyIds()) {
	    if (getItemProperty(propertyId) instanceof Buffered) {
		((Buffered) getItemProperty(propertyId)).setReadThrough(readThrough);
	    }
	}
	super.setReadThrough(readThrough);
    }

    @Override
    public void setInvalidAllowed(boolean invalidAllowed) throws UnsupportedOperationException {
	for (Id propertyId : getItemPropertyIds()) {
	    if (getItemProperty(propertyId) instanceof Validatable) {
		((Validatable) getItemProperty(propertyId)).setInvalidAllowed(invalidAllowed);
	    }
	}
	super.setInvalidAllowed(invalidAllowed);
    }

    @Override
    public void setInvalidCommitted(boolean invalidCommitted) {
	for (Id propertyId : getItemPropertyIds()) {
	    if (getItemProperty(propertyId) instanceof BufferedValidatable) {
		((BufferedValidatable) getItemProperty(propertyId)).setInvalidCommitted(invalidCommitted);
	    }
	}
	super.setInvalidCommitted(invalidCommitted);
    }

    @Override
    public boolean isValid() {
	for (Id propertyId : getItemPropertyIds()) {
	    if (getItemProperty(propertyId) instanceof Validatable) {
		if (!((Validatable) getItemProperty(propertyId)).isValid()) {
		    return false;
		}
	    }
	}
	return super.isValid();
    }

    @Override
    public void validate() throws InvalidValueException {
	for (Id propertyId : getItemPropertyIds()) {
	    if (getItemProperty(propertyId) instanceof Validatable) {
		((Validatable) getItemProperty(propertyId)).validate();
	    }
	}
	super.validate();
    }

    /* Notifiers */

    private class PropertySetChangeEvent extends EventObject implements Item.PropertySetChangeEvent {
	private PropertySetChangeEvent(Item source) {
	    super(source);
	}

	/**
	 * Gets the Item whose Property set has changed.
	 * 
	 * @return source object of the event as an <code>Item</code>
	 */
	@Override
	public Item getItem() {
	    return (Item) getSource();
	}
    }

    /**
     * Registers a new property set change listener for this Item.
     * 
     * @param listener the new Listener to be registered.
     */
    @Override
    public void addListener(Item.PropertySetChangeListener listener) {
	if (propertySetChangeListeners == null) {
	    propertySetChangeListeners = new LinkedList<PropertySetChangeListener>();
	}
	propertySetChangeListeners.add(listener);
    }

    /**
     * Removes a previously registered property set change listener.
     * 
     * @param listener the Listener to be removed.
     */
    @Override
    public void removeListener(Item.PropertySetChangeListener listener) {
	if (propertySetChangeListeners != null) {
	    propertySetChangeListeners.remove(listener);
	}
    }

    /**
     * Sends a Property set change event to all interested listeners.
     */
    protected void fireItemPropertySetChange() {
	if (propertySetChangeListeners != null) {
	    final Item.PropertySetChangeEvent event = new AbstractBufferedItem.PropertySetChangeEvent(this);
	    if (propertySetChangePropagationEnabled) {
		final Object[] l = propertySetChangeListeners.toArray();
		for (Object element : l) {
		    ((Item.PropertySetChangeListener) element).itemPropertySetChange(event);
		}
	    } else {
		lastEvent = event;
	    }
	}
    }

    public void setPropertySetChangePropagationEnabled(boolean propertySetChangePropagationEnabled) {
	if (this.propertySetChangePropagationEnabled != propertySetChangePropagationEnabled) {
	    this.propertySetChangePropagationEnabled = propertySetChangePropagationEnabled;
	    if (propertySetChangePropagationEnabled && lastEvent != null) {
		if (propertySetChangeListeners != null) {
		    final Object[] l = propertySetChangeListeners.toArray();
		    for (Object element : l) {
			((Item.PropertySetChangeListener) element).itemPropertySetChange(lastEvent);
		    }
		}
		lastEvent = null;
	    }
	}
    }

    @Override
    public Collection<?> getListeners(Class<?> eventType) {
	if (Item.PropertySetChangeEvent.class.isAssignableFrom(eventType)) {
	    if (propertySetChangeListeners == null) {
		return Collections.EMPTY_LIST;
	    }
	    return Collections.unmodifiableCollection(propertySetChangeListeners);
	}

	return Collections.EMPTY_LIST;
    }
}
