package io.github.fvarrui.fxorms.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Text of the property's row label, instead of the one generated from its
 * name. The form appends a colon.
 * <p>
 * It can be placed on the property's accessor method or on its backing field
 * (the field with the same name).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface Label {
	/**
	 * The label text.
	 *
	 * @return the label text, without the trailing colon
	 */
	String value();
}
