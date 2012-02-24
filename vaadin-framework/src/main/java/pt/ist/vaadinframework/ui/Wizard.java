package pt.ist.vaadinframework.ui;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class Wizard extends CustomComponent {
    private static final String CSS_STEP = "v-wizard-step";
    private static final String CSS_STEP_SELECTED = "v-wizard-step-selected";
    private static final String CSS_WIZARD = "v-wizard";
    private static final String CSS_WIZARD_CONTENT = "v-wizard-content";
    private static final String CSS_WIZARD_STEP_BAR = "v-wizard-step-bar";
    private static final String CSS_WIZARD_BUTTON_PANEL = "v-wizard-button-panel";
    private static final String CSS_WIZARD_BUTTON_PREV = "v-wizard-button-prev";
    private static final String CSS_WIZARD_BUTTON_NEXT = "v-wizard-button-next";

    private final AbstractOrderedLayout layout;
    private final StepBar tabsheet;
    private final List<WizardStep> steps;
    private int currentStepIndex;
    private WizardStep currentStep;
    private final HorizontalLayout content;
    private final ButtonPanel buttonPanel;
    private static Method ON_NEXT_STEP_EVENT_METHOD;
    private static Method ON_PREV_STEP_EVENT_METHOD;
    private static Method ON_WIZARD_COMPLETED_METHOD;
    static {
	try {
	    ON_NEXT_STEP_EVENT_METHOD = NextStepEventListener.class.getDeclaredMethod("nextStep",
		    new Class[] { OnNextStepEvent.class });
	    ON_PREV_STEP_EVENT_METHOD = PrevStepEventListener.class.getDeclaredMethod("prevStep",
		    new Class[] { OnPrevStepEvent.class });
	    ON_WIZARD_COMPLETED_METHOD = WizardCompletedListener.class.getDeclaredMethod("complete",
		    new Class[] { OnWizardCompleted.class });
	} catch (final java.lang.NoSuchMethodException e) {
	    // This should never happen
	    throw new java.lang.RuntimeException("Internal error finding methods in Wizard");
	}
    }

    public interface NextStepEventListener extends Serializable {

	public void nextStep(OnNextStepEvent event);

    }

    public interface PrevStepEventListener extends Serializable {

	public void prevStep(OnPrevStepEvent event);

    }

    public interface WizardCompletedListener extends Serializable {
	public void complete(OnWizardCompleted event);
    }

    public class OnStepEvent extends Component.Event {

	private static final long serialVersionUID = 1L;

	public OnStepEvent(Component source) {
	    super(source);
	    // TODO Auto-generated constructor stub
	}

	public WizardStep getWizardStep() {
	    return (WizardStep) getSource();
	}
    }

    public class OnNextStepEvent extends OnStepEvent {

	public OnNextStepEvent(Component source) {
	    super(source);
	    // TODO Auto-generated constructor stub
	}

    }

    public class OnPrevStepEvent extends OnStepEvent {

	public OnPrevStepEvent(Component source) {
	    super(source);
	    // TODO Auto-generated constructor stub
	}

    }

    public class OnWizardCompleted extends Component.Event {

	public OnWizardCompleted(Component source) {
	    super(source);
	    // TODO Auto-generated constructor stub
	}

    }

    private boolean fireWizardComplete() {
	if (currentStepIndex == steps.size() - 1) {
	    fireEvent(new OnWizardCompleted(currentStep));
	    return true;
	}
	return false;
    }

    private void fireNextStep() {
	if (!fireWizardComplete()) {
	    final int next = currentStepIndex + 1;
	    if (changeCurrentStep(next)) {
		fireEvent(new OnNextStepEvent(currentStep));
	    }
	}
    }

    private void firePrevStep() {
	final int prev = currentStepIndex - 1;
	if (changeCurrentStep(prev)) {
	    fireEvent(new OnPrevStepEvent(currentStep));
	}
    }

    private boolean changeCurrentStep(int index) {
	if (currentStepIndex == index) {
	    return false;
	}
	if (index >= 0 && index <= steps.size() - 1) {
	    currentStepIndex = index;
	    buttonPanel.prev.setVisible(currentStepIndex != 0);
	    currentStep = steps.get(currentStepIndex);
	    tabsheet.selectStep(currentStep);
	    return true;
	}
	return false;
    }

    public void addListener(NextStepEventListener listener) {
	super.addListener(OnNextStepEvent.class, listener, ON_NEXT_STEP_EVENT_METHOD);
    }

    public void addListener(PrevStepEventListener listener) {
	super.addListener(OnPrevStepEvent.class, listener, ON_PREV_STEP_EVENT_METHOD);
    }

    public void addListener(WizardCompletedListener listener) {
	super.addListener(OnWizardCompleted.class, listener, ON_WIZARD_COMPLETED_METHOD);
    }

    class ButtonPanel extends HorizontalLayout implements ClickListener {
	public Button prev;
	public Button next;

	public ButtonPanel(String prevCaption, String nextCaption) {
	    addStyleName(CSS_WIZARD_BUTTON_PANEL);
	    prev = new Button();
	    prev.setCaption(prevCaption);
	    prev.addListener(this);
	    prev.addStyleName(CSS_WIZARD_BUTTON_PREV);
	    addComponent(prev);

	    next = new Button();
	    next.setCaption(nextCaption);
	    next.addStyleName(CSS_WIZARD_BUTTON_NEXT);
	    next.addListener(this);
	    addComponent(next);
	}

	@Override
	public void buttonClick(ClickEvent event) {
	    if (event.getButton().equals(next)) {
		fireNextStep();
	    } else if (event.getButton().equals(prev)) {
		firePrevStep();
	    }
	}

    }

    class StepBar extends CustomComponent {
	private final HorizontalLayout stepBar;
	private final Map<Integer, Label> headers;

	public StepBar() {
	    stepBar = new HorizontalLayout();
	    stepBar.setWidth("100%");
	    stepBar.addStyleName(CSS_WIZARD_STEP_BAR);
	    headers = new HashMap<Integer, Label>();
	    setCompositionRoot(stepBar);
	}

	public void addStep(Component c, String label) {
	    final Label lblHeader = new Label(label);
	    lblHeader.addStyleName(CSS_STEP);
	    lblHeader.setImmediate(true);
	    headers.put(steps.indexOf(c), lblHeader);
	    stepBar.addComponent(lblHeader);
	}

	public void selectStep(Component c) {
	    final int curr = steps.indexOf(c);
	    for (Integer i : headers.keySet()) {
		final Label label = headers.get(i);
		if (i != curr) {
		    label.removeStyleName(CSS_STEP_SELECTED);
		    label.addStyleName(CSS_STEP);
		} else {
		    label.addStyleName(CSS_STEP_SELECTED);
		}
	    }
	    content.removeAllComponents();
	    content.addComponent(c);
	}
    }

    public Wizard(final List<WizardStep> steps, String prevCaption, String nextCaption) {
	addStyleName(CSS_WIZARD);

	currentStepIndex = -1;
	currentStep = null;
	this.steps = steps;

	layout = new VerticalLayout();
	layout.setSizeFull();

	tabsheet = new StepBar();
	tabsheet.setWidth("100%");

	content = new HorizontalLayout();
	content.setSizeFull();
	content.addStyleName(CSS_WIZARD_CONTENT);

	buttonPanel = new ButtonPanel(prevCaption, nextCaption);
	createVerticalWizard(prevCaption, nextCaption);
	changeCurrentStep(0);
    }

    private void createVerticalWizard(String prevCaption, String nextCaption) {
	for (WizardStep ws : steps) {
	    tabsheet.addStep(ws, ws.getStepTitle());
	}
	layout.addComponent(tabsheet);
	layout.addComponent(content);
	layout.setExpandRatio(content, 1f);
	layout.addComponent(buttonPanel);
    }

    @Override
    public void attach() {
	super.attach();
	setSizeFull();
	setCompositionRoot(layout);
    }

}
