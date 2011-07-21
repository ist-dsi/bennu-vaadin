package com.vaadin.ui;

import java.util.Map;

import com.vaadin.data.Container;
import com.vaadin.data.util.LuceneIndexedContainer;


/**
 * @author Sérgio Silva (sergio.silva@ist.utl.pt)
 * 
 */

public class LuceneSelect extends Select {
    	
    	public LuceneSelect() {
    	    super();
    	    setFilteringMode(FILTERINGMODE_OFF);
    	    setNullSelectionAllowed(false);
	}
    	
    	
    	
	public LuceneSelect(String caption, Container dataSource) {
	    super(caption, dataSource);
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
	    if (variables.containsKey("filter")) {
		final String filterText = (String) variables.get("filter");
		final LuceneIndexedContainer luceneContainer = (LuceneIndexedContainer) getContainerDataSource();
		luceneContainer.search(filterText);
	    }
	    super.changeVariables(source, variables);
	}
	
	@Override
	public void setContainerDataSource(Container newDataSource) {
	    if (newDataSource instanceof LuceneIndexedContainer) {
		super.setContainerDataSource(newDataSource);
	    }
	}
	
//	@Override
//	public void setFilteringMode(int filteringMode) {
//	    if (filteringMode != FILTERINGMODE_OFF) {
//		super.setFilteringMode(FILTERINGMODE_CONTAINS);
//	    }
//	}
	
}
