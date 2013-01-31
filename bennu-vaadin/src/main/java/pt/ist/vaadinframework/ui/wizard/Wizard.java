package pt.ist.vaadinframework.ui.wizard;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * @author David Martinho (davidmartinho@ist.utl.pt)
 */
public abstract class Wizard extends CustomComponent {

	public interface CloseListener {
		public void onWizardClose(Wizard wizard);
	}

	private Set<CloseListener> closeListenerSet = new HashSet<CloseListener>();

	private VerticalLayout container;

	private List<WizardStep> stepHistoryList;
	private int currentStep = 0;

	public Wizard(WizardStep initialStep) {
		stepHistoryList = new ArrayList<WizardStep>();
		initialStep.setWizard(this);
		stepHistoryList.add(initialStep);
		bindUi();
	}

	private void bindUi() {
		container = new VerticalLayout();
		container.setSpacing(true);
		setCompositionRoot(container);
	}

	public WizardStep getCurrentStep() {
		if (stepHistoryList.size() > 0) {
			return stepHistoryList.get(currentStep);
		} else {
			return null;
		}
	}

	public void startWizard() {
		setCurrentStep(getCurrentStep());
	}

	public void goToNextStep() {
		WizardStep nextStep = getCurrentStep().getNextStep();
		if (nextStep != null) {
			nextStep.setWizard(getCurrentStep().getWizard());
			stepHistoryList.add(nextStep);
			currentStep++;
			setCurrentStep(nextStep);
		}
	}

	public void goToPreviousStep() {
		stepHistoryList.remove(getCurrentStep());
		currentStep--;
		setCurrentStep(getCurrentStep());
	}

	private void setCurrentStep(WizardStep wizardStep) {
		container.removeAllComponents();
		container.addComponent(wizardStep);
	}

	public final void close() {
		for (CloseListener closeListener : closeListenerSet) {
			closeListener.onWizardClose(this);
		}
		Window subwindow = getWindow();
		(subwindow.getParent()).removeWindow(subwindow);
	}

	public void commit() {
		for (WizardStep wizardStep : stepHistoryList) {
			wizardStep.commit();
		}
	}

	public void addListener(CloseListener closeListener) {
		closeListenerSet.add(closeListener);
	}
}
