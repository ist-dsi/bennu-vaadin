package module.vaadin.ui.commons;

import java.util.Collection;

import module.vaadin.data.util.BufferedContainer;

import org.apache.commons.lang.StringUtils;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;

public class ContainerEditor extends CustomField {
    private final Table table;

    public ContainerEditor() {
	VerticalLayout layout = new VerticalLayout();
	layout.setSpacing(true);
	table = new Table();
	layout.addComponent(table);
	table.setWidth(100, UNITS_PERCENTAGE);
	table.setPageLength(0);
	table.addGeneratedColumn(StringUtils.EMPTY, new ColumnGenerator() {
	    @Override
	    public Component generateCell(final Table source, final Object itemId, Object columnId) {
		Button delete = new Button("delete");
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
	Button add = new Button("add");
	layout.addComponent(add);
	add.addListener(new ClickListener() {
	    @Override
	    public void buttonClick(ClickEvent event) {
		if (table.getContainerDataSource() instanceof BufferedContainer) {
		    BufferedContainer container = (BufferedContainer) table.getContainerDataSource();
		    container.addItem(container.getElementType());
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
    public Class<?> getType() {
	return Collection.class;
    }
}
