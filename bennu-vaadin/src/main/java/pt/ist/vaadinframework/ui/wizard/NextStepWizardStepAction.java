package pt.ist.vaadinframework.ui.wizard;

/**
 * @author David Martinho (davidmartinho@ist.utl.pt)
 */
public class NextStepWizardStepAction extends WizardStepAction {

	public NextStepWizardStepAction(WizardStep currentStep, String actionLabel) {
		super(currentStep, actionLabel);
	}

	@Override
	public void execute(WizardStep wizardStep) {
		wizardStep.getWizard().goToNextStep();
	}

}
