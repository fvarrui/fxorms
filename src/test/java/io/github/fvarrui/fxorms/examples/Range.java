package io.github.fvarrui.fxorms.examples;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation: numeric properties annotated with it are rendered as a
 * slider by {@link SliderAdapter}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface Range {
	double min();
	double max();
}
