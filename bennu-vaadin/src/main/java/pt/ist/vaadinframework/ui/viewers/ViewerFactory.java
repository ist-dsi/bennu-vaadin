package pt.ist.vaadinframework.ui.viewers;

import java.io.Serializable;

import com.vaadin.data.Item;
import com.vaadin.data.Property.Viewer;
import com.vaadin.ui.Component;

public interface ViewerFactory extends Serializable {
	Viewer createViewer(Item item, Object propertyId, Component uiContext);

	String makeCaption(Item item, Object propertyId, Component uiContext);
}