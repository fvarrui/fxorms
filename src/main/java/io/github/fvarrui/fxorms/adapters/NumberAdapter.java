package io.github.fvarrui.fxorms.adapters;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;

import io.github.fvarrui.fxorms.annotations.Format;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyFloatWrapper;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

public class NumberAdapter extends FieldAdapter<TextField, Property<Number>> {

	public NumberAdapter() {
		super(DoubleProperty.class, LongProperty.class, IntegerProperty.class, FloatProperty.class);
	}

	@Override
	public TextField build(Field field) {
		TextField control = new TextField();
		control.setPrefColumnCount(5);
		control.setEditable(!isReadOnly(field));
		control.getProperties().put(NumberFormat.class, createFormat(field));
		return control;
	}

	@Override
	public boolean isReadOnly(Field field) {
		return 	super.isReadOnly(field) ||
				ReadOnlyDoubleWrapper.class.isAssignableFrom(field.getType()) ||
				ReadOnlyLongWrapper.class.isAssignableFrom(field.getType()) ||
				ReadOnlyIntegerWrapper.class.isAssignableFrom(field.getType()) ||
				ReadOnlyFloatWrapper.class.isAssignableFrom(field.getType());
	}

	@Override
	public void doBind(TextField control, Property<Number> property) {
		NumberFormat format = (NumberFormat) control.getProperties().get(NumberFormat.class);
		TextBinding.bind(control, property, new StrictNumberConverter(format));
	}

	@Override
	public void doUnbind(TextField control, Property<Number> property) {
		TextBinding.unbind(control);
	}

	private static NumberFormat createFormat(Field field) {
		Format format = field.getAnnotation(Format.class);
		if (format != null) {
			return new DecimalFormat(format.value());
		}
		boolean integer = IntegerProperty.class.isAssignableFrom(field.getType()) || LongProperty.class.isAssignableFrom(field.getType());
		if (integer) {
			NumberFormat integerFormat = NumberFormat.getIntegerInstance();
			integerFormat.setGroupingUsed(false);
			integerFormat.setParseIntegerOnly(true);
			return integerFormat;
		}
		return NumberFormat.getNumberInstance();
	}

	/**
	 * Unlike {@link javafx.util.converter.NumberStringConverter}, rejects empty
	 * text and text with trailing garbage ("12abc") instead of parsing a prefix.
	 */
	private static class StrictNumberConverter extends StringConverter<Number> {

		private final NumberFormat format;

		StrictNumberConverter(NumberFormat format) {
			this.format = format;
		}

		@Override
		public String toString(Number number) {
			return number == null ? "" : format.format(number);
		}

		@Override
		public Number fromString(String text) {
			String trimmed = text == null ? "" : text.trim();
			ParsePosition position = new ParsePosition(0);
			Number number = format.parse(trimmed, position);
			if (number == null || trimmed.isEmpty() || position.getIndex() != trimmed.length()) {
				throw new IllegalArgumentException("Not a valid number: " + text);
			}
			return number;
		}

	}

}
