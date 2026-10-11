package io.github.fvarrui.fxorms.adapters;

import java.time.LocalDateTime;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.util.Locale;

import io.github.fvarrui.fxorms.BeanProperty;
import io.github.fvarrui.fxorms.annotations.Format;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

/**
 * Renders {@code ObjectProperty<LocalDateTime>} as a {@link TextField}.
 * <p>
 * Values use the default locale's MEDIUM format, which includes seconds (e.g.
 * "Jan 2, 2024, 3:04:05 AM"), or the {@link java.time.format.DateTimeFormatter}
 * pattern of a {@link Format} annotation. Parsing ignores case, and plain spaces
 * are accepted where the locale's format uses no-break spaces. Empty text sets
 * the value to {@code null}; text that can't be parsed marks the field as
 * invalid and leaves the property unchanged (see {@link TextBinding}).
 */
public class LocalDateTimeAdapter extends ObjectAdapter<TextField, ObservableValue<LocalDateTime>> {

	/**
	 * Creates the adapter for {@code LocalDateTime} object properties.
	 */
	public LocalDateTimeAdapter() {
		super(LocalDateTime.class);
	}

	@Override
	public TextField build(BeanProperty property) {
		TextField control = new TextField();
		control.setPrefColumnCount(20);
		control.setEditable(!isReadOnly(property));
		control.getProperties().put(DateTimeFormatter.class, createFormatter(property));
		return control;
	}

	@Override
	public void doBind(TextField control, ObservableValue<LocalDateTime> property) {
		DateTimeFormatter formatter = (DateTimeFormatter) control.getProperties().get(DateTimeFormatter.class);
		TextBinding.bind(control, property, new LocalDateTimeConverter(formatter));
	}

	@Override
	public void doUnbind(TextField control, ObservableValue<LocalDateTime> property) {
		TextBinding.unbind(control);
	}

	/**
	 * The {@link Format} pattern if present; otherwise the locale's MEDIUM
	 * format, which includes seconds (SHORT drops them).
	 */
	private static DateTimeFormatter createFormatter(BeanProperty property) {
		Format format = property.getAnnotation(Format.class);
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
