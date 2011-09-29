package pt.ist.vaadinframework.fragment;

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

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @author Sérgio Silva (sergio.silva@ist.utl.pt)
 * 
 */

public class FragmentQueryTestApplication {

    public static String readln() {
	InputStreamReader converter = new InputStreamReader(System.in);
	BufferedReader reader = new BufferedReader(converter);
	try {
	    return reader.readLine();
	} catch (Throwable t) {
	    throw new Error(t);
	}
    }

    public static void main(String... args) {
	String line = null;
	System.out.printf(":$> ");
	while (!(line = readln()).equals("quit")) {
	    try {
		final FragmentQuery fragmentQuery = new FragmentQuery(line);
		System.out.println(fragmentQuery.toString());
	    } catch (InvalidFragmentException e) {
		System.out.println("Error : " + e.getMessage());
	    }
	    System.out.printf("\n:$> ");
	}

    }
}
