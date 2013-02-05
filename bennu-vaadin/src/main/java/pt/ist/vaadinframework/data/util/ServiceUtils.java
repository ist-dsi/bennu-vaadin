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
package pt.ist.vaadinframework.data.util;

import jvstm.CommitException;
import jvstm.cps.ConsistencyException;
import pt.ist.fenixframework.pstm.AbstractDomainObject.UnableToDetermineIdException;
import pt.ist.fenixframework.pstm.IllegalWriteException;

import com.vaadin.data.Buffered;

/**
 * @author Pedro Santos (pedro.miguel.santos@ist.utl.pt)
 * 
 */
public class ServiceUtils {
    public static void handleException(Throwable throwable) {
        // This is a little hackish but is somewhat forced by the
        // combination of architectures of both vaadin and the jvstm
        if (throwable instanceof IllegalWriteException) {
            throw (IllegalWriteException) throwable;
        } else if (throwable instanceof ConsistencyException) {
            throw (ConsistencyException) throwable;
        } else if (throwable instanceof UnableToDetermineIdException) {
            throw (UnableToDetermineIdException) throwable;
        } else if (throwable instanceof CommitException) {
            throw (CommitException) throwable;
        } else if (throwable instanceof Buffered.SourceException) {
            for (Throwable cause : ((Buffered.SourceException) throwable).getCauses()) {
                handleException(cause);
            }
        } else if (throwable.getCause() != null) {
            handleException(throwable.getCause());
        }
    }
}
