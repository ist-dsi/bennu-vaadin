package pt.ist.vaadinframework.data.old;


public class BennuItem<Type> {
    // private static final long serialVersionUID = 5900069060944359463L;
    // private final Type object;
    //
    // public BennuItem(Type object, String... properties) {
    // this.object = object;
    // for (String property : properties) {
    // addItemProperty(property);
    // }
    // }
    //
    // public void addItemProperty(String property) {
    // if (object instanceof AbstractDomainObject) {
    // addItemProperty(property, new LazySaveProperty(object, property));
    // } else {
    // addItemProperty(property, new MethodProperty(object, property));
    // }
    // }
    //
    // @Service
    // public void save() {
    // for (Object property : getItemPropertyIds()) {
    // Property item = getItemProperty(property);
    // if (item instanceof LazySaveProperty) {
    // ((LazySaveProperty) item).save();
    // }
    // }
    // }
}
