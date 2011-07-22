package module.vaadin.ui.commons;

import java.util.Collection;
import java.util.ResourceBundle;

import module.vaadin.data.util.BufferedContainer;
import pt.ist.vaadinframework.ui.DefaultFieldFactory;

import com.vaadin.data.Item;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;

public class BennuFieldFactory extends DefaultFieldFactory {

    public BennuFieldFactory(ResourceBundle bundle) {
	super(bundle);
    }

    @Override
    protected Field makeField(Item item, Object propertyId, Component uiContext) {
	Class<?> type = item.getItemProperty(propertyId).getType();
	if (Collection.class.isAssignableFrom(type) && item.getItemProperty(propertyId) instanceof BufferedContainer) {
	    return new ContainerEditor(this);
	}
	return super.makeField(item, propertyId, uiContext);
    }
}
