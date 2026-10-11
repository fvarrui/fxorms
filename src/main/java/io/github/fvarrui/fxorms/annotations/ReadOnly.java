package io.github.fvarrui.fxorms.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Makes a property read-only in the form: its control shows the value but can't
 * be edited. Text controls become non-editable; other controls are disabled.
 * <p>
 * Properties whose accessor returns only a {@code ReadOnlyXxxProperty} are
 * always read-only; this annotation is for writable properties the form must
 * not change.
 * <p>
 * It can be placed on the property's accessor method or on its backing field
 * (the field with the same name).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface ReadOnly {
	/**
	 * Whether the property is read-only.
	 *
	 * @return {@code false} to cancel the annotation
	 */
	boolean value() default true;
}
