package module.vaadin.data.util;

public interface InstanceCreationListener<ItemId, ItemType> {
    public void itemCreation(ItemId itemId, ItemType item);
}
