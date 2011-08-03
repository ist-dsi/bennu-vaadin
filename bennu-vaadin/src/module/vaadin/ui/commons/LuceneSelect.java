package module.vaadin.ui.commons;

import module.vaadin.data.util.LuceneContainer;
import pt.ist.bennu.ui.TimeoutSelect;

import com.vaadin.data.Container;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;

/**
 * @author Sérgio Silva (sergio.silva@ist.utl.pt)
 */
public class LuceneSelect extends TimeoutSelect implements TextChangeListener {
    public LuceneSelect() {
	super();
	addListener((TextChangeListener) this);
    }

    public LuceneSelect(String caption, Container dataSource) {
	super(caption, dataSource);
	addListener((TextChangeListener) this);
    }

    @Override
    public void setContainerDataSource(Container newDataSource) {
	if (newDataSource instanceof LuceneContainer) {
	    super.setContainerDataSource(newDataSource);
	} else {
	    throw new UnsupportedOperationException("Container must be of type: module.vaadin.data.util.LuceneContainer");
	}
    }

    @Override
    public void textChange(TextChangeEvent event) {
	final LuceneContainer<?> luceneContainer = (LuceneContainer<?>) getContainerDataSource();
	if (luceneContainer == null) {
	    throw new UnsupportedOperationException("You must set the container datasource first.");
	}
	luceneContainer.search(event.getText());
    }
}
