package pt.ist.vaadinframework.data.hints;

import java.util.Arrays;
import java.util.Collection;

import pt.ist.vaadinframework.data.HintedProperty.Hint;

import com.vaadin.data.Container;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Field;

public class Options implements Hint {
	private final Container container;
	private int captionMode = AbstractSelect.ITEM_CAPTION_MODE_EXPLICIT_DEFAULTS_ID;
	private Object captionPropertyId = null;

	public Options(Container container) {
		this.container = container;
	}

	public Options(Collection<?> options) {
		this(new IndexedContainer(options));
	}

	public Options(Object... options) {
		this(Arrays.asList(options));
	}

	/**
	 * Sets the item caption mode.
	 * 
	 * See also: {@link AbstractSelect#setItemCaptionMode(int) }
	 * 
	 * @param captionMode
	 *            one of the modes defined in {@link AbstractSelect}.
	 */
	public Options mode(int captionMode) {
		Options options = new Options(container);
		options.captionMode = captionMode;
		options.captionPropertyId = captionPropertyId;
		return options;
	}

	public Options captionPropertyId(Object captionPropertyId) {
		Options options = new Options(container);
		options.captionMode = captionMode;
		options.captionPropertyId = captionPropertyId;
		return options;

	}

	@Override
	public Field applyHint(Field field) {
		((AbstractSelect) field).setContainerDataSource(container);
		((AbstractSelect) field).setItemCaptionMode(captionMode);
		((AbstractSelect) field).setItemCaptionPropertyId(captionPropertyId);
		return field;
	}

	@Override
	public boolean appliesTo(Field field) {
		return field instanceof AbstractSelect;
	}
}
