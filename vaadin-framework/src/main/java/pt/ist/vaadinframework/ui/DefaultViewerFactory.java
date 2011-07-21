package pt.ist.vaadinframework.ui;

import java.util.ResourceBundle;

import pt.ist.bennu.ui.viewers.ViewerFactory;
import pt.ist.vaadinframework.VaadinFrameworkLogger;

import com.vaadin.data.Item;
import com.vaadin.data.Property.Viewer;
import com.vaadin.data.util.AbstractDomainItem;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Label;

public class DefaultViewerFactory implements ViewerFactory {
    private final ResourceBundle bundle;

    public DefaultViewerFactory(ResourceBundle bundle) {
	this.bundle = bundle;
    }

    @Override
    public final Viewer createViewer(Item item, Object propertyId, Component uiContext) {
	Viewer viewer = makeViewer(item, propertyId, uiContext);
	AbstractComponent component = (AbstractComponent) viewer;
	component.setDescription(makeDescription(item, propertyId, uiContext));
	component.setCaption(makeCaption(item, propertyId, uiContext));
	return viewer;
    }
    
    @Override
    public final String makeCaption(Item item, Object propertyId, Component uiContext) {
	if (item instanceof AbstractDomainItem) {
	    AbstractDomainItem domainItem = (AbstractDomainItem) item;
	    String key = domainItem.getLabelKey(bundle, propertyId);
	    if (key != null) {
		return key;
	    }
	    key = domainItem.getType().getName() + "." + propertyId;
	    VaadinFrameworkLogger.getLogger().warn("i18n opportunity missed: " + key);
	}
	return DefaultFieldFactory.createCaptionByPropertyId(propertyId);
    }

    protected String makeDescription(Item item, Object propertyId, Component uiContext) {
	if (item instanceof AbstractDomainItem) {
	    AbstractDomainItem domainItem = (AbstractDomainItem) item;
	    String key = domainItem.getDescriptionKey(bundle,propertyId);
	    if (key != null) {
		return key;
	    }
	    key = domainItem.getType().getName() + "." + propertyId + ".description";
	    VaadinFrameworkLogger.getLogger().warn("i18n opportunity missed: " + key);
	}
	return makeCaption(item, propertyId, uiContext); 
    }

    protected Viewer makeViewer(Item item, Object propertyId, Component uiContext) {
	return new Label();
    }
}