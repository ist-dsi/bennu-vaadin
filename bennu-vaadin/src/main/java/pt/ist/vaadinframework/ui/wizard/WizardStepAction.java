package pt.ist.vaadinframework.ui.wizard;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomComponent;

/**
 * @author David Martinho (davidmartinho@ist.utl.pt)
 */
public abstract class WizardStepAction extends CustomComponent {

    private String label;
    private Button button;
    
    public WizardStepAction(final WizardStep currentStep, String actionLabel) {
        button = new Button(actionLabel);
        button.addListener(new ClickListener() {
            public void buttonClick(ClickEvent event) {
                execute(currentStep);
            }
        });
        setCompositionRoot(button);
    }

    public String getLabel() {
        return label;
    }
   
    public Button getButton() {
        return button;
    }
    
    public abstract void execute(WizardStep wizardStep);
    
}
