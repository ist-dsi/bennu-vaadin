//package module.vaadin.data.util;
//
//import java.util.Arrays;
//import java.util.Collection;
//import java.util.HashSet;
//
//public class CollectionHintedProperty<T> extends ObjectHintedProperty<HashSet<T>> {
//    
//    public CollectionHintedProperty() {
//	super(null,HashSet.class);
//	
//    }
//    
//    public CollectionHintedProperty(Object value, Class<HashSet<T>> type, Hint... hints) {
//	super(value, type, Arrays.asList(hints));
//    }
//    
//    public CollectionHintedProperty(Object value, Class<HashSet<T>> type, Collection<Hint> hints) {
//	super(value, type, hints);
//    }
//    
//    public void add(T value) {
//	final Collection<T> curr = getValue();
//	curr.add(value);
//	setValue(curr);
//    }
//    
//    public void addAll(Collection<T> value) {
//   	final Collection<T> curr = getValue();
//   	curr.addAll(value);
//   	setValue(curr);
//    }
//
//}
