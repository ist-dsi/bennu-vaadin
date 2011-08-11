package module.vaadin.data.util.reflect;

import java.util.Collection;

import module.vaadin.data.util.BufferedContainer;
import module.vaadin.data.util.HintedProperty;
import module.vaadin.data.util.VBoxProperty;
import pt.ist.fenixframework.pstm.AbstractDomainObject;

import com.vaadin.data.util.metamodel.MetaModel;
import com.vaadin.data.util.metamodel.PropertyDescriptor;

public class DomainContainer<Type extends AbstractDomainObject> extends
BufferedContainer<Type, String, DomainItem<Type>> {
    public DomainContainer(HintedProperty value, Class<? extends Type> elementType) {
	super(value, elementType);
    }

    public DomainContainer(Class<? extends Type> elementType) {
	super(new VBoxProperty(Collection.class), elementType);
    }

    public DomainContainer(Collection<? extends Type> elements, Class<? extends Type> elementType) {
	super(new VBoxProperty(elements), elementType);
    }

    @Override
    public DomainItem<Type> makeItem(HintedProperty itemId) {
	return new DomainItem<Type>(itemId);
    }

    public void setContainerProperties(String... propertyIds) {
	for (String propertyId : propertyIds) {
	    PropertyDescriptor propertyDescriptor = MetaModel.findMetaModelForType(getElementType()).getPropertyDescriptor(
		    propertyId);
	    addContainerProperty(propertyId, propertyDescriptor.getPropertyType(), propertyDescriptor.getDefaultValue());
	}
    }
}
