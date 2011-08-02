package com.vaadin.ui;

import pt.ist.bennu.ui.TimeoutSelect;

import com.vaadin.data.Container;
import com.vaadin.data.util.LuceneIndexedContainer;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;


/**
 * @author Sérgio Silva (sergio.silva@ist.utl.pt)
 * 
 */

public class LuceneSelect extends TimeoutSelect implements TextChangeListener {
    	
    
    	public LuceneSelect() {
    	    super();
    	    addListener((TextChangeListener)this);
	}
    	
    	
    	
	public LuceneSelect(String caption, Container dataSource) {
	    super(caption, dataSource);
	    addListener((TextChangeListener)this);
	}



	public LuceneSelect(String caption) {
	    super(caption);
	    addListener((TextChangeListener)this);
	}

	@Override
	public void setContainerDataSource(Container newDataSource) {
//	    if (!(newDataSource instanceof LuceneIndexedContainer)) {
//		throw new UnsupportedOperationException("The container datasource must inherit from LuceneIndexedContainer.");
//	    } 
	    super.setContainerDataSource(newDataSource);
	}

	@Override
	public void textChange(TextChangeEvent event) {
	    final LuceneIndexedContainer luceneContainer = (LuceneIndexedContainer) getContainerDataSource();
	    if (luceneContainer == null) {
		throw new UnsupportedOperationException("You must set the container datasource first.");
	    }
	    luceneContainer.search(event.getText());
	}
	
}
