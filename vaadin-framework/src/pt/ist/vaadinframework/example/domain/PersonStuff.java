package pt.ist.vaadinframework.example.domain;

public class PersonStuff extends PersonStuff_Base {

    public PersonStuff(Boolean important) {
	super();
	setRoot(ExampleRoot.getInstance());
	setImportant(important);
    }

}
