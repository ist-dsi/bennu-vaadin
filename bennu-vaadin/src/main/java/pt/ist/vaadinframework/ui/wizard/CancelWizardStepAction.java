package pt.ist.vaadinframework.ui.wizard;

/**
 * @author David Martinho (davidmartinho@ist.utl.pt)
 */
public class CancelWizardStepAction extends WizardStepAction {

    public CancelWizardStepAction(WizardStep currentStep, String actionLabel) {
	super(currentStep, actionLabel);
    }

    @Override
    public void execute(WizardStep wizardStep) {
	wizardStep.getWizard().close();
    }

}
