package pt.ist.vaadinframework.data.hints;

import pt.ist.vaadinframework.data.HintedProperty.Hint;

import com.vaadin.ui.Field;
import com.vaadin.ui.RichTextArea;

public class RichText implements Hint {
    @Override
    public Field applyHint(Field field) {
        RichTextArea area = new RichTextArea();
        HintTools.copyConfiguration(field, area);
        return area;
    }

    @Override
    public boolean appliesTo(Field field) {
        return true;
    }
}
