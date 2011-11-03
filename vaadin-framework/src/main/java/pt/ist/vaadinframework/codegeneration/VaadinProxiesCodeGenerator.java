/*
 * Copyright 2011 Instituto Superior Tecnico
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the vaadin-framework-ant.
 *
 *   The vaadin-framework-ant Infrastructure is free software: you can 
 *   redistribute it and/or modify it under the terms of the GNU Lesser General 
 *   Public License as published by the Free Software Foundation, either version 
 *   3 of the License, or (at your option) any later version.*
 *
 *   vaadin-framework-ant is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with vaadin-framework-ant. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package pt.ist.vaadinframework.codegeneration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.WordUtils;

import pt.ist.vaadinframework.data.BufferedItem.BufferedProperty;
import pt.ist.vaadinframework.data.HintedProperty;
import pt.ist.vaadinframework.data.VBoxProperty;
import pt.ist.vaadinframework.data.hints.Required;
import pt.ist.vaadinframework.data.reflect.DomainContainer;
import pt.ist.vaadinframework.data.reflect.DomainItem;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JVar;
import com.vaadin.data.Property;

import dml.DomainClass;
import dml.DomainEntity;
import dml.DomainModel;
import dml.Role;
import dml.Slot;
import dml.Slot.Option;

/**
 * @author Pedro Santos (pedro.miguel.santos@ist.utl.pt)
 */
public class VaadinProxiesCodeGenerator {
    private final DomainModel model;

    private final File srcBaseDir;

    private final Map<DomainClass, Map<String, JFieldVar>> propertyMap = new HashMap<DomainClass, Map<String, JFieldVar>>();

    private final Map<DomainClass, Set<DomainClass>> subTypeMap = new HashMap<DomainClass, Set<DomainClass>>();

    private static final Map<String, File> packageMapper = new HashMap<String, File>();

    public VaadinProxiesCodeGenerator(DomainModel model, File srcBaseDir, String vaadinSrcDir) {
	this.model = model;
	this.srcBaseDir = srcBaseDir;
	InputStream inputStream;
	try {
	    inputStream = getClass().getResourceAsStream("/.dmlProjectPackageMapper");
	    StringWriter writer = new StringWriter();
	    IOUtils.copy(inputStream, writer);
	    final String contents = writer.toString();
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
	    if (clazz.getSuperclass() != null) {
		if (!subTypeMap.containsKey(clazz.getSuperclass())) {
		    subTypeMap.put((DomainClass) clazz.getSuperclass(), new HashSet<DomainClass>());
		}
		subTypeMap.get(clazz.getSuperclass()).add(clazz);
	    }
	}
	for (DomainClass clazz : model.getDomainClasses()) {
	    generateProperty(srcgen, clazz);
	    generateBaseItem(srcgen, clazz);
	    generateBaseContainer(srcgen, clazz);
	    File packageDir = getPackageDir(clazz.getPackageName());
	    if (packageDir != null) {
		if (!localsrcs.containsKey(packageDir)) {
		    localsrcs.put(packageDir, new JCodeModel());
		}
		generateNotBaseItem(localsrcs.get(packageDir), clazz);
		generateNotBaseContainer(localsrcs.get(packageDir), clazz);
	    }
	}
	for (DomainClass clazz : model.getDomainClasses()) {
	    if (clazz.getSuperclass() != null) {
		generateContainerSuperMethods(srcgen, clazz);
		generatePropertyImplements(srcgen, clazz);
	    }
	}
	srcgen.build(srcBaseDir);
	for (File packageDir : localsrcs.keySet()) {
	    packageDir.mkdirs();
	    localsrcs.get(packageDir).build(packageDir);
	}
    }

    private void generateProperty(JCodeModel model, DomainClass clazz) throws JClassAlreadyExistsException {
	JPackage pckg = model._package(clazz.getPackageName());
	JDefinedClass properties = pckg._interface(clazz.getName() + "Properties");
	propertyMap.put(clazz, new HashMap<String, JFieldVar>());
	for (Slot slot : clazz.getSlotsList()) {
	    JFieldVar field = properties.field(JMod.PUBLIC | JMod.STATIC | JMod.FINAL, String.class,
		    getTableName(slot.getName()), JExpr.lit(slot.getName()));
	    propertyMap.get(clazz).put(slot.getName(), field);
	}
	for (Role role : clazz.getRoleSlotsList()) {
	    if (role.getName() != null && !role.getType().getName().startsWith("Remote")) {
		JFieldVar field = properties.field(JMod.PUBLIC | JMod.STATIC | JMod.FINAL, String.class,
			getTableName(role.getName()), JExpr.lit(role.getName()));
		propertyMap.get(clazz).put(role.getName(), field);
	    }
	}
    }

    private void generatePropertyImplements(JCodeModel codegen, DomainClass clazz) {
	JDefinedClass supertype = codegen._getClass(clazz.getSuperclass().getFullName() + "Properties");
	JDefinedClass type = codegen._getClass(clazz.getFullName() + "Properties");
	type._implements(supertype);
    }

    private void generateBaseItem(JCodeModel codegen, DomainClass clazz) throws JClassAlreadyExistsException {
	String suffix = getPackageDir(clazz.getPackageName()) != null ? "Item_Base" : "Item";
	JDefinedClass type = codegen._class(clazz.getFullName() + suffix);
	if (clazz.getSuperclass() != null) {
	    type._extends(domainItemRef(codegen, clazz.getSuperclass()));
	} else {
	    type._extends(codegen.ref(DomainItem.class).narrow(codegen.ref(clazz.getFullName())));
	}
	type._implements(domainPropertiesRef(codegen, clazz));

	JMethod byProperty = type.constructor(JMod.PUBLIC);
	JVar propertyParam = byProperty.param(HintedProperty.class, "value");
	byProperty.body().invoke("super").arg(propertyParam);

	JMethod byType = type.constructor(JMod.PUBLIC);
	JVar typeParam = byType.param(codegen.ref(Class.class).narrow(codegen.ref(clazz.getFullName()).wildcard()), "type");
	byType.body().invoke("super").arg(typeParam);

	JMethod byInstance = type.constructor(JMod.PUBLIC);
	JVar instanceParam = byInstance.param(codegen.ref(clazz.getFullName()), "instance");
	byInstance.body().invoke("super").arg(instanceParam);

	{
	    JMethod getItemProperty = type.method(JMod.PUBLIC, Property.class, "getItemProperty");
	    getItemProperty.annotate(Override.class);
	    JVar propertyIdParam = getItemProperty.param(Object.class, "propertyId");
	    JBlock getItemPropertyBody = getItemProperty.body();
	    JVar propertyVar = getItemPropertyBody.decl(codegen.ref(Property.class), "property", JExpr.direct("map")
		    .invoke("get").arg(propertyIdParam));
	    JBlock ifbody = getItemPropertyBody._if(propertyVar.eq(JExpr._null()))._then();
	    for (Slot slot : clazz.getSlotsList()) {
		JBlock slotIfBody = ifbody._if(propertyMap.get(clazz).get(slot.getName()).eq(propertyIdParam))._then();
		slotIfBody._return(JExpr.invoke("get" + WordUtils.capitalize(slot.getName()) + "Property"));
	    }
	    for (Role role : clazz.getRoleSlotsList()) {
		if (role.getName() != null && !role.getType().getName().startsWith("Remote")) {
		    JBlock slotIfBody = ifbody._if(propertyMap.get(clazz).get(role.getName()).eq(propertyIdParam))._then();
		    slotIfBody._return(JExpr.invoke("get" + WordUtils.capitalize(role.getName()) + "Property"));
		}
	    }
	    ifbody.assign(propertyVar, JExpr.invoke("makeProperty").arg(propertyIdParam));
	    getItemPropertyBody._return(propertyVar);
	}

	for (Slot slot : clazz.getSlotsList()) {
	    JMethod propertyAccess = type.method(JMod.PUBLIC, codegen.ref(Property.class),
		    "get" + WordUtils.capitalize(slot.getName()) + "Property");
	    // propertyAccess.body()._return(JExpr.invoke("getItemProperty").arg(propertyMap.get(clazz).get(slot.getName())));
	    JBlock body = propertyAccess.body();
	    JFieldVar slotProperty = propertyMap.get(clazz).get(slot.getName());
	    JVar propertyVar = body.decl(codegen.ref(HintedProperty.class), "property",
		    JExpr.direct("map").invoke("get").arg(slotProperty));
	    JBlock ifbody = body._if(propertyVar.eq(JExpr._null()))._then();
	    JInvocation newBP = JExpr._new(codegen.ref(BufferedProperty.class));
	    newBP.arg(slotProperty);
	    newBP.arg(JExpr.dotclass(codegen.ref(slot.getSlotType().getFullname())));
	    if (slot.getOptions().contains(Option.REQUIRED)) {
		newBP.arg(JExpr._new(codegen.ref(Required.class)));
	    }
	    ifbody.assign(propertyVar, newBP);
	    body._return(propertyVar);
	}
	for (Role role : clazz.getRoleSlotsList()) {
	    if (role.getName() != null && !role.getType().getName().startsWith("Remote")) {
		JClass returnType = role.getMultiplicityUpper() == 1 ? domainItemRef(codegen, role.getType())
			: domainContainerRef(codegen, role.getType());
		JMethod propertyAccess = type.method(JMod.PUBLIC, returnType, "get" + WordUtils.capitalize(role.getName())
			+ "Property");
		JBlock body = propertyAccess.body();
		JFieldVar slotProperty = propertyMap.get(clazz).get(role.getName());
		JVar propertyVar = body.decl(codegen.ref(HintedProperty.class), "property", JExpr.direct("map").invoke("get")
			.arg(slotProperty));
		JBlock ifbody = body._if(propertyVar.eq(JExpr._null()))._then();
		JInvocation newBP = JExpr._new(codegen.ref(BufferedProperty.class));
		newBP.arg(slotProperty);
		if (role.getMultiplicityUpper() == 1) {
		    newBP.arg(JExpr.dotclass(codegen.ref(role.getType().getFullName())));
		} else {
		    newBP.arg(JExpr.dotclass(codegen.ref(Set.class)));
		}
		if (role.getMultiplicityLower() == 1) {
		    newBP.arg(JExpr._new(codegen.ref(Required.class)));
		}
		ifbody.assign(propertyVar, newBP);
		if (role.getMultiplicityUpper() == 1) {
		    body._return(JExpr._new(codegen.ref(role.getType().getFullName() + "Item")).arg(propertyVar));
		} else {
		    body._return(JExpr._new(codegen.ref(role.getType().getFullName() + "Container")).arg(propertyVar));
		}
	    }
	}
    }

    private void generateNotBaseItem(JCodeModel codegen, DomainClass clazz) throws JClassAlreadyExistsException {
	JDefinedClass type = codegen._class(clazz.getFullName() + "Item");
	type._extends(domainBaseItemRef(codegen, clazz));

	JMethod byProperty = type.constructor(JMod.PUBLIC);
	JVar propertyParam = byProperty.param(HintedProperty.class, "value");
	byProperty.body().invoke("super").arg(propertyParam);

	JMethod byType = type.constructor(JMod.PUBLIC);
	JVar typeParam = byType.param(codegen.ref(Class.class).narrow(codegen.ref(clazz.getFullName()).wildcard()), "type");
	byType.body().invoke("super").arg(typeParam);

	JMethod byInstance = type.constructor(JMod.PUBLIC);
	JVar instanceParam = byInstance.param(codegen.ref(clazz.getFullName()), "instance");
	byInstance.body().invoke("super").arg(instanceParam);
    }

    private void generateBaseContainer(JCodeModel codegen, DomainClass clazz) throws JClassAlreadyExistsException {
	String suffix = getPackageDir(clazz.getPackageName()) != null ? "Container_Base" : "Container";
	JDefinedClass type = codegen._class(clazz.getFullName() + suffix);
	if (clazz.getSuperclass() != null) {
	    type._extends(domainContainerRef(codegen, clazz.getSuperclass()));
	} else {
	    type._extends(codegen.ref(DomainContainer.class).narrow(codegen.ref(clazz.getFullName())));
	}
	type._implements(domainPropertiesRef(codegen, clazz));

	if (clazz.getSuperclass() == null) {
	    JMethod noargs = type.constructor(JMod.PUBLIC);
	    JInvocation noargsInvocation = noargs.body().invoke("super");
	    noargsInvocation.arg(JExpr._new(codegen.ref(VBoxProperty.class)).arg(JExpr.dotclass(codegen.ref(Collection.class))));
	    noargsInvocation.arg(JExpr.dotclass(codegen.ref(clazz.getFullName())));

	    JMethod byProperty = type.constructor(JMod.PUBLIC);
	    JInvocation byPropertyInvocation = byProperty.body().invoke("super");
	    byPropertyInvocation.arg(byProperty.param(HintedProperty.class, "value"));
	    byPropertyInvocation.arg(JExpr.dotclass(codegen.ref(clazz.getFullName())));

	    JMethod byCollection = type.constructor(JMod.PUBLIC);
	    JInvocation byCollectionInvocation = byCollection.body().invoke("super");
	    JVar valueSet = byCollection.param(codegen.ref(Collection.class).narrow(codegen.ref(clazz.getFullName()).wildcard()),
		    "valueSet");
	    byCollectionInvocation.arg(JExpr._new(codegen.ref(VBoxProperty.class)).arg(valueSet));
	    byCollectionInvocation.arg(JExpr.dotclass(codegen.ref(clazz.getFullName())));
	} else {
	    type.constructor(JMod.PUBLIC).body().invoke("super");

	    JMethod byProperty = type.constructor(JMod.PUBLIC);
	    byProperty.body().invoke("super").arg(byProperty.param(HintedProperty.class, "value"));

	    JMethod byCollection = type.constructor(JMod.PUBLIC);
	    JVar valueSet = byCollection.param(codegen.ref(Collection.class).narrow(codegen.ref(clazz.getFullName()).wildcard()),
		    "valueSet");
	    byCollection.body().invoke("super").arg(valueSet);
	}

	if (clazz.getSuperclass() == null) {
	    JMethod makeItem = type.method(JMod.PUBLIC, domainItemRef(codegen, clazz), "makeItem");
	    JVar itemId = makeItem.param(HintedProperty.class, "itemId");
	    if (subTypeMap.containsKey(clazz)) {
		for (DomainClass subType : subTypeMap.get(clazz)) {
		    JBlock ifBlock = makeItem.body()
			    ._if(itemId.invoke("getType").eq(JExpr.dotclass(codegen.ref(subType.getFullName()))))._then();
		    ifBlock._return(JExpr._new(domainItemRef(codegen, subType)).arg(itemId));
		}
	    }
	    makeItem.body()._return(JExpr._new(domainItemRef(codegen, clazz)).arg(itemId));
	}

	JClass typeItemClass = domainItemRef(codegen, clazz);

	JMethod getItem = type.method(JMod.PUBLIC, typeItemClass, "getItem");
	getItem.annotate(Override.class);
	JVar getItemParam = getItem.param(codegen.ref(Object.class), "itemId");
	getItem.body()._return(JExpr.cast(typeItemClass, JExpr._super().invoke("getItem").arg(getItemParam)));

	if (clazz.getSuperclass() == null) {
	    JClass domainItem = domainItemRef(codegen, clazz);
	    JMethod addByType = type.method(JMod.PUBLIC, domainItem, "add" + clazz.getName() + "Item");
	    addByType.body()
		    ._return(
			    JExpr.cast(domainItem,
				    JExpr._super().invoke("addItem").arg(JExpr.dotclass(codegen.ref(clazz.getFullName())))));
	}
    }

    private void generateContainerSuperMethods(JCodeModel codegen, DomainClass clazz) {
	DomainClass currentSuper = clazz;
	while (currentSuper.getSuperclass() != null) {
	    currentSuper = (DomainClass) currentSuper.getSuperclass();
	}
	JDefinedClass supertype = codegen._getClass(currentSuper.getFullName() + "Container_Base");
	if (supertype != null) {
	    JClass type = domainItemRef(codegen, clazz);
	    for (JMethod method : supertype.methods()) {
		if (method.name().equals("add" + clazz.getName() + "Item")) {
		    System.out.println("Duplicate subclass name: " + clazz.getName());
		    return;
		}
	    }
	    JMethod addByType = supertype.method(JMod.PUBLIC, type, "add" + clazz.getName() + "Item");
	    addByType.body()._return(
		    JExpr.cast(type, JExpr._super().invoke("addItem").arg(JExpr.dotclass(codegen.ref(clazz.getFullName())))));
	}
    }

    private void generateNotBaseContainer(JCodeModel codegen, DomainClass clazz) throws JClassAlreadyExistsException {
	JDefinedClass type = codegen._class(clazz.getFullName() + "Container");
	type._extends(domainBaseContainerRef(codegen, clazz));

	type.constructor(JMod.PUBLIC).body().invoke("super");

	JMethod byProperty = type.constructor(JMod.PUBLIC);
	byProperty.body().invoke("super").arg(byProperty.param(HintedProperty.class, "value"));

	JMethod byCollection = type.constructor(JMod.PUBLIC);
	JVar valueSet = byCollection.param(codegen.ref(Collection.class).narrow(codegen.ref(clazz.getFullName()).wildcard()),
		"valueSet");
	byCollection.body().invoke("super").arg(valueSet);
    }

    private static String getTableName(final String name) {
	final StringBuilder stringBuilder = new StringBuilder();
	boolean isFirst = true;
	for (final char c : name.toCharArray()) {
	    if (isFirst) {
		isFirst = false;
		stringBuilder.append(Character.toUpperCase(c));
	    } else {
		if (Character.isUpperCase(c)) {
		    stringBuilder.append('_');
		    stringBuilder.append(c);
		} else {
		    stringBuilder.append(Character.toUpperCase(c));
		}
	    }
	}
	return stringBuilder.toString();
    }

    private File getPackageDir(final String packageName) {
	final File file = packageMapper.get(packageName);
	if (file == null) {
	    final int i = packageName.lastIndexOf('.');
	    if (i != -1) {
		return getPackageDir(packageName.substring(0, i));
	    }
	}
	return file;
    }

    private JClass domainContainerRef(JCodeModel codegen, DomainEntity type) {
	return codegen.ref(type.getFullName() + "Container");
    }

    private JClass domainBaseContainerRef(JCodeModel codegen, DomainEntity type) {
	return codegen.ref(type.getFullName() + "Container_Base");
    }

    private JClass domainItemRef(JCodeModel codegen, DomainEntity type) {
	return codegen.ref(type.getFullName() + "Item");
    }

    private JClass domainBaseItemRef(JCodeModel codegen, DomainEntity type) {
	return codegen.ref(type.getFullName() + "Item_Base");
    }

    private JClass domainPropertiesRef(JCodeModel codegen, DomainEntity type) {
	return codegen.ref(type.getFullName() + "Properties");
    }
}
