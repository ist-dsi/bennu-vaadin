package pt.ist.bennu.vaadin.ui.pages;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;

public class WelcomePage extends CustomComponent {
    private static final long serialVersionUID = -2635167686892240156L;

    @Override
    public void attach() {
	super.attach();
	setCompositionRoot(new Label("welcome to bennu."));
    }
}
