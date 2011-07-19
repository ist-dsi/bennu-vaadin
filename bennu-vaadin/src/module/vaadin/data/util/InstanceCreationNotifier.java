package module.vaadin.data.util;

public interface InstanceCreationNotifier<ItemId, ItemType> {
    public void addListener(InstanceCreationListener<ItemId, ItemType> listener);

    public void removeListener(InstanceCreationListener<ItemId, ItemType> listener);
}
