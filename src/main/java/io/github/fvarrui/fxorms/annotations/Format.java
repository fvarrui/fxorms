package io.github.fvarrui.fxorms.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Display and input format of a property: a {@link java.text.DecimalFormat}
 * pattern for numbers (e.g. {@code "#,##0.00"}), a
 * {@link java.time.format.DateTimeFormatter} pattern for date-times and dates
 * (e.g. {@code "yyyy-MM-dd HH:mm"}).
 * <p>
 * It can be placed on the property's accessor method or on its backing field
 * (the field with the same name).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface Format {
	/**
	 * The pattern.
	 *
	 * @return the number or date-time pattern
	 */
	String value();
}
