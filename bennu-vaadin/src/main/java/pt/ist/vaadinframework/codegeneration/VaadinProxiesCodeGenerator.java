/*
 * Copyright 2011 Instituto Superior Tecnico
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the vaadin-framework.
 *
 *   The vaadin-framework Infrastructure is free software: you can 
 *   redistribute it and/or modify it under the terms of the GNU Lesser General 
 *   Public License as published by the Free Software Foundation, either version 
 *   3 of the License, or (at your option) any later version.*
 *
 *   vaadin-framework is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with vaadin-framework. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package pt.ist.vaadinframework.codegeneration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import pt.ist.vaadinframework.data.AbstractBufferedContainer;
import pt.ist.vaadinframework.data.AbstractBufferedItem;
import pt.ist.vaadinframework.data.BufferedProperty;
import pt.ist.vaadinframework.data.HintedProperty.Hint;
import pt.ist.vaadinframework.data.PropertyId;
import pt.ist.vaadinframework.data.hints.Required;

import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JConditional;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;
import com.sun.codemodel.JTypeVar;
import com.sun.codemodel.JVar;
import com.sun.codemodel.writer.FileCodeWriter;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ConversionException;
import com.vaadin.data.Property.ReadOnlyException;
import com.vaadin.data.util.AbstractProperty;

import dml.DomainClass;
import dml.DomainEntity;
import dml.DomainModel;
import dml.EnumValueType;
import dml.ParamValueType;
import dml.Role;
import dml.Slot;
import dml.Slot.Option;
import dml.ValueType;

/**
 * @author Pedro Santos (pedro.miguel.santos@ist.utl.pt)
 * @author Sérgio Silva (sergio.silva@ist.utl.pt)
 */
public class VaadinProxiesCodeGenerator {
    private final DomainModel model;

    private final File srcBaseDir;

    private final Map<DomainEntity, JDefinedClass> itemBaseMap = new HashMap<DomainEntity, JDefinedClass>();

    private final Map<DomainEntity, JDefinedClass> itemMap = new HashMap<DomainEntity, JDefinedClass>();

    private final Map<JDefinedClass, JDefinedClass> propertyIdMap = new HashMap<JDefinedClass, JDefinedClass>();

    private final Map<JCodeModel, Map<String, JClass>> refMap = new HashMap<JCodeModel, Map<String, JClass>>();

    private final Map<DomainEntity, JDefinedClass> containerBaseMap = new HashMap<DomainEntity, JDefinedClass>();

    private final Map<DomainEntity, JDefinedClass> containerMap = new HashMap<DomainEntity, JDefinedClass>();

    private static final Map<String, File> packageMapper = new HashMap<String, File>();

    public VaadinProxiesCodeGenerator(DomainModel model, File srcBaseDir, String vaadinSrcDir, File packageSourceLocations) {
        this.model = model;
        this.srcBaseDir = srcBaseDir;

        try {
            final String contents = FileUtils.readFileToString(packageSourceLocations);
            for (final String line : contents.split("\n")) {
                final int sindex = line.indexOf(' ');
                final String packageName = line.substring(0, sindex);
                String srcDir = line.substring(sindex + 1);
                srcDir = srcDir.substring(0, srcDir.lastIndexOf(File.separatorChar)) + File.separatorChar + vaadinSrcDir;
                final File file = new File(srcDir);
                packageMapper.put(packageName, file);
            }
        } catch (IOException e) {
        }
    }

    public void generate() throws IOException, JClassAlreadyExistsException {
        JCodeModel srcgen = new JCodeModel();
        Map<File, JCodeModel> localsrcs = new HashMap<File, JCodeModel>();
        for (DomainClass clazz : model.getDomainClasses()) {
            registerClass(clazz, srcgen, localsrcs);
        }
        for (DomainClass clazz : model.getDomainClasses()) {
            if (clazz.hasSuperclass()) {
                registerHierarchy(clazz, clazz.getSuperclass());
            }
        }
        for (Entry<DomainEntity, JDefinedClass> entry : itemBaseMap.entrySet()) {
            DomainEntity clazz = entry.getKey();
            JDefinedClass proxy = entry.getValue();
            fillBaseItem(proxy, getHostType(proxy, clazz), clazz);
        }
        for (Entry<DomainEntity, JDefinedClass> entry : itemMap.entrySet()) {
            JDefinedClass proxy = entry.getValue();
            fillItem(proxy, getHostType(proxy, entry.getKey()));
        }
        iterateThroughContainers(new HashSet<DomainEntity>(containerBaseMap.keySet()), (DomainClass) containerBaseMap.keySet()
                .iterator().next());
        for (Entry<DomainEntity, JDefinedClass> entry : containerMap.entrySet()) {
            DomainEntity clazz = entry.getKey();
            JDefinedClass proxy = entry.getValue();
            fillContainer(proxy, (DomainClass) clazz);
        }
        CodeWriter src = new FileCodeWriter(srcBaseDir, true);
        CodeWriter res = new FileCodeWriter(srcBaseDir, true);
        srcgen.build(src, res);

        for (File packageDir : localsrcs.keySet()) {
            packageDir.mkdirs();
            src = new NonOverridingCodeWriter(new FileCodeWriter(packageDir), packageDir);
            res = new NonOverridingCodeWriter(new FileCodeWriter(packageDir), packageDir);
            localsrcs.get(packageDir).build(src, res);
        }
    }

    private void iterateThroughContainers(Set<DomainEntity> entities, DomainClass clazz) {
        if (entities.contains(clazz)) {
            if (clazz.hasSuperclass() && entities.contains(clazz.getSuperclass())) {
                iterateThroughContainers(entities, (DomainClass) clazz.getSuperclass());
            } else {
                JDefinedClass proxy = containerBaseMap.get(clazz);
                fillBaseContainer(proxy, getLowestItemFor(clazz), clazz);
                entities.remove(clazz);
            }
        }
        if (entities.isEmpty()) {
            return;
        }
        iterateThroughContainers(entities, (DomainClass) entities.iterator().next());
    }

    /**
     * Initialization of types
     * 
     * @param localsrcs
     * @param srcgen
     * @throws JClassAlreadyExistsException
     */
    private void registerClass(DomainClass clazz, JCodeModel srcgen, Map<File, JCodeModel> localsrcs)
            throws JClassAlreadyExistsException {
        JClass propertyIdType = srcgen.ref(PropertyId.class);
        JClass classref = ref(srcgen, clazz);

        JDefinedClass baseItem = srcgen._class(getProxyFullName(clazz, getItemSuffix(clazz)));
        JTypeVar genericItemType = baseItem.generify("Type", classref);
        baseItem._extends(srcgen.ref(AbstractBufferedItem.class).narrow(propertyIdType, genericItemType));
        itemBaseMap.put(clazz, baseItem);

        JDefinedClass propertyId = generatePropertyIdClass(baseItem, clazz);
        propertyIdMap.put(baseItem, propertyId);

        File packageDir = getPackageDir(clazz.getPackageName());
        if (packageDir != null) {
            if (!localsrcs.containsKey(packageDir)) {
                localsrcs.put(packageDir, new JCodeModel());
            }
            JDefinedClass item = localsrcs.get(packageDir)._class(getProxyFullName(clazz, "Item"));
            item._extends(baseItem.narrow(ref(item.owner(), clazz)));
            itemMap.put(clazz, item);
        }

        JDefinedClass baseContainer = srcgen._class(getProxyFullName(clazz, getContainerSuffix(clazz)));
        containerBaseMap.put(clazz, baseContainer);

        if (packageDir != null) {
            JDefinedClass container = localsrcs.get(packageDir)._class(getProxyFullName(clazz, "Container"));
            container._extends(baseContainer);
            containerMap.put(clazz, container);
        }
    }

    private void registerHierarchy(DomainClass type, DomainEntity supertype) {
        // override between ChildItem_Base and its super SuperItem or
        // SuperItem_Base (the latter one in case the super type does not have a
        // non base item)
        JDefinedClass item = getBaseItem(type);
        JDefinedClass superitem = getLowestItemFor(supertype);
        item._extends(superitem.narrow(item.typeParams()[0]));

        // override between SuperItem and SuperItem_Base for classes that are
        // not leaves (super of something in this case)
        if (!getBaseItem(supertype).equals(superitem)) {
            if (superitem.typeParams().length == 0) {
                superitem.generify("Type", ref(superitem.owner(), supertype));
            }
            superitem._extends(getBaseItem(supertype).narrow(superitem.typeParams()[0]));
        }

        propertyIdMap.get(item)._extends(propertyIdMap.get(getBaseItem(supertype)));
    }

    private void fillBaseItem(JDefinedClass item, JClass itemType, DomainEntity clazz) throws JClassAlreadyExistsException {
        generateConstructorForWrapping(item);
        generateConstructorForType(item, itemType);
        generateConstructorForInstance(item, itemType);
        generateConstructorForInstanceWithForcedType(item, itemType);

        Map<String, JDefinedClass> propertyClasses = new HashMap<String, JDefinedClass>();
        for (Slot slot : ((DomainClass) clazz).getSlotsList()) {
            generateSlotPropertyId(propertyIdMap.get(item), slot.getName());
            propertyClasses.put(slot.getName(),
                    generateSingleValueProperty(itemType, item, slot.getName(), getSlotType(item.owner(), clazz, slot)));
            generateDirectPropertyGetter(item, slot);
        }
        for (Role role : ((DomainClass) clazz).getRoleSlotsList()) {
            if (role.getName() != null && !role.getType().getName().startsWith("Remote")) {
                generateRolePropertyId(propertyIdMap.get(item), role.getName(), propertyIdMap.get(getBaseItem(role.getType())));
                if (role.getMultiplicityUpper() == 1) {
                    propertyClasses.put(role.getName(),
                            generateSingleValueProperty(itemType, item, role.getName(), ref(item.owner(), role.getType())));
                    generateItemPropertyGetter(item, role);
                } else {
                    propertyClasses.put(role.getName(),
                            generateCollectionValueProperty(itemType, item, role.getName(), ref(item.owner(), role.getType())));
                    generateContainerPropertyGetter(item, role);
                }
            }
        }
        generateMakePropertyMethod(item, (DomainClass) clazz, propertyClasses);
    }

    /**
     * <code>
     * public Property get_property_Property() {
     *     return getItemProperty(_propertytype_PropertyId._property_);
     * }
     * </code>
     */
    private void generateDirectPropertyGetter(JDefinedClass item, Slot slot) {
        JMethod getProperty =
                item.method(JMod.PUBLIC, Property.class, "get" + StringUtils.capitalize(slot.getName()) + "Property");
        getProperty.body()._return(JExpr.invoke("getItemProperty").arg(propertyIdMap.get(item).staticRef(slot.getName())));
    }

    private void generateItemPropertyGetter(JDefinedClass item, Role role) {
        JType resultType = getLowestItemFor(role.getType());
        JMethod getProperty = item.method(JMod.PUBLIC, resultType, "get" + StringUtils.capitalize(role.getName()) + "Property");
        getProperty.body()._return(
                JExpr.cast(resultType, JExpr.invoke("getItemProperty").arg(propertyIdMap.get(item).staticRef(role.getName()))));
    }

    private void generateContainerPropertyGetter(JDefinedClass item, Role role) {
        JType resultType = getLowestContainerFor(role.getType());
        JMethod getProperty = item.method(JMod.PUBLIC, resultType, "get" + StringUtils.capitalize(role.getName()) + "Property");
        getProperty.body()._return(
                JExpr.cast(resultType, JExpr.invoke("getItemProperty").arg(propertyIdMap.get(item).staticRef(role.getName()))));
    }

    private void fillItem(JDefinedClass item, JClass itemType) {
        generateConstructorForWrapping(item);
        generateConstructorForType(item, itemType);
        generateConstructorForInstance(item, itemType);
        generateConstructorForInstanceWithForcedType(item, itemType);
    }

    private void fillBaseContainer(JDefinedClass container, JClass item, DomainClass containerType) {
        JClass classref = ref(container.owner(), containerType);
        JClass propertyIdType = container.owner().ref(PropertyId.class);

        container._extends(container.owner().ref(AbstractBufferedContainer.class)
                .narrow(classref, propertyIdType, narrowToIfNeeded(item, classref, true)));

        generateConstructorForWrappingCollections(container, classref);
        generateConstructorForType(container, classref);
        generateConstructorForCollectionInstance(container, classref);

        generateMakeItemMethods(container, item, classref);

        DomainClass current = containerType;
        while (current.hasSuperclass()) {
            current = (DomainClass) current.getSuperclass();
            JDefinedClass currentContainer = getBaseContainer(current);
            JClass currentRef = ref(currentContainer.owner(), current);

            insertSpecificMakerIfClauseForInstance(currentContainer, item, currentRef, classref);
            insertSpecificMakerIfClauseForType(currentContainer, item, currentRef, classref);
            generateItemCreateShortcut(currentContainer, item, classref);
        }
    }

    private void fillContainer(JDefinedClass container, DomainClass containerType) {
        JClass classref = ref(container.owner(), containerType);

        generateConstructorForWrappingCollections(container, classref);
        generateConstructorForType(container, classref);
        generateConstructorForCollectionInstance(container, classref);
    }

    /**
     * <code>
     * public static class _clazz_PropertyId {
     * }
     * </code>
     */
    private JDefinedClass generatePropertyIdClass(JDefinedClass item, DomainClass clazz) throws JClassAlreadyExistsException {
        JDefinedClass propertyId = item._class(JMod.PUBLIC | JMod.STATIC, clazz.getName() + "PropertyId");
        propertyId._extends(PropertyId.class);

        // generateConstructor(propertyId);
        generateConstructorWithPiece(propertyId);

        return propertyId;
    }

    /**
     * <code>
     * public class _property_Property extends AbstractProperty {
     *     _Override
     *     public _propertytype_ getValue() {
     *         _parent_ host = _parent_Item_Base.this.getValue();
     *         return host != null ? host.get_property_() : null;
     *     }
     *     
     *     _Override
     *     public void setValue(Object newValue) throws ReadOnlyException, ConversionException {
     *         _parent_Item_Base.this.getValue().set_property_((_propertytype_) newValue);
     *     }
     *     
     *     _Override
     *     public Class<?> getType() {
     *         return _propertytype_.class;
     *     }
     * }
     * </code>
     */
    private JDefinedClass generateSingleValueProperty(JClass parent, JDefinedClass item, String property, JClass slotType)
            throws JClassAlreadyExistsException {
        JDefinedClass propClass = item._class(JMod.PUBLIC, StringUtils.capitalize(property) + "Property");
        propClass._extends(AbstractProperty.class);

        // getValue()
        JMethod getValue = propClass.method(JMod.PUBLIC, slotType, "getValue");
        getValue.annotate(Override.class);
        JVar host = getValue.body().decl(parent, "host", item.staticRef("this").invoke("getValue"));
        JConditional ifHost = getValue.body()._if(host.ne(JExpr._null()));
        ifHost._then()._return(host.invoke("get" + StringUtils.capitalize(property)));
        getValue.body()._return(JExpr._null());

        // setValue()
        JMethod setValue = propClass.method(JMod.PUBLIC, item.owner().VOID, "setValue");
        setValue.annotate(Override.class);
        setValue._throws(ReadOnlyException.class);
        setValue._throws(ConversionException.class);
        JVar newValue = setValue.param(Object.class, "newValue");
        setValue.body().add(
                item.staticRef("this").invoke("getValue").invoke("set" + StringUtils.capitalize(property))
                        .arg(JExpr.cast(slotType, newValue)));

        // getType()
        JMethod getType =
                propClass.method(JMod.PUBLIC, item.owner().ref(Class.class).narrow(item.owner().ref(Object.class).wildcard()),
                        "getType");
        getType.annotate(Override.class);
        getType.body()._return(JExpr.dotclass(slotType));

        return propClass;
    }

    /**
     * <code>
     * public class _property_Property extends AbstractProperty {
     *     _Override
     *     public Set<_propertytype_> getValue() {
     *         _parent_ host = _parent_Item_Base.this.getValue();
     *         return host != null ? host.get_property_Set() : null;
     *     }
     *     
     *     _Override
     *     public void setValue(Object newValue) throws ReadOnlyException, ConversionException {
     *         Set<_propertytype_> set = _parent_Item_Base.this.getValue().get_property_Set();
     *         set.clear();
     *         set.addAll((Set<_propertyType>) newValue);
     *     }
     *     
     *     _Override
     *     public Class<?> getType() {
     *         return Set.class;
     *     }
     * }
     * </code>
     */
    private JDefinedClass generateCollectionValueProperty(JClass parent, JDefinedClass item, String property, JClass propertyType)
            throws JClassAlreadyExistsException {
        JDefinedClass propClass = item._class(JMod.PUBLIC, StringUtils.capitalize(property) + "Property");
        propClass._extends(AbstractProperty.class);

        JClass setOfType = item.owner().ref(Set.class).narrow(propertyType);

        // getValue()
        JMethod getValue = propClass.method(JMod.PUBLIC, setOfType, "getValue");
        getValue.annotate(Override.class);
        JVar host = getValue.body().decl(parent, "host", item.staticRef("this").invoke("getValue"));
        JConditional ifHost = getValue.body()._if(host.ne(JExpr._null()));
        ifHost._then()._return(host.invoke("get" + StringUtils.capitalize(property) + "Set"));
        getValue.body()._return(JExpr._null());

        // setValue()
        JMethod setValue = propClass.method(JMod.PUBLIC, item.owner().VOID, "setValue");
        setValue.annotate(Override.class);
        setValue._throws(ReadOnlyException.class);
        setValue._throws(ConversionException.class);
        JVar newValue = setValue.param(Object.class, "newValue");
        JVar set =
                setValue.body().decl(setOfType, "set",
                        item.staticRef("this").invoke("getValue").invoke("get" + StringUtils.capitalize(property) + "Set"));
        setValue.body().add(set.invoke("clear"));
        setValue.body().add(set.invoke("addAll").arg(JExpr.cast(setOfType, newValue)));

        // getType()
        JMethod getType =
                propClass.method(JMod.PUBLIC, item.owner().ref(Class.class).narrow(item.owner().ref(Object.class).wildcard()),
                        "getType");
        getType.annotate(Override.class);
        getType.body()._return(JExpr.dotclass(item.owner().ref(Set.class)));

        return propClass;
    }

    /**
     * <code>
     * public _type_(String piece) {
     *     super(piece);
     * }
     * </code>
     */
    private void generateConstructorWithPiece(JDefinedClass type) {
        JMethod constructor = type.constructor(JMod.PUBLIC);
        final JVar piece = constructor.param(String.class, "piece");
        constructor.body().invoke("super").arg(piece);
    }

    /**
     * <code>
     * public _proxy_(Property wrapped, Hint... hints) {
     *     super(wrapped, hints);
     * }
     * </code>
     */
    private void generateConstructorForWrapping(JDefinedClass proxy) {
        JMethod constructor = proxy.constructor(JMod.PUBLIC);
        JVar wrapped = constructor.param(Property.class, "wrapped");
        JVar hints = constructor.varParam(Hint.class, "hints");
        constructor.body().invoke("super").arg(wrapped).arg(hints);
    }

    /**
     * <code>
     * public _proxy_(Property wrapped, Class<? extends _proxyType_> elementType, Hint... hints) {
     *     super(wrapped, elementType, hints);
     * }
     * </code>
     */
    private void generateConstructorForWrappingCollections(JDefinedClass proxy, JClass proxyType) {
        JMethod constructor = proxy.constructor(JMod.PUBLIC);
        JVar wrapped = constructor.param(Property.class, "wrapped");
        JVar elementType = constructor.param(proxy.owner().ref(Class.class).narrow(proxyType.wildcard()), "elementType");
        JVar hints = constructor.varParam(Hint.class, "hints");
        constructor.body().invoke("super").arg(wrapped).arg(elementType).arg(hints);
    }

    /**
     * <code>
     * public _proxy_(Class<? extends _proxyType_> type, Hint... hints) {
     *     super(type, hints);
     * }
     * </code>
     */
    private void generateConstructorForType(JDefinedClass proxy, JClass proxyType) {
        JMethod constructor = proxy.constructor(JMod.PUBLIC);
        JVar type = constructor.param(proxy.owner().ref(Class.class).narrow(proxyType.wildcard()), "type");
        JVar hints = constructor.varParam(Hint.class, "hints");
        constructor.body().invoke("super").arg(type).arg(hints);
    }

    /**
     * <code>
     * public _proxy_(_proxyType_ value, Hint... hints) {
     *     super(value, hints);
     * }
     * </code>
     */
    private void generateConstructorForInstance(JDefinedClass proxy, JClass proxyType) {
        JMethod constructor = proxy.constructor(JMod.PUBLIC);
        JVar value = constructor.param(proxyType, "value");
        JVar hints = constructor.varParam(Hint.class, "hints");
        constructor.body().invoke("super").arg(value).arg(hints);
    }

    /**
     * <code>
     * public _proxy_(List<_proxyType_> elements, Class<? extends _proxyType_> elementType, Hint... hints) {
     *     super(elements, elementType, hints);
     * }
     * 
     * </code>
     */
    private void generateConstructorForCollectionInstance(JDefinedClass proxy, JClass proxyType) {
        JMethod constructor = proxy.constructor(JMod.PUBLIC);
        JVar elements = constructor.param(proxy.owner().ref(List.class).narrow(proxyType), "elements");
        JVar elementType = constructor.param(proxy.owner().ref(Class.class).narrow(proxyType.wildcard()), "elementType");
        JVar hints = constructor.varParam(Hint.class, "hints");
        constructor.body().invoke("super").arg(elements).arg(elementType).arg(hints);
    }

    /**
     * <code>
     * public _proxy_(_proxyType_ value, Class<? extends _proxyType_> type, Hint... hints) {
     *     super(value, type, hints);
     * }
     * </code>
     */
    private void generateConstructorForInstanceWithForcedType(JDefinedClass proxy, JClass proxyType) {
        JMethod constructor = proxy.constructor(JMod.PUBLIC);
        JVar value = constructor.param(proxyType, "value");
        JVar type = constructor.param(proxy.owner().ref(Class.class).narrow(proxyType.wildcard()), "type");
        JVar hints = constructor.varParam(Hint.class, "hints");
        constructor.body().invoke("super").arg(value).arg(type).arg(hints);
    }

    /**
     * <code>
     * public final static PropertyId type = new PropertyId("_slot_");
     * </code>
     */
    private JFieldVar generateSlotPropertyId(JDefinedClass propertyIdClass, String slot) {
        JClass propertyId = propertyIdClass.owner().ref(PropertyId.class);
        return propertyIdClass.field(JMod.PUBLIC | JMod.FINAL | JMod.STATIC, propertyId, slot, JExpr._new(propertyId).arg(slot));
    }

    /**
     * <code>
     * public final static _rolePropertyIdType_ _role_() {
     *     return new _rolePropertyIdType_("_role_");
     * }
     * 
     * public final static _rolePropertyIdType_ _role_ = _role_();
     * </code>
     */
    private JFieldVar generateRolePropertyId(JDefinedClass propertyIdClass, String role, JType rolePropertyIdType) {
        JMethod getter = propertyIdClass.method(JMod.PUBLIC | JMod.FINAL | JMod.STATIC, rolePropertyIdType, role);
        getter.body()._return(JExpr._new(rolePropertyIdType).arg(role));

        return propertyIdClass.field(JMod.PUBLIC | JMod.FINAL | JMod.STATIC, rolePropertyIdType, role, JExpr.invoke(getter));
    }

    /**
     * <code>
     * _Override
     * protected Property makeProperty(PropertyId propertyId) {
     *     return null;
     * } </code>
     */
    private void generateMakePropertyMethod(JDefinedClass item, DomainClass clazz, Map<String, JDefinedClass> propertyClasses) {
        JCodeModel codegen = item.owner();

        JMethod makeProperty = item.method(JMod.PROTECTED, Property.class, "makeProperty");
        makeProperty.annotate(Override.class);
        JVar propertyId = makeProperty.param(PropertyId.class, "propertyId");
        for (Slot slot : clazz.getSlotsList()) {
            JConditional iff =
                    makeProperty.body()._if(propertyIdMap.get(item).staticRef(slot.getName()).invoke("equals").arg(propertyId));
            JInvocation invoke = JExpr._new(codegen.ref(BufferedProperty.class).narrow(getSlotType(codegen, clazz, slot)));
            invoke.arg(JExpr._new(propertyClasses.get(slot.getName())));
            if (slot.getOptions().contains(Option.REQUIRED)) {
                invoke.arg(JExpr._new(codegen.ref(Required.class)));
            }
            iff._then()._return(invoke);
        }
        for (Role role : clazz.getRoleSlotsList()) {
            if (role.getName() != null && !role.getType().getName().startsWith("Remote")) {
                JConditional iff =
                        makeProperty.body()._if(
                                propertyIdMap.get(item).staticRef(role.getName()).invoke("equals").arg(propertyId));
                JInvocation invoke;
                if (role.getMultiplicityUpper() == 1) {
                    if (itemMap.containsKey(role.getType())) {
                        invoke = JExpr._new(itemMap.get(role.getType()));
                    } else {
                        invoke = JExpr._new(itemBaseMap.get(role.getType()));
                    }
                    invoke.arg(JExpr._new(propertyClasses.get(role.getName())));
                } else {
                    if (containerMap.containsKey(role.getType())) {
                        invoke = JExpr._new(containerMap.get(role.getType()));
                    } else {
                        invoke = JExpr._new(containerBaseMap.get(role.getType()));
                    }
                    invoke.arg(JExpr._new(propertyClasses.get(role.getName()))).arg(ref(codegen, role.getType()).dotclass());
                }
                if (role.getMultiplicityLower() == 1) {
                    invoke.arg(JExpr._new(codegen.ref(Required.class)));
                }
                iff._then()._return(invoke);
            }
        }
        if (clazz.hasSuperclass()) {
            makeProperty.body()._return(JExpr._super().invoke("makeProperty").arg(propertyId));
        } else {
            makeProperty.body()._return(JExpr._null());
        }
    }

    /**
     * <code>
     * _Override
     * protected _clazz_Item_Base makeItem(_clazz_ itemId) {
     *     return new _clazz_Item_Base(itemId);
     * }
     * 
     * _Override
     * protected _clazz_Item_Base makeItem(Class<? extends _clazz_> type) {
     *     return new _clazz_Item_Base(type);
     * }
     * </code>
     */
    private void generateMakeItemMethods(JDefinedClass container, JClass item, JClass containerType) {
        JMethod makeByInstance = container.method(JMod.PUBLIC, narrowToIfNeeded(item, containerType, true), "makeItem");
        makeByInstance.annotate(Override.class);
        JVar itemId = makeByInstance.param(containerType, "itemId");
        makeByInstance.body()._return(JExpr._new(narrowToIfNeeded(item, containerType)).arg(itemId));

        JMethod makeByType = container.method(JMod.PUBLIC, narrowToIfNeeded(item, containerType, true), "makeItem");
        makeByType.annotate(Override.class);
        JVar type = makeByType.param(container.owner().ref(Class.class).narrow(containerType.wildcard()), "type");
        makeByType.body()._return(JExpr._new(narrowToIfNeeded(item, containerType)).arg(type));
    }

    /**
     * Insert into a method:
     * 
     * <code>
     * public Item makeItem(_containerType_ itemId);
     * </code>
     * 
     * the following:
     * 
     * <code>
     * if (itemId instanceOf _containerType_) {
     *     return new _item_<_containerType_>((_containerType_) itemId);
     * }
     * </code>
     * 
     * if (
     */
    private void insertSpecificMakerIfClauseForInstance(JDefinedClass container, JClass item, JClass containerType,
            JClass itemType) {
        JMethod makeItemByInstance = container.getMethod("makeItem", new JType[] { containerType });
        JVar itemId = makeItemByInstance.listParams()[0];
        makeItemByInstance.body().pos(0);
        JConditional iff = makeItemByInstance.body()._if(itemId._instanceof(itemType));
        iff._then()._return(JExpr._new(narrowToIfNeeded(item, itemType)).arg(JExpr.cast(itemType, itemId)));
    }

    /**
     * Insert into a method:
     * 
     * <code>
     * public Item makeItem(Class<? extends _containerType_> type);
     * </code>
     * 
     * the following:
     * 
     * <code>
     * if (type.equals(Class< extends _containerType_>)) {
     *     return new _item_<_class_>((Class< extends _containerType_>) type);
     * }
     * </code>
     * 
     * if (
     */
    private void insertSpecificMakerIfClauseForType(JDefinedClass container, JClass item, JClass containerType, JClass itemType) {
        JClass narrowedClass = container.owner().ref(Class.class).narrow(containerType.wildcard());

        JMethod makeItemByType = container.getMethod("makeItem", new JType[] { narrowedClass });
        JVar type = makeItemByType.listParams()[0];
        makeItemByType.body().pos(0);
        JConditional iff = makeItemByType.body()._if(type.invoke("equals").arg(itemType.dotclass()));
        JClass narrowedItemClass = item.owner().ref(Class.class).narrow(itemType.wildcard());
        iff._then()._return(JExpr._new(narrowToIfNeeded(item, itemType)).arg(JExpr.cast(narrowedItemClass, type)));
    }

    /**
     * <code>
     * public _item_ add_item_() {
     *     return addItem(_item_.class);
     * }
     * </code>
     */
    private void generateItemCreateShortcut(JDefinedClass container, JClass item, JClass containerType) {
        if (container.getMethod("add" + item.name(), new JType[0]) != null) {
            System.out.println("Duplicate subclass name: add" + item.name() + "() will not be generated for type: "
                    + containerType.fullName());
        } else {
            JMethod addItem = container.method(JMod.PUBLIC, item, "add" + item.name());
            addItem.body()._return(JExpr.cast(item, JExpr.invoke("addItem").arg(containerType.dotclass())));
        }
    }

    private String getItemSuffix(DomainEntity clazz) {
        Boolean isBaseItem = getPackageDir(clazz.getPackageName()) != null;
        return isBaseItem ? "Item_Base" : "Item";
    }

    private String getContainerSuffix(DomainEntity clazz) {
        Boolean isBaseItem = getPackageDir(clazz.getPackageName()) != null;
        return isBaseItem ? "Container_Base" : "Container";
    }

    /**
     * @param slot
     * @return
     */
    private JClass getSlotType(JCodeModel codegen, DomainEntity clazz, Slot slot) {
        final ValueType slotType = slot.getSlotType();
        if (slotType instanceof ParamValueType) {
            return codegen.ref(slotType.getBaseType().getFullname());
        }
        if (slotType instanceof EnumValueType) {
            String typeName = slotType.getFullname();
            if (typeName.contains(".")) {
                return codegen.ref(typeName);
            }
            return codegen.ref(clazz.getPackageName() + "." + typeName);
        }
        if (slotType.getFullname().contains(".")) {
            return codegen.ref(slotType.getFullname());
        }
        return JType.parse(codegen, slotType.getFullname()).boxify();
    }

    private File getPackageDir(final String packageName) {
        return getPackageDir2(packageName + ".data");
    }

    private File getPackageDir2(final String packageName) {
        final File file = packageMapper.get(packageName);
        if (file == null) {
            final int i = packageName.lastIndexOf('.');
            if (i != -1) {
                return getPackageDir2(packageName.substring(0, i));
            }
        }
        return file;
    }

    private String getProxyFullName(DomainEntity type, String suffix) {
        return String.format("%s.data.%s%s", type.getPackageName(), type.getName(), suffix);
    }

    private JDefinedClass getBaseItem(DomainEntity type) {
        return itemBaseMap.get(type);
    }

    private JDefinedClass getBaseContainer(DomainEntity type) {
        return containerBaseMap.get(type);
    }

    private JDefinedClass getLowestItemFor(DomainEntity type) {
        return itemMap.containsKey(type) ? itemMap.get(type) : itemBaseMap.get(type);
    }

    private JDefinedClass getLowestContainerFor(DomainEntity type) {
        return containerMap.containsKey(type) ? containerMap.get(type) : containerBaseMap.get(type);
    }

    private JClass getHostType(JDefinedClass container, DomainEntity clazz) {
        return container.typeParams().length != 0 ? container.typeParams()[0] : ref(container.owner(), clazz);
    }

    private JClass ref(JCodeModel model, DomainEntity clazz) {
        if (!refMap.containsKey(model)) {
            refMap.put(model, new HashMap<String, JClass>());
        }
        Map<String, JClass> src = refMap.get(model);
        if (!src.containsKey(clazz.getFullName())) {
            src.put(clazz.getFullName(), model.ref(clazz.getFullName()));
        }
        return src.get(clazz.getFullName());
    }

    private JClass narrowToIfNeeded(JClass item, JClass type) {
        return narrowToIfNeeded(item, type, false);
    }

    private JClass narrowToIfNeeded(JClass item, JClass type, boolean wildcard) {
        if (item.typeParams().length != 0 || item.isParameterized()) {
            if (wildcard) {
                return item.narrow(type.wildcard());
            }
            return item.narrow(type);
        }
        return item;
    }
}
