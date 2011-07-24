package module.vaadin.ui.commons;

import java.util.Collection;
import java.util.ResourceBundle;

import module.vaadin.data.util.BufferedContainer;
import pt.ist.vaadinframework.VaadinFrameworkLogger;
import pt.ist.vaadinframework.ui.DefaultFieldFactory;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
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
	    return new ContainerEditor(this, bundle);
	}
	return super.makeField(item, propertyId, uiContext);
    }

    @Override
    protected String makeCaption(Item item, Object propertyId, Component uiContext) {
	if (item instanceof Property) {
	    String key = ((Property) item).getType().getName() + "." + propertyId;
	    if (bundle.containsKey(key)) {
		return bundle.getString(key);
	    }
	    VaadinFrameworkLogger.getLogger().warn("i18n opportunity missed: " + key);
	}
	return super.makeCaption(item, propertyId, uiContext);
    }

    @Override
    protected String makeDescription(Item item, Object propertyId, Component uiContext) {
	if (item instanceof Property) {
	    String key = ((Property) item).getType().getName() + "." + propertyId + ".description";
	    if (bundle.containsKey(key)) {
		return bundle.getString(key);
	    }
	    VaadinFrameworkLogger.getLogger().warn("i18n opportunity missed: " + key);
	}
	return super.makeDescription(item, propertyId, uiContext);
    }
}
