package io.github.fvarrui.fxorms.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Renders a string property as a multi-line {@link javafx.scene.control.TextArea}
 * instead of a single-line text field.
 * <p>
 * It can be placed on the property's accessor method or on its backing field
 * (the field with the same name).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface Multiline {
	/**
	 * Whether the text area is used.
	 *
	 * @return {@code false} to cancel the annotation
	 */
	boolean value() default true;
	/**
	 * Preferred number of visible rows of the text area.
	 *
	 * @return the number of rows
	 */
	int rows() default 5;
}
