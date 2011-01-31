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
package com.vaadin.data.util;

import com.vaadin.data.Buffered;
import com.vaadin.data.Validator.InvalidValueException;

/**
 * @author Pedro Santos (pedro.miguel.santos@ist.utl.pt)
 * 
 */
@SuppressWarnings("serial")
public abstract class BufferedProxy implements Buffered {
    private boolean writeThrough = true;

    private boolean readThrough = true;

    /**
     * @see com.vaadin.data.Buffered#isWriteThrough()
     */
    @Override
    public boolean isWriteThrough() {
	return writeThrough;
    }

    /**
     * @see com.vaadin.data.Buffered#setWriteThrough(boolean)
     */
    @Override
    public void setWriteThrough(boolean writeThrough) throws SourceException, InvalidValueException {
	this.writeThrough = writeThrough;
    }

    /**
     * @see com.vaadin.data.Buffered#isReadThrough()
     */
    @Override
    public boolean isReadThrough() {
	return readThrough;
    }

    /**
     * @see com.vaadin.data.Buffered#setReadThrough(boolean)
     */
    @Override
    public void setReadThrough(boolean readThrough) throws SourceException {
	this.readThrough = readThrough;
    }
}
