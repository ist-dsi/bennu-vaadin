package module.vaadin.data.util.reflect;

import java.util.HashMap;
import java.util.Map;

import module.vaadin.data.util.BufferedItem;
import module.vaadin.data.util.HintedProperty;
import module.vaadin.data.util.VBoxProperty;
import module.vaadin.data.util.hints.Required;
import pt.ist.fenixframework.pstm.AbstractDomainObject;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.metamodel.MetaModel;
import com.vaadin.data.util.metamodel.PropertyDescriptor;

public class DomainItem<Type extends AbstractDomainObject> extends BufferedItem<String, Type> {
    private final Map<String, PropertyDescriptor> descriptorCache = new HashMap<String, PropertyDescriptor>();

    public DomainItem(HintedProperty value) {
	super(value);
    }

    public DomainItem(Type instance) {
	this(new VBoxProperty(instance));
    }

    public DomainItem(Class<? extends Type> type) {
	this(new VBoxProperty(type));
    }

    @Override
    protected Property makeProperty(String propertyId) {
	int split = propertyId.indexOf('.');
	Property property;
	if (split == -1) {
	    property = fromDescriptor(propertyId);
	    if (property != null) {
		addItemProperty(propertyId, property);
	    }
	} else {
	    String first = propertyId.substring(0, split);
	    String rest = propertyId.substring(split + 1);
	    property = getItemProperty(first);
	    if (property != null && property instanceof Item) {
		addItemProperty(first, property);
		property = ((Item) property).getItemProperty(rest);
	    } else {
		throw new RuntimeException("could not load property: " + propertyId + " for type: " + getType());
	    }
	}
	return property;
    }

    private Property fromDescriptor(String propertyId) {
	PropertyDescriptor descriptor = getDescriptor(propertyId);
	if (descriptor != null) {
	    BufferedProperty property;
	    if (descriptor.isRequired()) {
		property = new BufferedProperty(propertyId, descriptor.getPropertyType(), new Required());
	    } else {
		property = new BufferedProperty(propertyId, descriptor.getPropertyType());
	    }
	    if (AbstractDomainObject.class.isAssignableFrom(descriptor.getPropertyType())) {
		return new DomainItem(property);
	    } else if (descriptor.isCollection()) {
		return new DomainContainer(property, descriptor.getCollectionElementType());
	    }
	    return property;
	}
	throw new RuntimeException("could not load property: " + propertyId + " for type: " + getType());
    }

    @Override
    protected Object readPropertyValue(AbstractDomainObject host, String propertyId) {
	PropertyDescriptor descriptor = getDescriptor(propertyId);
	if (descriptor != null) {
	    return descriptor.read(host);
	}
	return null;
    }

    @Override
    protected void writePropertyValue(AbstractDomainObject host, String propertyId, Object newValue) {
	PropertyDescriptor descriptor = getDescriptor(propertyId);
	if (descriptor != null) {
	    descriptor.write(host, newValue);
	}
    }

    private PropertyDescriptor getDescriptor(String propertyId) {
	if (!descriptorCache.containsKey(propertyId)) {
	    MetaModel model = MetaModel.findMetaModelForType(getType());
	    descriptorCache.put(propertyId, model.getPropertyDescriptor(propertyId));
	}
	return descriptorCache.get(propertyId);
    }
}
