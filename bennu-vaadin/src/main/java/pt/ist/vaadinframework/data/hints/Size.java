package pt.ist.vaadinframework.data.hints;

import pt.ist.vaadinframework.data.HintedProperty.Hint;

import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Field;
import com.vaadin.ui.TextArea;

public class Size implements Hint {
    private float height = Sizeable.SIZE_UNDEFINED;
    private int heightUnits = Sizeable.UNITS_PERCENTAGE;
    private float width = Sizeable.SIZE_UNDEFINED;
    private int widthUnits = Sizeable.UNITS_PERCENTAGE;
    private int columns = -1;
    private int rows = 1;
    private int maxLength = -1;

    public Size() {
    }

    private Size(Size copy) {
	this.height = copy.height;
	this.heightUnits = copy.heightUnits;
	this.width = copy.width;
	this.widthUnits = copy.widthUnits;
	this.columns = copy.columns;
	this.rows = copy.rows;
	this.maxLength = copy.maxLength;
    }

    public Size height(float height, int heightUnits) {
	Size size = new Size(this);
	size.height = height;
	size.heightUnits = heightUnits;
	return size;
    }

    public Size width(float width, int widthUnits) {
	Size size = new Size(this);
	size.width = width;
	size.widthUnits = widthUnits;
	return size;
    }

    public Size cols(int columns) {
	Size size = new Size(this);
	size.columns = columns;
	return size;
    }

    public Size rows(int rows) {
	Size size = new Size(this);
	size.rows = rows;
	return size;
    }

    public Size maxLength(int maxLength) {
	Size size = new Size(this);
	size.maxLength = maxLength;
	return size;
    }

    @Override
    public Field applyHint(Field field) {
	field.setHeight(height, heightUnits);
	field.setWidth(width, widthUnits);
	if (field instanceof AbstractTextField) {
	    ((AbstractTextField) field).setColumns(columns);
	    ((AbstractTextField) field).setMaxLength(maxLength);
	    if (rows > 1) {
		if (field instanceof TextArea) {
		    ((TextArea) field).setRows(rows);
		} else {
		    TextArea area = new TextArea();
		    HintTools.copyConfiguration(field, area);
		    area.setRows(rows);
		    field = area;
		}
	    }
	}
	return field;
    }

    @Override
    public boolean appliesTo(Field field) {
	return true;
    }
}
