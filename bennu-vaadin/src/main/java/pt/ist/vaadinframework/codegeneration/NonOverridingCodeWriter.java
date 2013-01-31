/*
 * Copyright 2011 Instituto Superior Tecnico
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the vaadin-frameworkproxy-refactor.
 *
 *   The vaadin-frameworkproxy-refactor Infrastructure is free software: you can 
 *   redistribute it and/or modify it under the terms of the GNU Lesser General 
 *   Public License as published by the Free Software Foundation, either version 
 *   3 of the License, or (at your option) any later version.*
 *
 *   vaadin-frameworkproxy-refactor is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with vaadin-frameworkproxy-refactor. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package pt.ist.vaadinframework.codegeneration;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.commons.io.output.NullOutputStream;

import com.sun.codemodel.CodeWriter;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.writer.FilterCodeWriter;

/**
 * @author Pedro Santos (pedro.miguel.santos@ist.utl.pt)
 */
public class NonOverridingCodeWriter extends FilterCodeWriter {
	private final File srcDir;

	public NonOverridingCodeWriter(CodeWriter core, File srcDir) {
		super(core);
		this.srcDir = srcDir;
	}

	@Override
	public OutputStream openBinary(JPackage pkg, String fileName) throws IOException {
		return super.openBinary(pkg, fileName);
	}

	@Override
	public Writer openSource(JPackage pkg, String fileName) throws IOException {
		File wannaBeFile = new File(srcDir, pkg.name().replaceAll("\\.", "/") + File.separator + fileName);
		if (wannaBeFile.exists()) {
			return new OutputStreamWriter(new NullOutputStream());
		}
		System.out.println("Creating Data Layer Class: " + wannaBeFile.getAbsolutePath());
		return super.openSource(pkg, fileName);
	}
}
