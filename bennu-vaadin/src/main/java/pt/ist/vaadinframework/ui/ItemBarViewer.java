package pt.ist.vaadinframework.ui;

import java.lang.reflect.Method;

import pt.ist.vaadinframework.ui.factory.ResourceFactory;

import com.vaadin.data.Item;
import com.vaadin.data.Item.PropertySetChangeListener;
import com.vaadin.data.Property;
import com.vaadin.terminal.Resource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.themes.BaseTheme;

/**
 * A DomainItem viewer that displays a domain item in a bar.
 * 
 * @author David Martinho (davidmartinho@ist.utl.pt)
 * 
 */
public abstract class ItemBarViewer extends CustomComponent implements Item.Viewer, Item.PropertySetChangeListener,
        Item.PropertySetChangeNotifier {

    private static final long serialVersionUID = -6263849010604509178L;

    private static final String AVATAR_SIZE = "30px";

    private static final String CSS_ITEMBAR = "v-itembar";
    private static final String CSS_ITEMBAR_WRAPPER = "v-itembar-wrapper";
    private static final String CSS_ITEMBAR_AVATAR = "v-itembar-avatar";
    private static final String CSS_ITEMBAR_NAME_LABEL = "v-itembar-name-label";
    private static final String CSS_ITEMBAR_REMOVE_BUTTON = "v-itembar-remove-button";

    private Panel container;
    private GridLayout itemBar;
    private Embedded avatar;
    private Label nameLabel;
    private Button removeButton;

    private Item itemDataSource;

    public ItemBarViewer() {
        bindUi();
    }

    private void bindUi() {
        container = new Panel();
        container.addStyleName(CSS_ITEMBAR);
        container.setSizeFull();
        bindItemBar();
        setCompositionRoot(container);
    }

    private void bindItemBar() {
        itemBar = new GridLayout(3, 1);
        itemBar.addStyleName(CSS_ITEMBAR_WRAPPER);
        container.setContent(itemBar);
        itemBar.setSizeFull();
        bindAvatar();
        bindNameLabel();
        bindRemoveButton();
    }

    private void bindAvatar() {
        avatar = new Embedded();
        avatar.setWidth(AVATAR_SIZE);
        avatar.setHeight(AVATAR_SIZE);
        avatar.addStyleName(CSS_ITEMBAR_AVATAR);
        itemBar.addComponent(avatar, 0, 0, 0, 0);
        itemBar.setComponentAlignment(avatar, Alignment.MIDDLE_LEFT);
    }

    private void bindNameLabel() {
        nameLabel = new Label();
        nameLabel.addStyleName(CSS_ITEMBAR_NAME_LABEL);
        itemBar.addComponent(nameLabel, 1, 0, 1, 0);
        itemBar.setComponentAlignment(nameLabel, Alignment.MIDDLE_LEFT);
    }

    private void bindRemoveButton() {
        removeButton = new Button("x");
        removeButton.addStyleName(BaseTheme.BUTTON_LINK);
        removeButton.addStyleName(CSS_ITEMBAR_REMOVE_BUTTON);
        itemBar.addComponent(removeButton, 2, 0, 2, 0);
        itemBar.setComponentAlignment(removeButton, Alignment.MIDDLE_RIGHT);
    }

    public Button getRemoveButton() {
        return removeButton;
    }

    @Override
    public void setItemDataSource(Item newDataSource) {
        if (itemDataSource != null && Item.PropertySetChangeNotifier.class.isAssignableFrom(itemDataSource.getClass())) {
            ((Item.PropertySetChangeNotifier) itemDataSource).removeListener(this);
        }

        this.itemDataSource = newDataSource;
        refreshComponent();

        if (itemDataSource != null && Item.PropertySetChangeNotifier.class.isAssignableFrom(itemDataSource.getClass())) {
            ((Item.PropertySetChangeNotifier) itemDataSource).addListener(this);
        }
        requestRepaint();
    }

    private void refreshComponent() {
        avatar.setSource(getAvatarResource());
        nameLabel.setPropertyDataSource(getNameProperty());
    }

    public abstract Property getNameProperty();

    /**
     * Override this method to control the avatar icon that is associated to the
     * item bar.
     * 
     * @return the avatar resource to be included in the item bar
     */
    protected Resource getAvatarResource() {
        return ResourceFactory.getAvatarFor(((Property) itemDataSource).getValue());
    }

    @Override
    public Item getItemDataSource() {
        return itemDataSource;
    }

    @Override
    public void itemPropertySetChange(Item.PropertySetChangeEvent event) {
        fireItemPropertySetChangeEvent();
    }

    private void fireItemPropertySetChangeEvent() {
        fireEvent(new ItemBarViewer.ItemPropertySetChangeEvent(this));
        requestRepaint();
    }

    @Override
    public void addListener(PropertySetChangeListener listener) {
        addListener(Item.PropertySetChangeListener.class, listener, ITEM_PROPERTY_SET_CHANGE_METHOD);
    }

    @Override
    public void removeListener(PropertySetChangeListener listener) {
        removeListener(Item.PropertySetChangeListener.class, listener, ITEM_PROPERTY_SET_CHANGE_METHOD);
    }

    public class ItemPropertySetChangeEvent extends Component.Event implements Item.PropertySetChangeEvent {

        private static final long serialVersionUID = -7389162648358822581L;

        public ItemPropertySetChangeEvent(ItemBarViewer itemBar) {
            super(itemBar);
        }

        @Override
        public Item getItem() {
            return (Item) getSource();
        }
    }

    private static final Method ITEM_PROPERTY_SET_CHANGE_METHOD;

    static {
        try {
            ITEM_PROPERTY_SET_CHANGE_METHOD =
                    Item.PropertySetChangeListener.class.getDeclaredMethod("itemPropertySetChange",
                            new Class[] { Item.PropertySetChangeEvent.class });
        } catch (final java.lang.NoSuchMethodException e) {
            throw new RuntimeException("Internal error finding methods in " + ItemBarViewer.class.getName());
        }
    }

}
