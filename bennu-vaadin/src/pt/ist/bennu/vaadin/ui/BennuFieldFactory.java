package pt.ist.bennu.vaadin.ui;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import pt.ist.bennu.vaadin.ui.fieldfactories.BigDecimalFieldFactory;
import pt.ist.bennu.vaadin.ui.fieldfactories.BooleanFieldFactory;
import pt.ist.bennu.vaadin.ui.fieldfactories.ByteFieldFactory;
import pt.ist.bennu.vaadin.ui.fieldfactories.CharacterFieldFactory;
import pt.ist.bennu.vaadin.ui.fieldfactories.DateFieldFactory;
import pt.ist.bennu.vaadin.ui.fieldfactories.DoubleFieldFactory;
import pt.ist.bennu.vaadin.ui.fieldfactories.EnumFieldFactory;
import pt.ist.bennu.vaadin.ui.fieldfactories.FieldFactory;
import pt.ist.bennu.vaadin.ui.fieldfactories.FloatFieldFactory;
import pt.ist.bennu.vaadin.ui.fieldfactories.IntegerFieldFactory;
import pt.ist.bennu.vaadin.ui.fieldfactories.LongFieldFactory;
import pt.ist.bennu.vaadin.ui.fieldfactories.ShortFieldFactory;
import pt.ist.bennu.vaadin.ui.fieldfactories.TextFieldFactory;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.TableFieldFactory;

public class BennuFieldFactory implements FormFieldFactory, TableFieldFactory {
    private static Map<Class<?>, FieldFactory> factories = new HashMap<Class<?>, FieldFactory>();

    static {
	addClassFactory(Byte.class, new ByteFieldFactory());
	addClassFactory(Short.class, new ShortFieldFactory());
	addClassFactory(Integer.class, new IntegerFieldFactory());
	addClassFactory(Long.class, new LongFieldFactory());
	addClassFactory(Float.class, new FloatFieldFactory());
	addClassFactory(Double.class, new DoubleFieldFactory());
	addClassFactory(Boolean.class, new BooleanFieldFactory());
	addClassFactory(Character.class, new CharacterFieldFactory());
	addClassFactory(String.class, new TextFieldFactory());
	addClassFactory(BigDecimal.class, new BigDecimalFieldFactory());
	addClassFactory(Enum.class, new EnumFieldFactory());
	addClassFactory(Date.class, new DateFieldFactory());
    }

    private final ResourceBundle bundle;

    public BennuFieldFactory(ResourceBundle bundle) {
	this.bundle = bundle;
    }

    @Override
    public Field createField(Item item, Object propertyId, Component uiContext) {
	Property property = item.getItemProperty(propertyId);
	Field field = createField(propertyId, property, uiContext);
	setCaption(field, propertyId, property);
	setDescription(field, propertyId, property);
	return field;
    }

    @Override
    public Field createField(Container container, Object itemId, Object propertyId, Component uiContext) {
	Property property = container.getContainerProperty(itemId, propertyId);
	Field field = createField(propertyId, property, uiContext);
	setCaption(field, propertyId, property);
	setDescription(field, propertyId, property);
	return field;
    }

    private Field createField(Object propertyId, Property property, Component uiContext) {
	Class<?> type = property.getType();
	if (type != null) {
	    if (factories.containsKey(type)) {
		return factories.get(type).createField(propertyId, property, uiContext);
	    }
	    for (Class<?> clazz : factories.keySet()) {
		if (clazz.isAssignableFrom(type)) {
		    factories.put(type, factories.get(clazz));
		    return factories.get(type).createField(propertyId, property, uiContext);
		}
	    }
	}
	return null;
    }

    public static boolean addClassFactory(Class<?> type, FieldFactory factory) {
	if (!factories.containsKey(type)) {
	    factories.put(type, factory);
	    return true;
	}
	return false;
    }

    private void setCaption(Field field, Object propertyId, Property property) {
	String key = property.getType().getName() + "." + propertyId;
	if (bundle.containsKey(key)) {
	    field.setCaption(bundle.getString(key));
	} else {
	    field.setCaption(createCaptionByPropertyId(propertyId));
	}
    }

    private void setDescription(Field field, Object propertyId, Property property) {
	String key = property.getType().getName() + "." + propertyId + ".description";
	if (bundle.containsKey(key)) {
	    field.setDescription(bundle.getString(key));
	}
    }

    private static String createCaptionByPropertyId(Object propertyId) {
	String name = propertyId.toString();
	if (name.contains(".")) {
	    name = name.substring(name.lastIndexOf('.'), name.length() + 1);
	}
	if (name.length() > 0) {
	    if (name.indexOf(' ') < 0 && name.charAt(0) == Character.toLowerCase(name.charAt(0))
		    && name.charAt(0) != Character.toUpperCase(name.charAt(0))) {
		StringBuffer out = new StringBuffer();
		out.append(Character.toUpperCase(name.charAt(0)));
		int i = 1;

		while (i < name.length()) {
		    int j = i;
		    for (; j < name.length(); j++) {
			char c = name.charAt(j);
			if (Character.toLowerCase(c) != c && Character.toUpperCase(c) == c) {
			    break;
			}
		    }
		    if (j == name.length()) {
			out.append(name.substring(i));
		    } else {
			out.append(name.substring(i, j));
			out.append(" " + name.charAt(j));
		    }
		    i = j + 1;
		}

		name = out.toString();
	    }
	}
	return name;
    }
}
