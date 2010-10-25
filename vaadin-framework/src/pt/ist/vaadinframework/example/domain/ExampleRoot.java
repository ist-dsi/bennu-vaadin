package pt.ist.vaadinframework.example.domain;

import pt.ist.fenixframework.FenixFramework;

public class ExampleRoot extends ExampleRoot_Base {
    public ExampleRoot() {
	super();
	if (FenixFramework.getRoot() != null && FenixFramework.getRoot() != this) {
	    throw new Error("There can only be one! (instance of root)");
	}

	setPerson(new Person());
    }

    public static ExampleRoot getInstance() {
	return FenixFramework.getRoot();
    }
}
