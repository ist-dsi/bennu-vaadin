package pt.ist.bennu.vaadin;

import myorg.domain.contents.NodeBean;
import pt.ist.bennu.vaadin.domain.contents.VaadinNodeType;

public class ModuleInitializer {

    static {
	NodeBean.registerNodeType(new VaadinNodeType());
    }

}
