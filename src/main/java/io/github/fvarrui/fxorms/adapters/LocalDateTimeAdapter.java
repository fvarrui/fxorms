package io.github.fvarrui.fxorms.adapters;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.util.Locale;

import io.github.fvarrui.fxorms.annotations.Format;
import javafx.beans.property.Property;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

public class LocalDateTimeAdapter extends ObjectAdapter<TextField, Property<LocalDateTime>> {

	public LocalDateTimeAdapter() {
		super(LocalDateTime.class);
	}

	@Override
	public TextField build(Field field) {
		TextField control = new TextField();
		control.setPrefColumnCount(20);
		control.setEditable(!isReadOnly(field));
		control.getProperties().put(DateTimeFormatter.class, createFormatter(field));
		return control;
	}

	@Override
	public void doBind(TextField control, Property<LocalDateTime> property) {
		DateTimeFormatter formatter = (DateTimeFormatter) control.getProperties().get(DateTimeFormatter.class);
		TextBinding.bind(control, property, new LocalDateTimeConverter(formatter));
	}

	@Override
	public void doUnbind(TextField control, Property<LocalDateTime> property) {
		TextBinding.unbind(control);
	}

	/**
	 * The {@link Format} pattern if present; otherwise the locale's MEDIUM
	 * format, which includes seconds (SHORT drops them).
	 */
	private static DateTimeFormatter createFormatter(Field field) {
		Format format = field.getAnnotation(Format.class);
		Locale locale = Locale.getDefault(Locale.Category.FORMAT);
		String pattern = format != null ? format.value()
				: DateTimeFormatterBuilder.getLocalizedDateTimePattern(FormatStyle.MEDIUM, FormatStyle.MEDIUM, IsoChronology.INSTANCE, locale);
		// CLDR patterns use narrow no-break spaces (e.g. before "AM"), which users can't type
		pattern = pattern.replace(' ', ' ').replace(' ', ' ');
		return new DateTimeFormatterBuilder()
				.parseCaseInsensitive()
				.appendPattern(pattern)
				.toFormatter(locale);
	}

	private static class LocalDateTimeConverter extends StringConverter<LocalDateTime> {

		private final DateTimeFormatter formatter;

		LocalDateTimeConverter(DateTimeFormatter formatter) {
			this.formatter = formatter;
		}

		@Override
		public String toString(LocalDateTime value) {
			return value == null ? "" : formatter.format(value);
		}

		@Override
		public LocalDateTime fromString(String text) {
			String trimmed = text == null ? "" : text.trim();
			return trimmed.isEmpty() ? null : LocalDateTime.parse(trimmed, formatter);
		}

	}

}
