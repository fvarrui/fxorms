package io.github.fvarrui.fxorms.adapters;

import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.css.PseudoClass;
import javafx.scene.control.TextInputControl;
import javafx.util.StringConverter;

/**
 * Bidirectional binding between a text control and a property through a
 * converter. Text the converter can't parse (it throws) leaves the property
 * unchanged and sets the {@code :invalid} pseudo-class on the control.
 */
public final class TextBinding<T> {

	public static final PseudoClass INVALID = PseudoClass.getPseudoClass("invalid");

	private static final Object KEY = TextBinding.class;

	private final TextInputControl control;
	private final Property<T> property;
	private final StringConverter<T> converter;
	private final ChangeListener<String> textListener = (o, oldText, newText) -> onTextChanged(newText);
	private final ChangeListener<T> propertyListener = (o, oldValue, newValue) -> onPropertyChanged(newValue);
	private boolean updating = false;

	private TextBinding(TextInputControl control, Property<T> property, StringConverter<T> converter) {
		this.control = control;
		this.property = property;
		this.converter = converter;
	}

	public static <T> void bind(TextInputControl control, Property<T> property, StringConverter<T> converter) {
		unbind(control);
		TextBinding<T> binding = new TextBinding<>(control, property, converter);
		control.getProperties().put(KEY, binding);
		binding.onPropertyChanged(property.getValue());
		control.textProperty().addListener(binding.textListener);
		property.addListener(binding.propertyListener);
	}

	public static void unbind(TextInputControl control) {
		Object binding = control.getProperties().remove(KEY);
		if (binding != null) {
			((TextBinding<?>) binding).dispose();
		}
	}

	private void dispose() {
		control.textProperty().removeListener(textListener);
		property.removeListener(propertyListener);
		control.pseudoClassStateChanged(INVALID, false);
	}

	private void onTextChanged(String text) {
		if (updating) {
			return;
		}
		T value;
		try {
			value = converter.fromString(text);
		} catch (RuntimeException e) {
			control.pseudoClassStateChanged(INVALID, true);
			return;
		}
		updating = true;
		try {
			property.setValue(value);
			control.pseudoClassStateChanged(INVALID, false);
		} finally {
			updating = false;
		}
	}

	private void onPropertyChanged(T value) {
		if (updating) {
			return;
		}
		updating = true;
		try {
			control.setText(converter.toString(value));
			control.pseudoClassStateChanged(INVALID, false);
		} finally {
			updating = false;
		}
	}

}
