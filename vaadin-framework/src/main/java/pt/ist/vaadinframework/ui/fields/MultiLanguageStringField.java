/*
 * Copyright 2010 Instituto Superior Tecnico
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the vaadin-framework.
 *
 *   The vaadin-framework Infrastructure is free software: you can 
 *   redistribute it and/or modify it under the terms of the GNU Lesser General 
 *   Public License as published by the Free Software Foundation, either version 
 *   3 of the License, or (at your option) any later version.*
 *
 *   vaadin-framework is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with vaadin-framework. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package pt.ist.vaadinframework.ui.fields;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import pt.ist.vaadinframework.ui.FieldFactory;
import pt.utl.ist.fenix.tools.util.i18n.Language;
import pt.utl.ist.fenix.tools.util.i18n.MultiLanguageString;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.ui.Form;

/**
 * @author Pedro Santos (pedro.miguel.santos@ist.utl.pt)
 * 
 */
@SuppressWarnings("serial")
public class MultiLanguageStringField extends Form {
    public static class MultiLanguageStringItem implements Item {
	private final Property source;

	private final Map<Language, MultiLanguageStringItemProperty> properties = new HashMap<Language, MultiLanguageStringItemProperty>();

	public MultiLanguageStringItem(Property source, Language... languages) {
	    this.source = source;
	    for (Language language : languages) {
		properties.put(language, new MultiLanguageStringItemProperty(language));
	    }
	}

	@Override
	public Property getItemProperty(Object id) {
	    return properties.get(id);
	}

	@Override
	public Collection<Language> getItemPropertyIds() {
	    return properties.keySet();
	}

	@Override
	public boolean addItemProperty(Object id, Property property) throws UnsupportedOperationException {
	    throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeItemProperty(Object id) throws UnsupportedOperationException {
	    throw new UnsupportedOperationException();
	}

	public class MultiLanguageStringItemProperty implements Property {
	    private final Language language;

	    public MultiLanguageStringItemProperty(Language language) {
		this.language = language;
	    }

	    @Override
	    public Object getValue() {
		if (source.getValue() == null) {
		    return null;
		} else {
		    return ((MultiLanguageString) source.getValue()).getContent(language);
		}
	    }

	    @Override
	    public void setValue(Object newValue) throws ReadOnlyException, ConversionException {
		if (source.isReadOnly()) {
		    throw new ReadOnlyException();
		}
		if (source.getValue() == null) {
		    source.setValue(new MultiLanguageString());
		}
		((MultiLanguageString) source.getValue()).setContent(language, newValue.toString());
	    }

	    @Override
	    public Class<?> getType() {
		return String.class;
	    }

	    @Override
	    public boolean isReadOnly() {
		return source.isReadOnly();
	    }

	    @Override
	    public void setReadOnly(boolean newStatus) {
		source.setReadOnly(newStatus);
	    }
	}
    }

    private final Language[] languages;

    public MultiLanguageStringField(ResourceBundle bundle, Language... languages) {
	this.languages = languages;
	setFormFieldFactory(new FieldFactory(bundle));
	setWriteThrough(true);
	setImmediate(true);
    }

    @Override
    public void setPropertyDataSource(Property newDataSource) {
	super.setPropertyDataSource(newDataSource);
	if (!MultiLanguageString.class.isAssignableFrom(newDataSource.getType())) {
	    throw new UnsupportedOperationException("Property must be a MultiLanguageString");
	}
	setItemDataSource(new MultiLanguageStringItem(newDataSource, languages));
    }
}
