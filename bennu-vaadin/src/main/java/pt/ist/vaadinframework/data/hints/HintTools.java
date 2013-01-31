package pt.ist.vaadinframework.data.hints;

import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Field;

class HintTools {
	public static void copyConfiguration(Field from, Field to) {
		to.setStyleName(from.getStyleName());
		to.setEnabled(from.isEnabled());
		to.setVisible(from.isVisible());
		to.setReadOnly(from.isReadOnly());
		to.setCaption(from.getCaption());
		to.setIcon(from.getIcon());
		to.setDescription(from.getDescription());
		to.setHeight(from.getHeight(), from.getHeightUnits());
		to.setWidth(from.getWidth(), from.getWidthUnits());

		to.setRequired(from.isRequired());
		to.setRequiredError(from.getRequiredError());
		to.setInvalidAllowed(from.isInvalidAllowed());
		to.setInvalidCommitted(from.isInvalidCommitted());
		to.setReadThrough(from.isReadThrough());
		to.setWriteThrough(from.isWriteThrough());

		if (from instanceof AbstractField && to instanceof AbstractField) {
			((AbstractField) to).setImmediate(((AbstractField) from).isImmediate());
			((AbstractField) to).setLocale(((AbstractField) from).getLocale());
		}

		if (from instanceof AbstractSelect && to instanceof AbstractSelect) {
			((AbstractSelect) to).setContainerDataSource(((AbstractSelect) from).getContainerDataSource());
			((AbstractSelect) to).setNewItemsAllowed(((AbstractSelect) from).isNewItemsAllowed());
			((AbstractSelect) to).setNullSelectionAllowed(((AbstractSelect) from).isNullSelectionAllowed());
			((AbstractSelect) to).setNullSelectionAllowed(((AbstractSelect) from).isNullSelectionAllowed());
		}

		if (from instanceof AbstractTextField && to instanceof AbstractTextField) {
			((AbstractTextField) to).setColumns(((AbstractTextField) from).getColumns());
			((AbstractTextField) to).setInputPrompt(((AbstractTextField) from).getInputPrompt());
			((AbstractTextField) to).setMaxLength(((AbstractTextField) from).getMaxLength());
			((AbstractTextField) to).setNullRepresentation(((AbstractTextField) from).getNullRepresentation());
			((AbstractTextField) to).setNullSettingAllowed(((AbstractTextField) from).isNullSettingAllowed());
			((AbstractTextField) to).setTextChangeEventMode(((AbstractTextField) from).getTextChangeEventMode());
			((AbstractTextField) to).setTextChangeTimeout(((AbstractTextField) from).getTextChangeTimeout());
		}

		if (from instanceof DateField && to instanceof DateField) {
			((DateField) to).setResolution(((DateField) from).getResolution());
		}
	}
}
