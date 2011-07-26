package module.vaadin.ui.commons;

import java.util.Map;

import module.vaadin.data.util.LuceneContainer;

import com.vaadin.data.Container;
import com.vaadin.ui.Select;

/**
 * @author Sérgio Silva (sergio.silva@ist.utl.pt)
 */
public class LuceneSelect extends Select {
    public LuceneSelect() {
	super();
    }

    public LuceneSelect(String caption, Container dataSource) {
	super(caption, dataSource);
    }

    {
	setFilteringMode(FILTERINGMODE_OFF);
	setNullSelectionAllowed(false);
    }

    public LuceneSelect(String caption) {
	super(caption);
	setFilteringMode(FILTERINGMODE_OFF);
	setNullSelectionAllowed(false);
    }

    @Override
    public void changeVariables(Object source, Map<String, Object> variables) {
	if (getContainerDataSource() != null && getContainerDataSource() instanceof LuceneContainer) {
	    if (variables.containsKey("filter")) {
		final String filterText = (String) variables.get("filter");
		final LuceneContainer<?> luceneContainer = (LuceneContainer<?>) getContainerDataSource();
		luceneContainer.search(filterText);
	    }
	}
	super.changeVariables(source, variables);
    }

    // @Override
    // public void setFilteringMode(int filteringMode) {
    // if (filteringMode != FILTERINGMODE_OFF) {
    // super.setFilteringMode(FILTERINGMODE_CONTAINS);
    // }
    // }

}
