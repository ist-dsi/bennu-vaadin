package module.vaadin.ui.commons;

import java.util.ArrayList;
import java.util.ResourceBundle;

import module.vaadin.data.util.BufferedContainer;
import module.vaadin.resources.VaadinResourceConstants;
import module.vaadin.resources.VaadinResources;

import org.apache.commons.lang.StringUtils;

import com.vaadin.data.Container;
import com.vaadin.data.Container.Indexed;
import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.data.Property;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.FieldWrapper;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.TableFieldFactory;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;

public class ContainerEditor<PC> extends FieldWrapper<PC> {
    public static class OrderChanger extends CustomComponent {

	private final Button up = new Button();

	private final Button down = new Button();

	public OrderChanger(final Indexed container, final Object itemId) {
	    HorizontalLayout layout = new HorizontalLayout();
	    layout.addComponent(up);
	    up.setIcon(new ThemeResource("../runo/icons/32/arrow-up.png"));
	    up.addStyleName(BaseTheme.BUTTON_LINK);
	    up.addListener(new ClickListener() {
		@Override
		public void buttonClick(ClickEvent event) {
		    int newIndex = container.indexOfId(itemId) - 1;
		    container.removeItem(itemId);
		    container.addItemAt(newIndex, itemId);
		    up.setEnabled(newIndex > 0);
		}
	    });
	    up.setEnabled(container.indexOfId(itemId) > 0);

	    layout.addComponent(down);
	    down.setIcon(new ThemeResource("../runo/icons/32/arrow-down.png"));
	    down.addStyleName(BaseTheme.BUTTON_LINK);
	    down.addListener(new ClickListener() {
		@Override
		public void buttonClick(ClickEvent event) {
		    int newIndex = container.indexOfId(itemId) + 1;
		    container.removeItem(itemId);
		    container.addItemAt(newIndex, itemId);
		    down.setEnabled(newIndex < container.size() - 1);
		}
	    });
	    down.setEnabled(container.indexOfId(itemId) < container.size() - 1);

	    setCompositionRoot(layout);
	}
    }

    public static class ContainerEditorTable extends TransactionalTable {
	public ContainerEditorTable(TableFieldFactory factory, ResourceBundle bundle) {
	    super(bundle);
	    setWidth(100, UNITS_PERCENTAGE);
	    setPageLength(0);
	    setTableFieldFactory(factory);
	    setEditable(true);
	    addGeneratedColumn("index", new ColumnGenerator() {
		@Override
		public Component generateCell(Table source, Object itemId, Object columnId) {
		    return new OrderChanger((Indexed) getContainerDataSource(), itemId);
		}
	    });
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
		ArrayList<Object> columns = new ArrayList<Object>();
		if (newDataSource instanceof Indexed) {
		    columns.add("index");
		    setColumnWidth("index", 77);
		}
		columns.addAll(((Container) newDataSource).getContainerPropertyIds());
		columns.add(StringUtils.EMPTY);
		setVisibleColumns(columns.toArray(new Object[0]));
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
