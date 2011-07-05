package module.vaadin.data.util;

public interface ItemConstructor<PropertyId, Type> {
    public PropertyId[] getOrderedArguments();
}
