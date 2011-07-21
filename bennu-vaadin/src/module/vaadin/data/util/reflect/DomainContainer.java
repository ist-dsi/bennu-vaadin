package module.vaadin.data.util.reflect;

import java.util.Collection;

import module.vaadin.data.util.BufferedContainer;
import module.vaadin.data.util.VBoxProperty;
import pt.ist.fenixframework.pstm.AbstractDomainObject;

import com.vaadin.data.Property;

public class DomainContainer<Type extends AbstractDomainObject> extends BufferedContainer<Type, String, DomainItem<Type>> {
    public DomainContainer(Property value, Class<? extends Type> elementType) {
	super(value, elementType);
    }

    public DomainContainer(Class<? extends Type> elementType) {
	super(new VBoxProperty(null), elementType);
    }

    public DomainContainer(Collection<? extends Type> elements, Class<? extends Type> elementType) {
	super(new VBoxProperty(elements), elementType);
    }

    @Override
    public DomainItem<Type> makeItem(Type itemId) {
	return new DomainItem<Type>(itemId);
    }

    @Override
    public DomainItem<Type> makeItem(Class<? extends Type> type) {
	return new DomainItem<Type>(type);
    }
}
