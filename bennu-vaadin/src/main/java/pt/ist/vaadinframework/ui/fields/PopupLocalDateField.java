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
package pt.ist.vaadinframework.ui.fields;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.vaadin.data.Property;
import com.vaadin.ui.PopupDateField;

/**
 * @author Pedro Santos (pedro.miguel.santos@ist.utl.pt)
 */
@SuppressWarnings("serial")
public class PopupLocalDateField extends PopupDateField {
	public static class LocalDateProperty implements Property {
		private final Property property;

		public LocalDateProperty(Property dataSource) {
			this.property = dataSource;
			if (!LocalDate.class.isAssignableFrom(dataSource.getType())) {
				throw new IllegalArgumentException("Can't use " + dataSource.getType().getName()
						+ " typed property as datasource");
			}
		}

		@Override
		public Object getValue() {
			LocalDate date = (LocalDate) property.getValue();
			return date == null ? null : date.toDateTimeAtStartOfDay().toDate();
		}

		@Override
		public void setValue(Object newValue) throws ReadOnlyException, ConversionException {
			Date date = (Date) newValue;
			if (date != null) {
				DateTime dateTime = new DateTime(date.getTime());
				property.setValue(new LocalDate().withYear(dateTime.getYear()).withMonthOfYear(dateTime.getMonthOfYear())
						.withDayOfMonth(dateTime.getDayOfMonth()));
			} else {
				property.setValue(null);
			}
		}

		@Override
		public Class<?> getType() {
			return Date.class;
		}

		@Override
		public boolean isReadOnly() {
			return property.isReadOnly();
		}

		@Override
		public void setReadOnly(boolean newStatus) {
			property.setReadOnly(newStatus);
		}
	}

	public PopupLocalDateField() {
		super();
	}

	public PopupLocalDateField(Property dataSource) throws IllegalArgumentException {
		super(new LocalDateProperty(dataSource));
	}

	public PopupLocalDateField(String caption, Property dataSource) {
		super(caption, new LocalDateProperty(dataSource));
	}

	public PopupLocalDateField(String caption) {
		super(caption);
	}

	@Override
	public void setPropertyDataSource(Property dataSource) {
		super.setPropertyDataSource(new LocalDateProperty(dataSource));
	}
}
