/*
 * Copyright 2010 Instituto Superior Tecnico
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
package pt.ist.vaadinframework.ui;

import java.util.Collections;
import java.util.Set;

/**
 * @author Pedro Santos (pedro.miguel.santos@ist.utl.pt)
 * 
 */
public class SelectOptionProvider {
    public static Set<?> provideFor(Class<?> type) {
//        DomainModel model = FenixFramework.getDomainModel();
//        DomainClass domainClass = model.findClass(type.getName());
//        Set<String> roots = getRoots();
//        for (Role role : domainClass.getRoleSlotsList()) {
//            if (roots.contains(role.getOtherRole().getType().getFullName())) {
//
//            }
//        }
        return Collections.emptySet();
    }

//    private static Set<String> getRoots() {
//        Set<String> roots = new HashSet<String>();
//        PersistentRoot root = AbstractDomainObject.fromOID(1L);
//        while (root != null) {
//            roots.add(root.getRootObject().getClass().getName());
//            root = root.hasNext() ? root.getNext() : null;
//        }
//        return roots;
//    }
}
