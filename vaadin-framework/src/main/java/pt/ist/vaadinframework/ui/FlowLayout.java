package pt.ist.vaadinframework.ui;

import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;

public class FlowLayout extends CssLayout {
    public static final String TAGNAME = "inline";
    public static final String CLASSNAME = "v-" + TAGNAME;

    @Override
    public void addComponent(Component c) {
	super.addComponent(c);
	c.addStyleName(CLASSNAME);
    }

    @Override
    public void addComponent(Component c, int index) {
	super.addComponent(c, index);
	c.addStyleName(CLASSNAME);
    }

    @Override
    public void addComponentAsFirst(Component c) {
	super.addComponentAsFirst(c);
	c.addStyleName(CLASSNAME);
    }
}
