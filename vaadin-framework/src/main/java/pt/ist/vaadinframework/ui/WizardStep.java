package pt.ist.vaadinframework.ui;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;

public class WizardStep extends CustomComponent {
    private static final String CSS_WIZARD_STEP = "v-wizardstep";
    private static final String CSS_DESCRIPTION = "v-wizardstep-description";
    private static final String CSS_ERRORS = "v-wizardstep-errors";
    private static final String CSS_CONTENT = "v-wizardstep-content";
    private final VerticalLayout layout;
    private final HorizontalLayout hlDescription;
    private final HorizontalLayout hlErrors;
    private final HorizontalLayout hlContent;
    private String stepTitle;

    public WizardStep() {
	setStepTitle("");
	layout = new VerticalLayout();
	layout.addStyleName(CSS_WIZARD_STEP);

	hlDescription = new HorizontalLayout();
	hlDescription.setWidth("100%");
	hlDescription.addStyleName(CSS_DESCRIPTION);

	hlErrors = new HorizontalLayout();
	hlErrors.setWidth("100%");
	hlErrors.addStyleName(CSS_ERRORS);

	hlContent = new HorizontalLayout();
	hlContent.setSizeFull();
	hlContent.addStyleName(CSS_CONTENT);

	layout.addComponent(hlDescription);
	layout.addComponent(hlErrors);
	layout.addComponent(hlContent);
	layout.setExpandRatio(hlContent, 1f);
	setCompositionRoot(layout);
    }

    public WizardStep(String stepTitle, Component description, Component errors, Component content) {
	this();
	addDescription(description);
	addErrors(errors);
	addContent(content);
	setStepTitle(stepTitle);
    }

    private void setStepTitle(String stepTitle) {
	this.stepTitle = stepTitle;
    }

    private static void addComponent(Layout layout, Component c) {
	layout.removeAllComponents();
	layout.addComponent(c);
    }

    public void addDescription(Component c) {
	addComponent(hlDescription, c);
    }

    public void addErrors(Component c) {
	addComponent(hlErrors, c);
    }

    public void addContent(Component c) {
	addComponent(hlContent, c);
    }

    public Component getContent() {
	return hlContent.getComponent(0);
    }

    public String getStepTitle() {
	return stepTitle;
    }
}
