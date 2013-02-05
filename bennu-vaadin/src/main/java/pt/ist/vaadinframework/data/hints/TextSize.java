/*
 * Copyright 2011 Instituto Superior Tecnico
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
package pt.ist.vaadinframework.data.hints;

import pt.ist.vaadinframework.data.HintedProperty.Hint;

import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Field;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

public class TextSize implements Hint {
    private final int rows;

    public TextSize(int rows) {
        this.rows = rows;
    }

    @Override
    public Field applyHint(Field field) {
        if (field instanceof TextArea) {
            TextArea area = (TextArea) field;
            if (rows > 1) {
                area.setRows(rows);
            } else {
                TextField newField = new TextField();
                newField.setCaption(area.getCaption());
                newField.setDescription(area.getDescription());
                newField.setImmediate(area.isImmediate());
                newField.setInvalidAllowed(area.isInvalidAllowed());
                newField.setInvalidCommitted(area.isInvalidCommitted());
                newField.setNullRepresentation(area.getNullRepresentation());
                newField.setReadThrough(area.isReadThrough());
                newField.setReadOnly(area.isReadOnly());
                newField.setRequired(area.isRequired());
                newField.setWidth(area.getWidth(), area.getWidthUnits());
                return newField;
            }
        }
        return field;
    }

    @Override
    public boolean appliesTo(Field field) {
        return field instanceof AbstractTextField;
    }

}
