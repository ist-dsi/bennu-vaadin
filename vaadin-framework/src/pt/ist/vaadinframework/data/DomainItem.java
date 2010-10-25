///*
// * Copyright 2010 Instituto Superior Tecnico
// * 
// *      https://fenix-ashes.ist.utl.pt/
// * 
// *   This file is part of the vaadin-framework.
// *
// *   The vaadin-framework Infrastructure is free software: you can 
// *   redistribute it and/or modify it under the terms of the GNU Lesser General 
// *   Public License as published by the Free Software Foundation, either version 
// *   3 of the License, or (at your option) any later version.*
// *
// *   vaadin-framework is distributed in the hope that it will be useful,
// *   but WITHOUT ANY WARRANTY; without even the implied warranty of
// *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// *   GNU Lesser General Public License for more details.
// *
// *   You should have received a copy of the GNU Lesser General Public License
// *   along with vaadin-framework. If not, see <http://www.gnu.org/licenses/>.
// * 
// */
//package pt.ist.vaadinframework.data;
//
//import java.beans.BeanInfo;
//import java.beans.IntrospectionException;
//import java.beans.Introspector;
//import java.beans.PropertyDescriptor;
//import java.lang.reflect.Method;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Map;
//import java.util.Set;
//
//import org.apache.log4j.Logger;
//
//import pt.ist.fenixframework.FenixFramework;
//import pt.ist.fenixframework.pstm.AbstractDomainObject;
//import pt.ist.vaadinframework.data.JPAContainerItem.ItemProperty;
//
//import com.vaadin.data.Buffered;
//import com.vaadin.data.Item;
//import com.vaadin.data.Validator.InvalidValueException;
//import com.vaadin.data.util.MethodProperty;
//import com.vaadin.data.util.PropertysetItem;
//
//import dml.DomainClass;
//import dml.DomainModel;
//import dml.Role;
//
///**
// * {@link Item} of properties created using a combination of reflection
// * techniques and domain model inspection.
// * 
// * @author Pedro Santos (pedro.miguel.santos@ist.utl.pt)
// * @param <Type>
// *            type of the object being proxied
// */
//@SuppressWarnings("serial")
//public class DomainItem<Type extends AbstractDomainObject> extends PropertysetItem implements Buffered {
//    protected static final Logger LOGGER = Logger.getLogger(DomainItem.class.getName());
//
//    protected static final Map<Class<? extends AbstractDomainObject>, BeanInfo> INSPECTIONS = new HashMap<Class<? extends AbstractDomainObject>, BeanInfo>();
//
//    private static final Set<String> ignoredProperties = new HashSet<String>();
//
//    static {
//	ignoredProperties.add("OID");
//	ignoredProperties.add("oid");
//	ignoredProperties.add("idInternal");
//	ignoredProperties.add("deleted");
//	ignoredProperties.add("root");
//    }
//
//    private final Type bean;
//
//    private boolean writeThrough = true;
//
//    /**
//     * <p>
//     * Creates a new instance of <code>BeanItem</code> and adds all properties
//     * of a Java Bean to it. The properties are identified by their respective
//     * bean names.
//     * </p>
//     * 
//     * <p>
//     * Note : This version only supports introspectable bean properties and
//     * their getter and setter methods. Stand-alone <code>is</code> and
//     * <code>are</code> methods are not supported.
//     * </p>
//     * 
//     * @param bean
//     *            the Java Bean to copy properties from.
//     * 
//     */
//    public DomainItem(Type bean) {
//	this.bean = bean;
//	inspectItemProperties(bean);
//    }
//
//    /**
//     * Gets the underlying JavaBean object.
//     * 
//     * @return the bean object.
//     */
//    public Type getBean() {
//	return bean;
//    }
//
//    private void inspectItemProperties(Type bean) {
//	try {
//	    if (!INSPECTIONS.containsKey(bean.getClass())) {
//		INSPECTIONS.put(bean.getClass(), Introspector.getBeanInfo(bean.getClass()));
//	    }
//	    BeanInfo info = INSPECTIONS.get(bean.getClass());
//
//	    DomainModel model = FenixFramework.getDomainModel();
//	    DomainClass domainClass = model.findClass(bean.getClass().getName());
//	    Set<String> roleNames = new HashSet<String>();
//	    for (Role roleSlot : domainClass.getRoleSlotsList()) {
//		String name = roleSlot.getName();
//		if (roleSlot.getMultiplicityUpper() != 1) {
//		    addItemProperty(name, new RelationProperty<Type>(bean, name));
//		    roleNames.add(name);
//		    roleNames.add(name + "Count");
//		    roleNames.add(name + "Iterator");
//		    roleNames.add(name + "Set");
//		}
//	    }
//
//	    final PropertyDescriptor[] pds = info.getPropertyDescriptors();
//	    for (PropertyDescriptor descriptor : pds) {
//		String name = descriptor.getName();
//		if (ignoredProperties.contains(name)) {
//		    continue;
//		}
//		if (roleNames.contains(name)) {
//		    continue;
//		}
//		Method getMethod = descriptor.getReadMethod();
//		Method setMethod = descriptor.getWriteMethod();
//		Class<?> type = descriptor.getPropertyType();
//		if ((getMethod != null) && getMethod.getDeclaringClass() != Object.class) {
//		    addItemProperty(name, new MethodProperty(type, bean, getMethod, setMethod));
//		}
//	    }
//	} catch (IntrospectionException e) {
//	    LOGGER.warn("object introspection failed for class: " + bean.getClass().getName(), e);
//	}
//    }
//
//    /*
//     * (non-Javadoc)
//     * 
//     * @see com.vaadin.data.Buffered#commit()
//     */
//    @Override
//    public void commit() throws SourceException, InvalidValueException {
//	// TODO Auto-generated method stub
//
//    }
//
//    /*
//     * (non-Javadoc)
//     * 
//     * @see com.vaadin.data.Buffered#discard()
//     */
//    @Override
//    public void discard() throws SourceException {
//	// TODO Auto-generated method stub
//
//    }
//
//    /*
//     * (non-Javadoc)
//     * 
//     * @see com.vaadin.data.Buffered#isWriteThrough()
//     */
//    @Override
//    public boolean isWriteThrough() {
//	return writeThrough;
//    }
//
//    /*
//     * (non-Javadoc)
//     * 
//     * @see com.vaadin.data.Buffered#setWriteThrough(boolean)
//     */
//    @Override
//    public void setWriteThrough(boolean writeThrough) throws SourceException, InvalidValueException {
//	if (this.writeThrough != writeThrough) {
//	    if (writeThrough) {
//		/*
//		 * According to the Buffered interface, commit must be executed
//		 * if writeThrough is turned on.
//		 */
//		commit();
//		/*
//		 * Do some cleaning up
//		 */
//		for (ItemProperty prop : propertyMap.values()) {
//		    prop.clearCache();
//		}
//	    } else {
//		/*
//		 * We can iterate directly over the map, as this operation only
//		 * affects existing properties. Properties that are lazily
//		 * created afterwards will work automatically.
//		 */
//		for (ItemProperty prop : propertyMap.values()) {
//		    prop.cacheRealValue();
//		}
//	    }
//	    this.writeThrough = writeThrough;
//	    /*
//	     * Normally, if writeThrough is changed, readThrough should also be
//	     * changed.
//	     */
//	    setReadThrough(writeThrough);
//	}
//    }
//
//    /*
//     * (non-Javadoc)
//     * 
//     * @see com.vaadin.data.Buffered#isReadThrough()
//     */
//    @Override
//    public boolean isReadThrough() {
//	// TODO Auto-generated method stub
//	return false;
//    }
//
//    /*
//     * (non-Javadoc)
//     * 
//     * @see com.vaadin.data.Buffered#setReadThrough(boolean)
//     */
//    @Override
//    public void setReadThrough(boolean readThrough) throws SourceException {
//	// TODO Auto-generated method stub
//
//    }
//
//    /*
//     * (non-Javadoc)
//     * 
//     * @see com.vaadin.data.Buffered#isModified()
//     */
//    @Override
//    public boolean isModified() {
//	// TODO Auto-generated method stub
//	return false;
//    }
// }
