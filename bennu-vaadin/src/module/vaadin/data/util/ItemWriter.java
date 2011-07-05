package module.vaadin.data.util;

import com.vaadin.data.Buffered.SourceException;

public interface ItemWriter<PropertyId, Type> {
    public PropertyId[] getOrderedArguments();

    public void write(Type object, Object[] arguments) throws SourceException;
}
