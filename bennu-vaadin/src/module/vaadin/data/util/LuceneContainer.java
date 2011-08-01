package module.vaadin.data.util;

import java.util.HashSet;
import java.util.List;

import module.vaadin.data.util.reflect.DomainContainer;
import module.vaadin.ui.commons.LuceneSelect;
import pt.ist.fenixframework.plugins.luceneIndexing.DomainIndexer;
import pt.ist.fenixframework.plugins.luceneIndexing.queryBuilder.dsl.BuildingState;
import pt.ist.fenixframework.plugins.luceneIndexing.queryBuilder.dsl.DSLState;
import pt.ist.fenixframework.pstm.AbstractDomainObject;

/**
 * Container for use with {@link LuceneSelect}.
 * 
 * @author Sérgio Silva (sergio.silva@ist.utl.pt)
 */
public class LuceneContainer<Type extends AbstractDomainObject> extends DomainContainer<Type> {

    private int maxHits = 1000000;

    public LuceneContainer(Class<? extends Type> elementType) {
	super(new HashSet<Type>(), elementType);
    }

    public void setMaxHits(int maxHits) {
	this.maxHits = maxHits;
    }

    protected DSLState createFilterExpression(String filterText) {
	final String[] split = filterText.trim().split("\\s+");
	if (split.length > 1) {
	    BuildingState expr = new BuildingState();

	    for (int i = 0; i < split.length - 1; i++) {
		expr = expr.matches(split[i]).and();
	    }

	    return expr.matches(split[split.length - 1]);

	} else {
	    return new BuildingState().matches(split[0]);
	}
    }

    public void search(String filterText) {
	removeAllItems();
	final DSLState expr = createFilterExpression(filterText);
	final List<? extends AbstractDomainObject> searchResult = DomainIndexer.getInstance().search(getElementType(), expr,
		maxHits);
	for (AbstractDomainObject domainObject : searchResult) {
	    addItem(domainObject);
	}
    }
}
