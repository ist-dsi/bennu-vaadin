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
package pt.ist.vaadinframework.ui;

import java.util.ArrayList;
import java.util.Collection;

import jvstm.TransactionalCommand;
import pt.ist.fenixframework.pstm.AbstractDomainObject;
import pt.ist.fenixframework.pstm.Transaction;

import com.vaadin.data.Buffered;
import com.vaadin.data.Item;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Window.Notification;

/**
 * @author Pedro Santos (pedro.miguel.santos@ist.utl.pt)
 */
@SuppressWarnings("serial")
public class TxForm extends Form {
    public static enum FormStyle {
	WRITE_THROUGH, SAVE_CLEAR_CANCEL;
    }

    private Collection<ClickListener> saveListeners, discardListeners, cancelListeners;

    public TxForm() {
	this(FormStyle.WRITE_THROUGH);
    }

    public TxForm(Layout layout) {
	this(layout, new FieldFactory(), FormStyle.WRITE_THROUGH);
    }

    public TxForm(Layout layout, FormFieldFactory factory) {
	this(layout, factory, FormStyle.WRITE_THROUGH);
    }

    public TxForm(FormStyle style) {
	super();
	setFormFieldFactory(new FieldFactory());
	init(style);
    }

    public TxForm(Layout layout, FormStyle style) {
	this(layout, new FieldFactory(), style);
    }

    public TxForm(Layout layout, FormFieldFactory factory, FormStyle style) {
	super(layout, factory);
	init(style);
    }

    private void init(FormStyle style) {
	setImmediate(true);
	switch (style) {
	case WRITE_THROUGH:
	    setWriteThrough(true);
	    break;
	case SAVE_CLEAR_CANCEL:
	    setWriteThrough(false);
	    HorizontalLayout buttons = new HorizontalLayout();
	    buttons.setSpacing(true);
	    buttons.setMargin(true);
	    Button save = new Button("Save", new Button.ClickListener() {
		@Override
		public void buttonClick(ClickEvent event) {
		    txCommit();
		    notifyListeners(saveListeners, event);
		    getWindow().showNotification("successful commit", Notification.TYPE_TRAY_NOTIFICATION);
		    if (getWindow().isClosable() && getWindow().getParent() != null) {
			getWindow().getParent().removeWindow(getWindow());
		    }
		}
	    });
	    buttons.addComponent(save);
	    Button clear = new Button("Clear", new Button.ClickListener() {
		@Override
		public void buttonClick(ClickEvent event) {
		    discard();
		    notifyListeners(discardListeners, event);
		    getWindow().showNotification("cleared form", Notification.TYPE_TRAY_NOTIFICATION);
		}
	    });
	    buttons.addComponent(clear);
	    Button cancel = new Button("Cancel", new Button.ClickListener() {
		@Override
		public void buttonClick(ClickEvent event) {
		    notifyListeners(cancelListeners, event);
		    getWindow().showNotification("cancelled form", Notification.TYPE_TRAY_NOTIFICATION);
		    if (getWindow().isClosable()) {
			getWindow().getParent().removeWindow(getWindow());
		    }
		}
	    });
	    buttons.addComponent(cancel);
	    getFooter().addComponent(buttons);
	    break;
	default:
	    break;
	}
    }

    @Override
    public void setItemDataSource(Item newDataSource, Collection propertyIds) {
	super.setItemDataSource(newDataSource, propertyIds);
	if (newDataSource instanceof Buffered) {
	    ((Buffered) newDataSource).setWriteThrough(isWriteThrough());
	    ((Buffered) newDataSource).setReadThrough(isReadThrough());
	}
    }

    /**
     * @see com.vaadin.ui.Form#commit()
     */
    @Override
    public void commit() throws SourceException {
	try {
	    super.commit();
	} catch (SourceException e) {
	    for (Throwable cause : e.getCauses()) {
		if (cause instanceof jvstm.CommitException) {
		    throw (jvstm.CommitException) cause;
		}
		if (cause instanceof AbstractDomainObject.UnableToDetermineIdException) {
		    throw (AbstractDomainObject.UnableToDetermineIdException) cause;
		}
	    }
	    throw e;
	}
    }

    private void txCommit() {
	if (Transaction.isInTransaction()) {
	    Transaction.commit();
	}
	Transaction.withTransaction(new TransactionalCommand() {
	    @Override
	    public void doIt() {
		TxForm.this.commit();
		if (!isWriteThrough() && getItemDataSource() instanceof Buffered) {
		    ((Buffered)getItemDataSource()).commit();
		}
	    }
	});
	Transaction.begin(true);
    }

    public void addSaveListener(ClickListener listener) {
	if (saveListeners == null) {
	    saveListeners = new ArrayList<ClickListener>();
	}
	saveListeners.add(listener);
    }

    public void removeSaveListener(ClickListener listener) {
	if (saveListeners != null) {
	    saveListeners.remove(listener);
	}
    }

    public void addDiscardListener(ClickListener listener) {
	if (discardListeners == null) {
	    discardListeners = new ArrayList<ClickListener>();
	}
	discardListeners.add(listener);
    }

    public void removeDiscardListener(ClickListener listener) {
	if (discardListeners != null) {
	    discardListeners.remove(listener);
	}
    }

    public void addCancelListener(ClickListener listener) {
	if (cancelListeners == null) {
	    cancelListeners = new ArrayList<ClickListener>();
	}
	cancelListeners.add(listener);
    }

    public void removeCancelListener(ClickListener listener) {
	if (cancelListeners != null) {
	    cancelListeners.remove(listener);
	}
    }

    private void notifyListeners(Collection<ClickListener> listeners, ClickEvent event) {
	if (listeners != null) {
	    for (ClickListener listener : listeners) {
		listener.buttonClick(event);
	    }
	}
    }
}
