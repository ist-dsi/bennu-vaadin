package pt.ist.vaadinframework.ui.fields;

import java.io.Serializable;
import java.util.Arrays;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import pt.ist.fenixWebFramework.services.Service;
import pt.ist.vaadinframework.ui.TransactionalForm;
import pt.ist.vaadinframework.ui.layout.ControlsLayout;

import com.vaadin.data.Container;
import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.data.Container.ItemSetChangeListener;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.DomainContainer;
import com.vaadin.data.util.DomainItem;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.TableFieldFactory;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.BaseTheme;

public class TableContainerEditor extends CustomField {
    public static class CreateRequestEvent extends EventObject {
	public CreateRequestEvent(DomainContainer container) {
	    super(container);
	}

	public DomainContainer getContainer() {
	    return (DomainContainer) super.getSource();
	}
    }

    public interface CreateRequestListener extends Serializable {
	public void creationRequest(CreateRequestEvent event);
    }

    public static class GeneratedColumnActionEvent implements Serializable {
	private final Table source;
	private final Object itemId;
	private final Object columnId;

	public GeneratedColumnActionEvent(Table source, Object itemId, Object columnId) {
	    this.source = source;
	    this.itemId = itemId;
	    this.columnId = columnId;
	}

	public Table getSource() {
	    return source;
	}

	public Object getItemId() {
	    return itemId;
	}

	public Object getColumnId() {
	    return columnId;
	}
    }

    public interface GeneratedColumnActionListener extends Serializable {
	public void generatedColumnClick(GeneratedColumnActionEvent event);
    }

    public class ElementEditOrCreateWindow extends Window {
	public ElementEditOrCreateWindow(String caption, Item item) {
	    super(caption);
	    getContent().setSizeUndefined();
	    TransactionalForm form = new TransactionalForm();
	    form.setWriteThrough(false);
	    form.setFormFieldFactory((FormFieldFactory) table.getTableFieldFactory());
	    form.setItemDataSource(item, Arrays.asList(properties));
	    form.addSubmitButton();
	    form.addClearButton();
	    form.addCancelButton();
	    addComponent(form);
	    center();
	}
    }

    private final Table table;

    private final ControlsLayout buttons;

    private Map<String, GeneratedColumnActionListener> generatedColumnListeners;

    private final String[] properties;

    public TableContainerEditor(String... properties) {
	this.properties = properties;
	VerticalLayout layout = new VerticalLayout();
	table = new Table();
	layout.addComponent(table);
	table.setPageLength(0);
	table.setSizeFull();
	table.setVisible(table.getContainerDataSource().size() != 0);
	table.addListener(new ItemSetChangeListener() {
	    @Override
	    public void containerItemSetChange(ItemSetChangeEvent event) {
		table.setValue(event.getContainer().size() != 0);
	    }
	});
	buttons = new ControlsLayout();
	layout.addComponent(buttons);
	setCompositionRoot(layout);
    }

    @Override
    public void attach() {
	super.attach();
	refreshGeneratedColumn();
    }

    public void addCreatorButton(String caption, final CreateRequestListener listener) {
	Button creator = new Button(caption);
	buttons.addComponent(creator);
	creator.addListener(new ClickListener() {
	    public void buttonClick(ClickEvent event) {
		listener.creationRequest(new CreateRequestEvent((DomainContainer) getPropertyDataSource()));
	    }
	});
    }

    public void addCreatorButton(final String caption) {
	addCreatorButton(caption, new CreateRequestListener() {
	    public void creationRequest(CreateRequestEvent event) {
		DomainItem item = (DomainItem) event.getContainer().addItem();
		getApplication().getMainWindow().addWindow(new ElementEditOrCreateWindow(caption, item));
	    }
	});
    }

    public void addGeneratedButton(String caption, GeneratedColumnActionListener listener) {
	if (generatedColumnListeners == null) {
	    generatedColumnListeners = new HashMap<String, GeneratedColumnActionListener>();
	}
	generatedColumnListeners.put(caption, listener);
	if (table.removeGeneratedColumn(StringUtils.EMPTY)) {
	    refreshGeneratedColumn();
	}
    }

    public void addEditButton(final String caption) {
	addGeneratedButton(caption, new GeneratedColumnActionListener() {
	    @Override
	    public void generatedColumnClick(GeneratedColumnActionEvent event) {
		Item item = event.getSource().getContainerDataSource().getItem(event.getItemId());
		getApplication().getMainWindow().addWindow(new ElementEditOrCreateWindow(caption, item));
	    }
	});
    }

    public void addDeleteButton(final String caption) {
	addGeneratedButton(caption, new GeneratedColumnActionListener() {
	    @Override
	    public void generatedColumnClick(GeneratedColumnActionEvent event) {
		deleteElement(event.getSource().getContainerDataSource(), event.getItemId());
	    }
	});
    }

    @Service
    private static void deleteElement(Container container, Object itemId) {
	// FIXME: side effects on remove wont work on service restart
	container.removeItem(itemId);
    }

    private void refreshGeneratedColumn() {
	if (generatedColumnListeners != null && !generatedColumnListeners.isEmpty()) {
	    table.addGeneratedColumn(StringUtils.EMPTY, new ColumnGenerator() {
		public Component generateCell(Table source, Object itemId, Object columnId) {
		    HorizontalLayout actions = new HorizontalLayout();
		    actions.setSpacing(true);
		    final GeneratedColumnActionEvent columnEvent = new GeneratedColumnActionEvent(source, itemId, columnId);
		    for (final Entry<String, GeneratedColumnActionListener> column : generatedColumnListeners.entrySet()) {
			Button action = new Button(column.getKey());
			actions.addComponent(action);
			action.addStyleName(BaseTheme.BUTTON_LINK);
			action.addListener(new ClickListener() {
			    public void buttonClick(ClickEvent event) {
				column.getValue().generatedColumnClick(columnEvent);
			    }
			});
		    }
		    return actions;
		}
	    });
	}
    }

    public void setEditable(boolean editable) {
	table.setEditable(editable);
    }

    public void setTableFieldFactory(TableFieldFactory fieldFactory) {
	table.setTableFieldFactory(fieldFactory);
    }

    @Override
    public void setPropertyDataSource(Property newDataSource) {
	if (newDataSource instanceof DomainContainer) {
	    DomainContainer container = (DomainContainer) newDataSource;
	    container.setContainerProperties(properties);
	    super.setPropertyDataSource(newDataSource);
	    table.setContainerDataSource(container);
	    // i'd prefer that a table listener would handle this automatically
	    table.setVisible(table.getContainerDataSource().size() != 0);
	} else {
	    throw new UnsupportedOperationException();
	}
    }

    @Override
    public Class<?> getType() {
	return Set.class;
    }
}
