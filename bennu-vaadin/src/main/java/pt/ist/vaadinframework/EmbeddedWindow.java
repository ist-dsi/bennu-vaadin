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

import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.lang.StringUtils;

import pt.ist.vaadinframework.annotation.EmbeddedComponentUtils;
import pt.ist.vaadinframework.fragment.FragmentQuery;
import pt.ist.vaadinframework.ui.EmbeddedComponentContainer;

import com.vaadin.ui.UriFragmentUtility;
import com.vaadin.ui.UriFragmentUtility.FragmentChangedEvent;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * @author Pedro Santos (pedro.miguel.santos@ist.utl.pt), Sérgio Silva
 *         (sergio.silva@ist.utl.pt)
 */
public class EmbeddedWindow extends Window {
	final UriFragmentUtility fragmentUtility = new UriFragmentUtility();

	private final Stack<String> history = new Stack<>();

	private final Map<String, EmbeddedComponentContainer> pageCache = new HashMap<>();

	public void open(String fragment) {
		fragmentUtility.setFragment(fragment);
	}

	public void back() {
		try {
			history.pop(); // consume current
			fragmentUtility.setFragment(history.pop());
		} catch (EmptyStackException e) {
			// back fails quietly if no history is available
		}
	}

	public void refresh() {
		loadFragment(history.peek(), false);
	}

	public FragmentQuery getFragment() {
		return new FragmentQuery("#" + fragmentUtility.getFragment());
	}

	public EmbeddedWindow() {
		setImmediate(true);
		final VerticalLayout layout = new VerticalLayout();
		layout.addComponent(fragmentUtility);
		fragmentUtility.addListener(new UriFragmentUtility.FragmentChangedListener() {
			@Override
			public void fragmentChanged(FragmentChangedEvent source) {
				String fragment = source.getUriFragmentUtility().getFragment();
				loadFragment(fragment, true);
			}
		});
		setContent(layout);
	}

	private void loadFragment(String fragment, boolean logHistory) {
		FragmentQuery query = new FragmentQuery("#" + fragment);

		EmbeddedComponentContainer page = null;
		if (pageCache.containsKey(fragment)) {
			page = pageCache.get(fragment);
		} else {
			try {
				Class<? extends EmbeddedComponentContainer> requestedType = EmbeddedApplication.getPage(query.getPath());
				if (requestedType == null) {
					showNotification("Página não encontrada", "A página pedida não foi encontrada no servidor",
							Notification.TYPE_ERROR_MESSAGE);
					VaadinFrameworkLogger.getLogger().info("O fragmento: " + fragment + " não foi encontrado");
				} else {
					EmbeddedComponentContainer container = requestedType.newInstance();
					if (container.isAllowedToOpen(query.getParams())) {
						container.setArguments(query.getParams());
						if (EmbeddedComponentUtils.getAnnotation(requestedType).persistent()) {
							pageCache.put(fragment, container);
						}
						page = container;
					} else {
						showNotification("Acesso negado", "Não tem permissões para aceder a esta página",
								Notification.TYPE_ERROR_MESSAGE);
						VaadinFrameworkLogger.getLogger().info("O fragmento: " + fragment + " não pode ser acedido");
					}
				}
			} catch (InstantiationException e) {
				throw new PageLoadingError(e);
			} catch (IllegalAccessException e) {
				throw new PageLoadingError(e);
			}
		}
		if (page != null) {
			getContent().removeAllComponents();
			getContent().addComponent(fragmentUtility);
			getContent().addComponent(page);
			if (logHistory) {
				history.push(fragment);
				VaadinFrameworkLogger.getLogger().info("history: " + StringUtils.join(history, " > "));
			}
		}
	}
}
