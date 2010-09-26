package pt.ist.bennu.vaadin.data;

import pt.ist.fenixWebFramework.services.Service;
import pt.ist.fenixframework.pstm.AbstractDomainObject;

import com.vaadin.data.Property;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.data.util.PropertysetItem;

public class BennuItem extends PropertysetItem {
    private static final long serialVersionUID = 5900069060944359463L;
    private final Object object;

    public BennuItem(Object object, String... properties) {
	this.object = object;
	for (String property : properties) {
	    addItemProperty(property);
	}
    }

    public void addItemProperty(String property) {
	if (object instanceof AbstractDomainObject) {
	    addItemProperty(property, new LazySaveProperty(object, property));
	} else {
	    addItemProperty(property, new MethodProperty(object, property));
	}
    }

    @Service
    public void save() {
	for (Object property : getItemPropertyIds()) {
	    Property item = getItemProperty(property);
	    if (item instanceof LazySaveProperty) {
		((LazySaveProperty) item).save();
	    }
	}
    }
}
