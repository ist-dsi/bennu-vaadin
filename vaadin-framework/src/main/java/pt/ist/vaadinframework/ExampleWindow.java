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
package pt.ist.vaadinframework;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.UriFragmentUtility;
import com.vaadin.ui.UriFragmentUtility.FragmentChangedEvent;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * @author Pedro Santos (pedro.miguel.santos@ist.utl.pt)
 * 
 */
public class ExampleWindow extends Window {
    private final UriFragmentUtility fragmentUtility = new UriFragmentUtility();

    private Component current;

    private final VerticalLayout layout;

    public ExampleWindow() {
	layout = new VerticalLayout();
	layout.addComponent(fragmentUtility);
	fragmentUtility.addListener(new UriFragmentUtility.FragmentChangedListener() {
	    @Override
	    public void fragmentChanged(FragmentChangedEvent source) {
		String fragment = source.getUriFragmentUtility().getFragment();

		Matcher matcher = Pattern.compile("ic/(.*)/edit").matcher(fragment);
		if (matcher.matches()) {
		    setBody(new XptoComponent(matcher.group(1)));
		}
	    }
	});
	setContent(layout);
    }

    private void setBody(Component component) {
	if (current != null) {
	    layout.replaceComponent(current, component);
	} else {
	    layout.addComponent(component);
	}
	current = component;
    }

    public class XptoComponent extends Label {
	public XptoComponent(String string) {
	    super("IC numero: " + string);
	}
    }
}
