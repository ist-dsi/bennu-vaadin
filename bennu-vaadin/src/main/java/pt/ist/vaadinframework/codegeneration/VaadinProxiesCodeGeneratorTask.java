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
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import pt.ist.fenixframework.FenixFrameworkPlugin;
import pt.ist.fenixframework.pstm.DML;
import pt.ist.fenixframework.pstm.dml.FenixDomainModel;
import pt.utl.ist.fenix.tools.util.MultiProperty;
import antlr.ANTLRException;

import com.sun.codemodel.JClassAlreadyExistsException;

/**
 * @author Pedro Santos (pedro.miguel.santos@ist.utl.pt)
 * 
 */
public class VaadinProxiesCodeGeneratorTask extends Task {
	private File srcBaseDir;

	private File buildDir;

	private File packageSourceLocations;

	private String vaadinSrcDir;

	private final List<FileSet> filesets = new ArrayList<FileSet>();

	public File getSrcBaseDir() {
		return srcBaseDir;
	}

	public void setSrcBaseDir(File srcBaseDir) {
		this.srcBaseDir = srcBaseDir;
	}

	public File getBuildDir() {
		return buildDir;
	}

	public void setBuildDir(File buildDir) {
		this.buildDir = buildDir;
	}

	public File getPackageSourceLocations() {
		return packageSourceLocations;
	}

	public void setPackageSourceLocations(File packageSourceLocations) {
		this.packageSourceLocations = packageSourceLocations;
	}

	public String getVaadinSrcDir() {
		return vaadinSrcDir;
	}

	public void setVaadinSrcDir(String vaadinSrcDir) {
		this.vaadinSrcDir = vaadinSrcDir;
	}

	public void addFileset(FileSet fileset) {
		filesets.add(fileset);
	}

	@Override
	public void execute() throws BuildException {
		super.execute();

		DateTime start = new DateTime();
		final Properties properties = new MultiProperty();
		try {
			properties.load(new FileInputStream(buildDir.getAbsolutePath() + "/WEB-INF/classes/configuration.properties"));
			File timestampFile = new File(srcBaseDir, "vaadin-timestamp");
			long latestBuildTime = srcBaseDir != null ? srcBaseDir.lastModified() : 0;

			boolean shouldCompile = false;

			// final String preInitClassnames =
			// properties.getProperty("pre.init.classnames");
			// System.out.println("Pre-init class names: " + preInitClassnames);
			// if (preInitClassnames != null) {
			// final String[] classnames = preInitClassnames.split(",");
			// for (final String classname : classnames) {
			// if (classname != null && !classname.isEmpty()) {
			// try {
			// Class.forName(classname.trim());
			// } catch (final ClassNotFoundException e) {
			// throw new Error(e);
			// }
			// }
			// }
			// }

			List<URL> domainModelURLs = new ArrayList<URL>();

			// whereToInject keeps track where the DMLs for the plugin should
			// be injected, so they are sequential injected and before the
			// application DMLs

			FenixFrameworkPlugin[] plugins = getPluginArray(properties);
			if (plugins != null) {
				for (FenixFrameworkPlugin plugin : plugins) {
					List<URL> pluginDomainModel = plugin.getDomainModel();
					domainModelURLs.addAll(pluginDomainModel);
				}
			}

			for (FileSet fileset : filesets) {
				if (fileset.getDir().exists()) {
					DirectoryScanner scanner = fileset.getDirectoryScanner(getProject());
					String[] includedFiles = scanner.getIncludedFiles();
					for (String includedFile : includedFiles) {
						String filePath = fileset.getDir().getAbsolutePath() + "/" + includedFile;
						File file = new File(filePath);
						boolean isModified = file.lastModified() > latestBuildTime;
						// System.out.println(includedFile + " : " + (isModified
						// ? "not up to date" : "up to date"));
						domainModelURLs.add(new File(filePath).toURI().toURL());
						shouldCompile = shouldCompile || isModified;
					}
				}
			}

			// first, get the domain model
			FenixDomainModel model = DML.getDomainModelForURLs(FenixDomainModel.class, domainModelURLs, false);
			VaadinProxiesCodeGenerator generator =
					new VaadinProxiesCodeGenerator(model, srcBaseDir, vaadinSrcDir, packageSourceLocations);
			generator.generate();
			timestampFile.delete();
			timestampFile.createNewFile();
			// } else {
			// System.out.println("All dml files are up to date, skipping generation");
			// }
		} catch (IOException e) {
			throw new BuildException(e);
		} catch (ANTLRException e) {
			throw new BuildException(e);
		} catch (JClassAlreadyExistsException e) {
			throw new BuildException(e);
		}
		Duration processingTime = new Duration(start, new DateTime());
		PeriodFormatter formatter =
				new PeriodFormatterBuilder().appendMinutes().appendSuffix("m").appendSeconds().appendSuffix("s").toFormatter();
		System.out.println("Vaadin Generation Took: " + formatter.print(processingTime.toPeriod()));
	}

	private FenixFrameworkPlugin[] getPluginArray(Properties properties) {
		String property = properties.getProperty("plugins");
		if (StringUtils.isEmpty(property)) {
			return new FenixFrameworkPlugin[0];
		}
		String[] classNames = property.split("\\s*,\\s*");

		FenixFrameworkPlugin[] pluginArray = new FenixFrameworkPlugin[classNames.length];
		for (int i = 0; i < classNames.length; i++) {
			try {
				pluginArray[i] = (FenixFrameworkPlugin) Class.forName(classNames[i].trim()).newInstance();
			} catch (InstantiationException e) {
				throw new Error(e);
			} catch (IllegalAccessException e) {
				throw new Error(e);
			} catch (ClassNotFoundException e) {
				throw new Error(e);
			}
		}
		return pluginArray;
	}

}
