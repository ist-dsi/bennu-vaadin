package pt.ist.bennu.vaadin.ui.pages;

import myorg.domain.MyOrg;
import pt.ist.bennu.vaadin.data.BennuContainer;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

public class ServersPage extends CustomComponent {
    private static final long serialVersionUID = 3085157127270225011L;

    @Override
    public void attach() {
	super.attach();
	VerticalLayout layout = new VerticalLayout();
	final Table table = new Table() {
	    @Override
	    public void commit() throws SourceException, InvalidValueException {
		BennuContainer container = (BennuContainer) getContainerDataSource();
		container.save();
	    };
	};
	table.setWidth("100%");
	table.setContainerDataSource(new BennuContainer(MyOrg.getInstance().getVirtualHostsSet()));
	table.addContainerProperty("hostname", String.class, null);
	table.addContainerProperty("applicationTitle", String.class, null);
	table.addContainerProperty("applicationSubTitle", String.class, null);
	table.addContainerProperty("applicationCopyright", String.class, null);
	table.setEditable(true);
	layout.addComponent(table);

	Button save = new Button("Save");
	save.addListener(new Button.ClickListener() {
	    @Override
	    public void buttonClick(ClickEvent event) {
		table.commit();
	    }
	});
	layout.addComponent(save);
	setCompositionRoot(layout);
    }
}
