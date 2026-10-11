package io.github.fvarrui.fxorms.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Display and input format of a field: a {@link java.text.DecimalFormat}
 * pattern for numbers, a {@link java.time.format.DateTimeFormatter} pattern for
 * dates and times.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface Format {
	String value();
}
