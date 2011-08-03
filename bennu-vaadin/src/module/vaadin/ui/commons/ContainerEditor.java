package module.vaadin.ui.commons;

import java.util.ResourceBundle;

import module.vaadin.data.util.BufferedContainer;
import module.vaadin.resources.VaadinResourceConstants;
import module.vaadin.resources.VaadinResources;

import org.apache.commons.lang.StringUtils;

import com.vaadin.data.Container;
import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.data.Property;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.FieldWrapper;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.TableFieldFactory;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;

public class ContainerEditor<PC> extends FieldWrapper<PC> {
    public static class ContainerEditorTable extends TransactionalTable {
	public ContainerEditorTable(TableFieldFactory factory, ResourceBundle bundle) {
	    super(bundle);
	    setWidth(100, UNITS_PERCENTAGE);
	    setPageLength(0);
	    setTableFieldFactory(factory);
	    setEditable(true);
	    addGeneratedColumn(StringUtils.EMPTY, new ColumnGenerator() {
		@Override
		public Component generateCell(final Table source, final Object itemId, Object columnId) {
		    Button delete = new Button(VaadinResources.getString(VaadinResourceConstants.COMMONS_DELETE_ACTION));
		    delete.addStyleName(BaseTheme.BUTTON_LINK);
		    delete.addListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
			    source.getContainerDataSource().removeItem(itemId);
			}
		    });
		    return delete;
		}
	    });
	    setVisible(size() > 0);
	    addListener(new ItemSetChangeListener() {
		@Override
		public void containerItemSetChange(ItemSetChangeEvent event) {
		    setVisible(event.getContainer().size() > 0);
		}
	    });
	}

	@Override
	protected boolean isEmpty() {
	    return getContainerDataSource().size() == 0;
	}

	@Override
	public void setPropertyDataSource(Property newDataSource) {
	    if (newDataSource instanceof Container) {
		super.setContainerDataSource((Container) newDataSource);
	    }
	}
    }

    public ContainerEditor(TableFieldFactory factory, ResourceBundle bundle, Class<? extends PC> type) {
	super(new ContainerEditorTable(factory, bundle), null, type);

	final VerticalLayout layout = new VerticalLayout();
	layout.setSpacing(true);
	layout.addComponent(getWrappedField());
	Button add = new Button(VaadinResources.getString(VaadinResourceConstants.COMMONS_ADD_ACTION));
	add.addStyleName(BaseTheme.BUTTON_LINK);
	layout.addComponent(add);
	add.addListener(new ClickListener() {
	    @Override
	    public void buttonClick(ClickEvent event) {
		if (getWrappedField().getContainerDataSource() instanceof BufferedContainer) {
		    BufferedContainer<PC, ?, ?> container = (BufferedContainer<PC, ?, ?>) getWrappedField()
			    .getContainerDataSource();
		    container.addItem(container.getElementType());
		} else {
		    getWrappedField().getContainerDataSource().addItem();
		}
	    }
	});
	setCompositionRoot(layout);
    }

    @Override
    protected boolean isEmpty() {
	return getWrappedField().getContainerDataSource().size() == 0;
    }

    @Override
    protected ContainerEditorTable getWrappedField() {
	return (ContainerEditorTable) super.getWrappedField();
    }

    public void setColumnHeaderMode(int columnHeaderMode) {
	getWrappedField().setColumnHeaderMode(columnHeaderMode);
    }

    public void setColumnExpandRatio(Object propertyId, float expandRatio) {
	getWrappedField().setColumnExpandRatio(propertyId, expandRatio);
    }

    public void setColumnWidth(Object propertyId, int width) {
	getWrappedField().setColumnWidth(propertyId, width);
    }

    public void addGeneratedColumn(Object id, ColumnGenerator generatedColumn) {
	getWrappedField().addGeneratedColumn(id, generatedColumn);
    }
}
