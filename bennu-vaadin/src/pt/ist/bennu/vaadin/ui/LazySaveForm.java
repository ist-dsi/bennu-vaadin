package pt.ist.bennu.vaadin.ui;

import java.util.ResourceBundle;

import pt.ist.bennu.vaadin.data.BennuItem;
import pt.ist.fenixWebFramework.services.Service;

import com.vaadin.data.Item;
import com.vaadin.ui.Form;

public class LazySaveForm extends Form {
    public LazySaveForm(ResourceBundle bundle) {
	setFormFieldFactory(new BennuFieldFactory(bundle));
    }

    @Override
    public void commit() throws SourceException {
	super.commit();
	save();
    }

    @Service
    private void save() {
	Item item = getItemDataSource();
	if (item instanceof BennuItem) {
	    ((BennuItem) item).save();
	}
    }
}
