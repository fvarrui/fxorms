package io.github.fvarrui.fxorms.adapters;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import io.github.fvarrui.fxorms.BeanProperty;
import io.github.fvarrui.fxorms.annotations.Format;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.DatePicker;
import javafx.util.converter.LocalDateStringConverter;

/**
 * Renders {@code ObjectProperty<LocalDate>} as a {@link DatePicker}, disabled
 * when the property is read-only. A {@link Format} annotation sets the
 * {@link DateTimeFormatter} pattern of the picker's text.
 */
public class LocalDateAdapter extends ObjectAdapter<DatePicker, ObservableValue<LocalDate>> {

	/**
	 * Creates the adapter for {@code LocalDate} object properties.
	 */
	public LocalDateAdapter() {
		super(LocalDate.class);
	}

	@Override
	public DatePicker build(BeanProperty property) {
		DatePicker control = new DatePicker();
		Format format = property.getAnnotation(Format.class);
		if (format != null) {
			control.setConverter(new LocalDateStringConverter(DateTimeFormatter.ofPattern(format.value()), null));
		}
		control.setDisable(isReadOnly(property));
		return control;
	}

	@Override
	public void doBind(DatePicker control, ObservableValue<LocalDate> property) {
		bindValue(control.valueProperty(), property);
	}

	@Override
	public void doUnbind(DatePicker control, ObservableValue<LocalDate> property) {
		unbindValue(control.valueProperty(), property);
	}

}
