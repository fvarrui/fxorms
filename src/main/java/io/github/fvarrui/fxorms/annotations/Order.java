package io.github.fvarrui.fxorms.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Position of the property's row. Rows with an order come first, sorted by
 * value; the rest follow the declaration order of their backing fields.
 * <p>
 * It can be placed on the property's accessor method or on its backing field
 * (the field with the same name).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface Order {
	/**
	 * The position; lower values come first.
	 *
	 * @return the position
	 */
	int value();
}
