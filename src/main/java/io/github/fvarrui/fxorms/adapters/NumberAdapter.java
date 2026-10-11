package io.github.fvarrui.fxorms.adapters;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;

import io.github.fvarrui.fxorms.BeanProperty;
import io.github.fvarrui.fxorms.annotations.Format;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyFloatProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyLongProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

/**
 * Renders numeric properties ({@code int}, {@code long}, {@code float} and
 * {@code double}) as a {@link TextField}.
 * <p>
 * Integers are shown without grouping separators (12345, not 12,345); decimals
 * use the default locale's number format. {@link Format} sets a
 * {@link DecimalFormat} pattern instead. Text that isn't a complete number
 * (empty, "abc", "12abc", or "3.7" for an integer) marks the field as invalid
 * and leaves the property unchanged (see {@link TextBinding}).
 */
public class NumberAdapter extends FieldAdapter<TextField, ObservableValue<Number>> {

	/**
	 * Creates the adapter for integer, long, float and double properties.
	 */
	public NumberAdapter() {
		super(ReadOnlyDoubleProperty.class, ReadOnlyLongProperty.class, ReadOnlyIntegerProperty.class, ReadOnlyFloatProperty.class);
	}

	@Override
	public TextField build(BeanProperty property) {
		TextField control = new TextField();
		control.setPrefColumnCount(5);
		control.setEditable(!isReadOnly(property));
		control.getProperties().put(NumberFormat.class, createFormat(property));
		return control;
	}

	@Override
	public void doBind(TextField control, ObservableValue<Number> property) {
		NumberFormat format = (NumberFormat) control.getProperties().get(NumberFormat.class);
		TextBinding.bind(control, property, new StrictNumberConverter(format));
	}

	@Override
	public void doUnbind(TextField control, ObservableValue<Number> property) {
		TextBinding.unbind(control);
	}

	private static NumberFormat createFormat(BeanProperty property) {
		Format format = property.getAnnotation(Format.class);
		if (format != null) {
			return new DecimalFormat(format.value());
		}
		boolean integer = ReadOnlyIntegerProperty.class.isAssignableFrom(property.getType()) || ReadOnlyLongProperty.class.isAssignableFrom(property.getType());
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
