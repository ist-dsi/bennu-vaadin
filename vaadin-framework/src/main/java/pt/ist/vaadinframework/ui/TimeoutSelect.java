package pt.ist.vaadinframework.ui;

import java.util.Map;

import com.vaadin.data.Container;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.FieldEvents.TextChangeNotifier;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.terminal.gwt.client.ui.VTextField;
import com.vaadin.terminal.gwt.client.ui.VTimeoutSelect;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.ClientWidget;
import com.vaadin.ui.Component;
import com.vaadin.ui.Select;

/**
 * @author Sérgio Silva (sergio.silva@ist.utl.pt)
 * 
 */

@ClientWidget(VTimeoutSelect.class)
public class TimeoutSelect extends Select implements TextChangeNotifier {

    private String curText = null;

    private int timeout = 1000;

    // private Object selected;

    public TimeoutSelect() {
	super();
	setImmediate(true);
	super.setFilteringMode(Select.FILTERINGMODE_OFF);
    }

    public TimeoutSelect(String caption, Container dataSource) {
	super(caption, dataSource);
	setImmediate(true);
	super.setFilteringMode(Select.FILTERINGMODE_OFF);
    }

    public TimeoutSelect(String caption) {
	super(caption);
	setImmediate(true);
	super.setFilteringMode(Select.FILTERINGMODE_OFF);
    }

    @Override
    public void changeVariables(Object source, Map<String, Object> variables) {

	// if (variables.containsKey("selected")) {
	// final String[] keys = (String[]) variables.get("selected");
	// if (!isMultiSelect()) {
	// if (keys.length > 0) {
	// selected = itemIdMapper.get(keys[0]);
	// }
	// }
	// }

	if (variables.containsKey("filter")) {
	    final String newText = (String) variables.get("filter");
	    if (!newText.equals(curText)) {
		setText(newText);
	    } else {
		// variables.remove("filter");
	    }
	}

	super.changeVariables(source, variables);
    }

    private class TextChangeEventImpl extends TextChangeEvent {

	public TextChangeEventImpl(Component arg0) {
	    super(arg0);
	}

	@Override
	public String getText() {
	    return ((TimeoutSelect) getComponent()).curText;
	}

	@Override
	public int getCursorPosition() {
	    return 0;
	}

    }

    public void setContainerDataSource(Container newDataSource) {
	if (newDataSource == null) {
	    newDataSource = new IndexedContainer();
	}

	getCaptionChangeListener().clear();

	if (items != newDataSource) {

	    // Removes listeners from the old datasource
	    if (items != null) {
		if (items instanceof Container.ItemSetChangeNotifier) {
		    ((Container.ItemSetChangeNotifier) items).removeListener(this);
		}
		if (items instanceof Container.PropertySetChangeNotifier) {
		    ((Container.PropertySetChangeNotifier) items).removeListener(this);
		}
	    }

	    // Assigns new data source
	    items = newDataSource;

	    // Clears itemIdMapper also
	    itemIdMapper.removeAll();

	    // Adds listeners
	    if (items != null) {
		if (items instanceof Container.ItemSetChangeNotifier) {
		    ((Container.ItemSetChangeNotifier) items).addListener(this);
		}
		if (items instanceof Container.PropertySetChangeNotifier) {
		    ((Container.PropertySetChangeNotifier) items).addListener(this);
		}
	    }

	    /*
	     * We expect changing the data source should also clean value. See
	     * #810, #4607, #5281
	     */
	    // if (selected != null && !items.containsId(selected)) {
	    // setValue(null);
	    // }

	    requestRepaint();

	}
    }

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
	super.paintContent(target);

	if (hasListeners(TextChangeEvent.class)) {
	    target.addAttribute(VTextField.ATTR_TEXTCHANGE_EVENTMODE, AbstractTextField.TextChangeEventMode.TIMEOUT.toString());
	    target.addAttribute(VTextField.ATTR_TEXTCHANGE_TIMEOUT, getTimeout());
	}

    }

    private int getTimeout() {
	return timeout;
    }

    public void setTimeout(int timeout) {
	this.timeout = timeout;
	requestRepaint();
    }

    public void addListener(TextChangeListener listener) {
	addListener(TextChangeListener.EVENT_ID, TextChangeEvent.class, listener, TextChangeListener.EVENT_METHOD);
    }

    public void removeListener(TextChangeListener listener) {
	removeListener(listener);
    }

    @Override
    public void setFilteringMode(int filteringMode) {
    }

    public void setText(String newText) {
	curText = newText;
	fireEvent(new TextChangeEventImpl(this));
    }

}
