package pt.ist.vaadinframework.ui.viewers;

import java.util.Arrays;
import java.util.Collection;

import com.vaadin.data.Item;
import com.vaadin.data.Item.PropertySetChangeEvent;
import com.vaadin.data.Item.PropertySetChangeListener;
import com.vaadin.data.Item.PropertySetChangeNotifier;
import com.vaadin.data.Item.Viewer;
import com.vaadin.data.Property;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class TabularViewer extends VerticalLayout implements Viewer, PropertySetChangeListener {
    private Item itemDatasource;
    private ViewerFactory viewerFactory;
    Collection<?> propertyIds;

    private static final String CAPTION_CSS = "v-tabularviewer-caption";

    public TabularViewer() {
        addStyleName("properties");
        propertyIds = null;
    }

    public TabularViewer(ViewerFactory viewerFactory) {
        this();
        setViewerFactory(viewerFactory);
    }

    public TabularViewer(Item newDataSource, ViewerFactory viewerFactory) {
        this();
        setItemDataSource(newDataSource);
        setViewerFactory(viewerFactory);
    }

    public void setViewerFactory(ViewerFactory viewerFactory) {
        this.viewerFactory = viewerFactory;
    }

    public ViewerFactory getViewerFactory() {
        return viewerFactory;
    }

    @Override
    public void setItemDataSource(Item newDataSource) {
        setItemDataSource(newDataSource, newDataSource != null ? newDataSource.getItemPropertyIds() : null);
    }

    public void setItemDataSource(Item newDataSource, Collection<?> propertyIds) {

        if (itemDatasource != null) {
            if (itemDatasource instanceof PropertySetChangeNotifier) {
                ((PropertySetChangeNotifier) itemDatasource).removeListener(this);
            }
        }
        this.itemDatasource = newDataSource;
        if (itemDatasource instanceof PropertySetChangeNotifier) {
            ((PropertySetChangeNotifier) itemDatasource).addListener(this);
        }
        this.propertyIds = propertyIds;
        updateContent();
    }

    public void setVisibleItemProperties(Collection<?> visibleProperties) {
        setItemDataSource(itemDatasource, visibleProperties);
    }

    public void setVisibleItemProperties(Object[] visibleProperties) {
        setItemDataSource(itemDatasource, Arrays.asList(visibleProperties));
    }

    @Override
    public Item getItemDataSource() {
        return itemDatasource;
    }

    @Override
    public void itemPropertySetChange(PropertySetChangeEvent event) {
        updateContent();
    }

    private void updateContent() {
        removeAllComponents();
        HorizontalLayout line = null;
        for (Object propertyId : propertyIds) {
            line = new HorizontalLayout();
            line.addStyleName("properties");
            line.setWidth("100%");
            Property.Viewer value = viewerFactory.createViewer(itemDatasource, propertyId, this);
            value.setPropertyDataSource(itemDatasource.getItemProperty(propertyId));
            AbstractComponent valueComponent = (AbstractComponent) value;
            Label label = new Label(valueComponent.getCaption());
            label.addStyleName(CAPTION_CSS);
            valueComponent.setCaption(null);

            valueComponent.setSizeFull();
            label.setSizeFull();
            line.addComponent(label);
            line.addComponent(valueComponent);
            // line.setExpandRatio(label, 0.3f);
            // line.setExpandRatio(valueComponent, 0.7f);
            addComponent(line);
        }
        if (line != null) {
            line.addStyleName("properties-last");
        }
    }
}
