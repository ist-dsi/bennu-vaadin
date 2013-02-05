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

import java.util.HashMap;
import java.util.Map;

import pt.utl.ist.fenix.tools.util.i18n.Language;
import pt.utl.ist.fenix.tools.util.i18n.MultiLanguageString;

import com.vaadin.data.Property;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.PropertyFormatter;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Field;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

/**
 * @author Pedro Santos (pedro.miguel.santos@ist.utl.pt)
 * 
 */
@SuppressWarnings("serial")
public class MultiLanguageStringField extends CustomField {

    private final Language[] languages;

    private final Map<Language, TextField> fields = new HashMap<Language, TextField>();

    public MultiLanguageStringField(String bundlename, Language... languages) {
        this.languages = languages;
        GridLayout languagesLayout = new GridLayout(3, languages.length);
        languagesLayout.setSpacing(true);
        for (int i = 0; i < languages.length; ++i) {
            fields.put(languages[i], new TextField());
            languagesLayout.addComponent(fields.get(languages[i]), 0, i);

            Embedded languageIcon = new Embedded(null, new ThemeResource("../icons/flags/" + languages[i].toString() + ".gif"));
            languagesLayout.addComponent(languageIcon, 1, i);
            languagesLayout.setComponentAlignment(languageIcon, Alignment.MIDDLE_RIGHT);

            Label languageLabel = new Label(languages[i].toString());
            languagesLayout.addComponent(languageLabel, 2, i);
            languagesLayout.setComponentAlignment(languageLabel, Alignment.MIDDLE_LEFT);
        }
        setCompositionRoot(languagesLayout);
    }

    @Override
    public void setPropertyDataSource(final Property newDataSource) {
        super.setPropertyDataSource(newDataSource);
        for (final Language language : languages) {
            fields.get(language).setPropertyDataSource(new PropertyFormatter(newDataSource) {
                @Override
                public Object parse(String formattedValue) throws Exception {
                    MultiLanguageString current = (MultiLanguageString) newDataSource.getValue();
                    if (current == null) {
                        current = new MultiLanguageString();
                    }
                    return current.with(language, formattedValue);
                }

                @Override
                public String format(Object value) {
                    String content = ((MultiLanguageString) value).getContent(language);
                    if (content == null) {
                        return "";
                    }
                    return content;
                }
            });
        }
    }

    @Override
    public void setWriteThrough(boolean writeThrough) throws SourceException, InvalidValueException {
        for (TextField childField : fields.values()) {
            childField.setWriteThrough(writeThrough);
        }
        super.setWriteThrough(writeThrough);
    }

    @Override
    public void setEnabled(boolean enabled) {
        for (TextField childField : fields.values()) {
            childField.setEnabled(enabled);
        }
        super.setEnabled(enabled);
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        for (TextField childField : fields.values()) {
            childField.setReadOnly(readOnly);
        }
        super.setReadOnly(readOnly);
    }

    @Override
    public void setVisible(boolean visible) {
        for (TextField childField : fields.values()) {
            childField.setVisible(visible);
        }
        super.setVisible(visible);
    }

    @Override
    public void setImmediate(boolean immediate) {
        for (TextField childField : fields.values()) {
            childField.setImmediate(immediate);
        }
        super.setImmediate(immediate);
    }

    @Override
    public void setReadThrough(boolean readThrough) throws SourceException {
        for (TextField childField : fields.values()) {
            childField.setReadThrough(readThrough);
        }
        super.setReadThrough(readThrough);
    }

    @Override
    protected boolean isEmpty() {
        for (TextField childField : fields.values()) {
            if (((String) childField.getValue()).length() > 0) {
                return true;
            }
        }
        return false;
    }

    public TextField getTextField(Language language) {
        return fields.get(language);
    }

    @Override
    public void commit() throws SourceException, InvalidValueException {
        for (Field field : fields.values()) {
            field.commit();
        }
        super.commit();
    }

    @Override
    public Class<?> getType() {
        return MultiLanguageString.class;
    }
}
