/*
 * @(#)MultilanguageStringEditor.java
 *
 * Copyright 2011 Instituto Superior Tecnico
 * Founding Authors: Pedro Santos
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the Bennu-Vadin Integration Module.
 *
 *   The Bennu-Vadin Integration Module is free software: you can
 *   redistribute it and/or modify it under the terms of the GNU Lesser General
 *   Public License as published by the Free Software Foundation, either version 
 *   3 of the License, or (at your option) any later version.
 *
 *   The Bennu-Vadin Module is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with the Bennu-Vadin Module. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package module.vaadin.ui;

import pt.ist.bennu.core.domain.VirtualHost;
import pt.utl.ist.fenix.tools.util.i18n.Language;
import pt.utl.ist.fenix.tools.util.i18n.MultiLanguageString;

import com.vaadin.data.Buffered;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.AbstractProperty;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.Form;

/**
 * 
 * @author Pedro Santos
 * 
 */
public class MultilanguageStringEditor extends CustomField {
	public static class MLSProperty extends AbstractProperty {
		private final Language lang;

		private MultiLanguageString mls;

		public MLSProperty(Language lang, MultiLanguageString mls) {
			this.lang = lang;
			this.mls = mls;
		}

		@Override
		public Object getValue() {
			return mls.getContent(lang);
		}

		@Override
		public void setValue(Object newValue) throws ReadOnlyException, ConversionException {
			mls = mls.with(lang, (String) newValue);
			fireValueChange();
		}

		@Override
		public Class<?> getType() {
			return String.class;
		}
	}

	public static class MLSItem extends PropertysetItem {
		public MLSItem(MultiLanguageString mls) {
			if (mls == null) {
				mls = new MultiLanguageString();
			}
			for (Language language : VirtualHost.getVirtualHostForThread().getSupportedLanguagesSet()) {
				addItemProperty(language, new MLSProperty(language, mls));
			}
		}
	}

	private final Form innerForm;

	public MultilanguageStringEditor(Form parentForm) {
		innerForm = new Form();
		innerForm.setWriteThrough(parentForm.isWriteThrough());
		innerForm.setFormFieldFactory(parentForm.getFormFieldFactory());
		setCompositionRoot(innerForm);
	}

	@Override
	public void setCaption(String caption) {
		super.setCaption(caption);
		innerForm.setCaption(caption);
	}

	@Override
	public void setInternalValue(Object newValue) throws ReadOnlyException, ConversionException {
		MultiLanguageString mls =
				(newValue instanceof MultiLanguageString) ? (MultiLanguageString) newValue : new MultiLanguageString();

		super.setInternalValue(mls);

		// set item data source and visible properties in a single operation to
		// avoid creating fields multiple times
		innerForm.setItemDataSource(new MLSItem(mls));
	}

	@Override
	public void commit() throws Buffered.SourceException, InvalidValueException {
		super.commit();
		innerForm.commit();
	}

	@Override
	public void discard() throws Buffered.SourceException {
		super.discard();
		innerForm.discard();
	}

	@Override
	public Class<?> getType() {
		return MultiLanguageString.class;
	}
}
