package io.github.fvarrui.fxorms.adapters;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import io.github.fvarrui.fxorms.annotations.Format;
import javafx.beans.property.Property;
import javafx.scene.control.DatePicker;
import javafx.util.converter.LocalDateStringConverter;

public class LocalDateAdapter extends ObjectAdapter<DatePicker, Property<LocalDate>> {

	public LocalDateAdapter() {
		super(LocalDate.class);
	}

	@Override
	public DatePicker build(Field field) {
		DatePicker control = new DatePicker();
		Format format = field.getAnnotation(Format.class);
		if (format != null) {
			control.setConverter(new LocalDateStringConverter(DateTimeFormatter.ofPattern(format.value()), null));
		}
		control.setDisable(isReadOnly(field));
		return control;
	}

	@Override
	public void doBind(DatePicker control, Property<LocalDate> property) {
		control.valueProperty().bindBidirectional(property);
	}

	@Override
	public void doUnbind(DatePicker control, Property<LocalDate> property) {
		control.valueProperty().unbindBidirectional(property);
	}

}
