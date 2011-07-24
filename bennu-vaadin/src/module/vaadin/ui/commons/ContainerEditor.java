package module.vaadin.ui.commons;

import java.util.Collection;
import java.util.ResourceBundle;

import module.vaadin.data.util.BufferedContainer;
import module.vaadin.data.util.VBoxProperty;
import module.vaadin.resources.VaadinResourceConstants;
import module.vaadin.resources.VaadinResources;

import org.apache.commons.lang.StringUtils;

import com.vaadin.data.Container;
import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.data.Container.ItemSetChangeListener;
import com.vaadin.data.Property;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.TableFieldFactory;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;

public class ContainerEditor extends CustomField {
    private final Table table;

    public ContainerEditor(TableFieldFactory factory, ResourceBundle bundle) {
	VerticalLayout layout = new VerticalLayout();
	layout.setSpacing(true);
	table = new TransactionalTable(bundle);
	layout.addComponent(table);
	table.setWidth(100, UNITS_PERCENTAGE);
	table.setPageLength(0);
	table.setTableFieldFactory(factory);
	table.setEditable(true);
	table.addGeneratedColumn(StringUtils.EMPTY, new ColumnGenerator() {
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
	table.setVisible(table.size() > 0);
	table.addListener(new ItemSetChangeListener() {
	    @Override
	    public void containerItemSetChange(ItemSetChangeEvent event) {
		table.setVisible(event.getContainer().size() > 0);
	    }
	});
	Button add = new Button(VaadinResources.getString(VaadinResourceConstants.COMMONS_ADD_ACTION));
	add.addStyleName(BaseTheme.BUTTON_LINK);
	layout.addComponent(add);
	add.addListener(new ClickListener() {
	    @Override
	    public void buttonClick(ClickEvent event) {
		if (table.getContainerDataSource() instanceof BufferedContainer) {
		    BufferedContainer container = (BufferedContainer) table.getContainerDataSource();
		    container.addItem(new VBoxProperty(container.getElementType()));
		} else {
		    table.getContainerDataSource().addItem();
		}
	    }
	});
	setCompositionRoot(layout);
    }

    @Override
    public void setPropertyDataSource(Property newDataSource) {
	super.setPropertyDataSource(newDataSource);
	table.setContainerDataSource((Container) newDataSource);
    }

    @Override
    public void setWriteThrough(boolean writeTrough) throws SourceException, InvalidValueException {
	super.setWriteThrough(writeTrough);
	table.setWriteThrough(writeTrough);
    }

    @Override
    public void setImmediate(boolean immediate) {
	super.setImmediate(immediate);
	table.setImmediate(immediate);
    }

    @Override
    public void setReadThrough(boolean readTrough) throws SourceException {
	super.setReadThrough(readTrough);
	table.setReadThrough(true);
    }

    @Override
    public Class<?> getType() {
	return Collection.class;
    }
}
