package pt.ist.vaadinframework.ui;

import java.util.ResourceBundle;

import pt.ist.vaadinframework.VaadinFrameworkLogger;

import com.vaadin.data.Item;
import com.vaadin.data.Property.Viewer;
import com.vaadin.data.util.AbstractDomainItem;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;

public abstract class AbstractViewerFactory implements ViewerFactory {
    private final ResourceBundle bundle;

    public AbstractViewerFactory(ResourceBundle bundle) {
	this.bundle = bundle;
    }

    @Override
    public Viewer createViewer(Item item, Object propertyId, Component uiContext) {
	Viewer viewer = makeViewer(item, propertyId, uiContext);
	AbstractComponent component = (AbstractComponent) viewer;
	component.setCaption(makeCaption(item, propertyId, uiContext));
	component.setDescription(makeDescription(item, propertyId, uiContext));
	return viewer;
    }

    protected String makeCaption(Item item, Object propertyId, Component uiContext) {
	if (item instanceof AbstractDomainItem) {
	    String key = ((AbstractDomainItem) item).getLabelKey(propertyId);
	    if (bundle.containsKey(key)) {
		return bundle.getString(key);
	    }
	    VaadinFrameworkLogger.getLogger().warn("i18n opportunity missed: " + key);
	}
	return DefaultFieldFactory.createCaptionByPropertyId(propertyId);
    }

    protected String makeDescription(Item item, Object propertyId, Component uiContext) {
	if (item instanceof AbstractDomainItem) {
	    String key = ((AbstractDomainItem) item).getDescriptionKey(propertyId);
	    if (bundle.containsKey(key)) {
		return bundle.getString(key);
	    }
	    VaadinFrameworkLogger.getLogger().warn("i18n opportunity missed: " + key);
	}
	return makeCaption(item, propertyId, uiContext);
    }

    protected abstract Viewer makeViewer(Item item, Object propertyId, Component uiContext);
}