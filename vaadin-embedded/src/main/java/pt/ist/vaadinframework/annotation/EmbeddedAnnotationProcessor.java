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
package pt.ist.vaadinframework.annotation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import org.apache.commons.io.FileUtils;

@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes({ "pt.ist.vaadinframework.annotation.EmbeddedComponent" })
public class EmbeddedAnnotationProcessor extends AbstractProcessor {

    public static final String LOG_FILENAME = ".embeddedAnnotationLog";
    public static final String ENTRY_SEPERATOR = "\n";

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        final Set<String> actions = new HashSet<String>();

        final File file = new File(LOG_FILENAME);
        if (file.exists()) {
            try {
                final String contents = FileUtils.readFileToString(file);
                for (final String line : contents.split(ENTRY_SEPERATOR)) {
                    actions.add(line);
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }

        final Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(EmbeddedComponent.class);
        for (final Element element : elements) {
            if (element instanceof TypeElement) {
                final TypeElement typeElement = (TypeElement) element;
                actions.add(typeElement.getQualifiedName().toString());
            } else {
                System.out.println("Skipping processing of element: " + element.getClass().getName()
                        + ", this type was not expected!");
            }
        }

        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(LOG_FILENAME, false);
            for (final String action : actions) {
                fileWriter.append(action);
                fileWriter.write(ENTRY_SEPERATOR);
            }
        } catch (final IOException e) {
            e.printStackTrace();
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return true;
    }

}
