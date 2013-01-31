package pt.ist.vaadinframework.ui.wizard;

/**
 * @author David Martinho (davidmartinho@ist.utl.pt)
 */
public class PreviousStepWizardStepAction extends WizardStepAction {

	public PreviousStepWizardStepAction(WizardStep currentStep, String actionLabel) {
		super(currentStep, actionLabel);
	}

	@Override
	public void execute(WizardStep currentStep) {
		currentStep.getWizard().goToPreviousStep();
	}

}
