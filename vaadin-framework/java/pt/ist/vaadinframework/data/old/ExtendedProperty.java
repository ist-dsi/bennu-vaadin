package pt.ist.vaadinframework.data.old;

import java.util.Set;

import com.vaadin.data.Property;

public interface ExtendedProperty extends Property {
    public static enum LayoutHint {
	LOW_SPACE, LOTS_OF_SPACE, DATE_RESOLUTION_MSEC, DATE_RESOLUTION_SEC, DATE_RESOLUTION_MIN, DATE_RESOLUTION_HOUR, DATE_RESOLUTION_DAY, DATE_RESOLUTION_MONTH, DATE_RESOLUTION_YEAR;
    }

    public boolean isRequired();

    public void setRequired(boolean required);

    public Set<LayoutHint> getLayoutHints();

    public void addLayoutHint(LayoutHint hint);

    public void removeLayoutHint(LayoutHint hint);
}
