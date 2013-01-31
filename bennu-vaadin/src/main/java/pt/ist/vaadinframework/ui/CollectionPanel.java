package pt.ist.vaadinframework.ui;

import java.lang.reflect.Method;

import com.vaadin.data.Buffered;
import com.vaadin.data.Container;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;

/**
 * Creates a panel that view and manage a collection of items.
 * 
 * @author David Martinho (davidmartinho@ist.utl.pt)
 */
public abstract class CollectionPanel extends CustomComponent implements Container.Viewer, Container.Editor,
		Container.ItemSetChangeListener, Container.ItemSetChangeNotifier {

	private static final long serialVersionUID = -8342680039386201881L;

	private static final String CSS_COLLECTION_PANEL = "v-collectionpanel";
	private static final String CSS_COLLECTION_LABEL = "v-collectionpanel-label";
	private static final String CSS_COLLECTION_ADD_BUTTON = "v-collectionpanel-add-button";
	private static final String CSS_COLLECTION_REMOVE_BUTTON = "v-collectionpanel-remove-button";
	private static final String CSS_COLLECTION_CONTAINER = "v-collectionpanel-container";

	private GridLayout container;

	private boolean isRemovable = false;

	private Button addButton;
	private Button removeButton;
	private Label nameLabel;
	private Panel itemLayoutContainer;

	private Container itemContainer;

	public CollectionPanel(boolean isRemovable) {
		this.isRemovable = isRemovable;
		bindUi();
	}

	private void bindUi() {
		container = new GridLayout(3, 3);
		container.addStyleName(CSS_COLLECTION_PANEL);
		container.setSizeFull();
		bindAddButton();
		bindRemoveButton();
		bindCollectionNameLabel();
		bindContainer();
		setCompositionRoot(container);
	}

	private void bindAddButton() {
		addButton = new Button("+");
		addButton.addStyleName(BaseTheme.BUTTON_LINK);
		addButton.addStyleName(CSS_COLLECTION_ADD_BUTTON);
		addButton.setSizeUndefined();
		container.addComponent(addButton, 0, 1, 0, 1);
		container.setComponentAlignment(addButton, Alignment.TOP_RIGHT);
	}

	private void bindRemoveButton() {
		if (isRemovable) {
			removeButton = new Button("X");
			removeButton.addStyleName(BaseTheme.BUTTON_LINK);
			removeButton.addStyleName(CSS_COLLECTION_REMOVE_BUTTON);
			removeButton.setSizeUndefined();
			container.addComponent(removeButton, 2, 0, 2, 0);
		}
	}

	private void bindCollectionNameLabel() {
		nameLabel = new Label();
		nameLabel.addStyleName(CSS_COLLECTION_LABEL);
		nameLabel.setSizeUndefined();
		container.addComponent(nameLabel, 1, 0, 1, 0);
		container.setComponentAlignment(nameLabel, Alignment.BOTTOM_LEFT);
	}

	private void bindContainer() {
		itemLayoutContainer = new Panel();
		VerticalLayout wrapper = new VerticalLayout();
		wrapper.setMargin(true);
		wrapper.setSpacing(true);
		itemLayoutContainer.setContent(wrapper);
		itemLayoutContainer.addStyleName(CSS_COLLECTION_CONTAINER);
		container.addComponent(itemLayoutContainer, 1, 1, 2, 2);
		container.setColumnExpandRatio(1, 1f);
	}

	public Label getNameLabel() {
		return nameLabel;
	}

	public Button getAddButton() {
		return addButton;
	}

	public Button getRemoveButton() {
		return removeButton;
	}

	public Panel getItemLayoutContainer() {
		return itemLayoutContainer;
	}

	public void setWriteThrough(boolean state) {
		if (itemContainer != null) {
			((Buffered) itemContainer).setWriteThrough(state);
		}
	}

	@Override
	public void setContainerDataSource(Container newDataSource) {
		if (itemContainer != newDataSource) {
			if (itemContainer != null) {
				if (itemContainer instanceof Container.ItemSetChangeNotifier) {
					((Container.ItemSetChangeNotifier) itemContainer).removeListener(this);
				}
			}

			// Assigns new data source
			itemContainer = newDataSource;
			refreshComponents(itemContainer);

			// Adds listeners
			if (itemContainer != null) {
				if (itemContainer instanceof Container.ItemSetChangeNotifier) {
					((Container.ItemSetChangeNotifier) itemContainer).addListener(this);
				}
			}
		}
	}

	public void refreshComponents() {
		refreshComponents(getContainerDataSource());
	}

	protected abstract void refreshComponents(Container itemContainer);

	@Override
	public Container getContainerDataSource() {
		return itemContainer;
	}

	public void setNameLabel(String newValue) {
		this.nameLabel.setValue(newValue);
	}

	@Override
	public void containerItemSetChange(com.vaadin.data.Container.ItemSetChangeEvent event) {
		fireItemSetChangeEvent();
	}

	private void fireItemSetChangeEvent() {
		fireEvent(new CollectionPanel.ItemSetChangeEvent(this));
		requestRepaint();
	}

	public class ItemSetChangeEvent extends Component.Event implements Container.ItemSetChangeEvent {

		private static final long serialVersionUID = -6054196818392240334L;

		public ItemSetChangeEvent(CollectionPanel source) {
			super(source);
		}

		@Override
		public Container getContainer() {
			return (Container) getSource();
		}
	}

	@Override
	public void addListener(Container.ItemSetChangeListener listener) {
		addListener(CollectionPanel.ItemSetChangeEvent.class, listener, ITEM_SET_CHANGE_METHOD);
	}

	@Override
	public void removeListener(Container.ItemSetChangeListener listener) {
		removeListener(CollectionPanel.ItemSetChangeEvent.class, listener, ITEM_SET_CHANGE_METHOD);
	}

	private static final Method ITEM_SET_CHANGE_METHOD;

	static {
		try {
			ITEM_SET_CHANGE_METHOD =
					Container.ItemSetChangeListener.class.getDeclaredMethod("containerItemSetChange",
							new Class[] { Container.ItemSetChangeEvent.class });
		} catch (final java.lang.NoSuchMethodException e) {
			// This should never happen
			throw new java.lang.RuntimeException("Internal error finding methods in CollectionPanel");
		}
	}
}
