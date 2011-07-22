package module.vaadin.data.util;

/**
 * Custom constructor strategy. Implementors must provide a method with a
 * signature that has the types of the properties corresponding to the ids
 * returned by {@link #getOrderedArguments()}.
 * 
 * @author Pedro Santos (pedro.miguel.santos@ist.utl.pt)
 * 
 * @param <PropertyId>
 *            Type of the Ids of the properties
 */
public interface ItemConstructor<PropertyId> {
    public PropertyId[] getOrderedArguments();
}
