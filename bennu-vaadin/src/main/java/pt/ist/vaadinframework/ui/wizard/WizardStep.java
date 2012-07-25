package pt.ist.vaadinframework.ui.wizard;

import java.util.List;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.VerticalLayout;

/**
 * @author David Martinho (davidmartinho@ist.utl.pt)
 */
public abstract class WizardStep extends CustomComponent {

    private Wizard wizard;
    
    private static final String CSS_WIZARD_STEP = "v-wizardstep";
    private static final String CSS_DESCRIPTION = "v-wizardstep-description";
    private static final String CSS_ERRORS = "v-wizardstep-errors";
    private static final String CSS_CONTENT = "v-wizardstep-content";
    private static final String CSS_ACTIONS = "v-wizardstep-actions";
    
    private String title;
    private VerticalLayout container;
    private HorizontalLayout descriptionContainer;
    private HorizontalLayout errorsContainer;
    private HorizontalLayout contentContainer;
    private HorizontalLayout actionsContainer;

    private List<WizardStepAction> wizardStepActionList;
    
    public WizardStep() {
        setStepTitle("");
        bindUi();
    }

    private void bindUi() {
        container = new VerticalLayout();
        container.addStyleName(CSS_WIZARD_STEP);

        descriptionContainer = new HorizontalLayout();
        descriptionContainer.setWidth("100%");
        descriptionContainer.addStyleName(CSS_DESCRIPTION);

        errorsContainer = new HorizontalLayout();
        errorsContainer.setWidth("100%");
        errorsContainer.addStyleName(CSS_ERRORS);

        contentContainer = new HorizontalLayout();
        contentContainer.setSizeFull();
        contentContainer.addStyleName(CSS_CONTENT);

        actionsContainer = new HorizontalLayout();
        actionsContainer.setSpacing(true);
        actionsContainer.addStyleName(CSS_ACTIONS);

        container.addComponent(descriptionContainer);
        container.addComponent(errorsContainer);
        container.addComponent(contentContainer);
        container.addComponent(actionsContainer);
        
        container.setExpandRatio(contentContainer, 1f);
        setCompositionRoot(container);
    }
    
    public Wizard getWizard() {
        return wizard;
    }

    public abstract WizardStep getNextStep();
    
    public void commit() {
    }
    
    public Component getContentPanel() {
	return contentContainer.getComponent(0);
    }

    private void setComponent(Layout layout, Component c) {
	layout.removeAllComponents();
	layout.addComponent(c);
    }

    public void setDescriptionPanel(Component c) {
	setComponent(descriptionContainer, c);
    }

    public void setErrorPanel(Component c) {
	setComponent(errorsContainer, c);
    }

    public void setContentPanel(Component c) {
	setComponent(contentContainer, c);
    }

    public final void setStepTitle(String title) {
        this.title = title;
    }

    public String getStepTitle() {
        return title;
    }
    
    public List<WizardStepAction> getWizardStepActionList() {
        return wizardStepActionList;
    }
    
    public final void setWizardStepActionList(List<WizardStepAction> wizardStepActionList) {
        actionsContainer.removeAllComponents();
        for(WizardStepAction action : wizardStepActionList) {
            actionsContainer.addComponent(action);
        }
    }

    public void setWizard(Wizard wizard) {
        this.wizard = wizard;
    }
}
