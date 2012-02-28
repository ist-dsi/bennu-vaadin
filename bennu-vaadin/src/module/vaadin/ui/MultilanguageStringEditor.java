package module.vaadin.ui;

import myorg.domain.VirtualHost;

import org.apache.commons.lang.StringUtils;

import pt.utl.ist.fenix.tools.util.i18n.Language;
import pt.utl.ist.fenix.tools.util.i18n.MultiLanguageString;

import com.vaadin.data.Buffered;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.AbstractProperty;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.Form;

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
	private final MultiLanguageString mls;

	public MLSItem(MultiLanguageString mls) {
	    this.mls = mls;
	    if (mls == null) {
		mls = new MultiLanguageString();
	    }
	    String langs = VirtualHost.getVirtualHostForThread().getSupportedLanguages();
	    if (StringUtils.isNotBlank(langs)) {
		for (String langString : langs.split(":")) {
		    Language lang = Language.valueOf(langString);
		    addItemProperty(lang, new MLSProperty(lang, mls));
		}
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
	MultiLanguageString mls = (newValue instanceof MultiLanguageString) ? (MultiLanguageString) newValue
		: new MultiLanguageString();

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