package io.github.fvarrui.fxorms.adapters;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableValue;
import javafx.css.PseudoClass;
import javafx.scene.control.TextInputControl;
import javafx.util.StringConverter;

/**
 * Two-way binding between a text control and a property through a
 * {@link StringConverter}, with feedback on invalid input.
 * <p>
 * When the user types text the converter can't parse (its
 * {@code fromString} throws), the property is left unchanged and the control
 * gets the {@link #INVALID :invalid} pseudo-class, which the forms' default
 * stylesheet paints red. The mark is cleared when the text becomes valid or the
 * property changes. A read-only property (not a {@link WritableValue}) is only
 * shown, never written.
 * <p>
 * Unlike {@code Bindings.bindBidirectional(text, property, converter)}, which
 * only logs parse errors, this lets users see their mistakes. Adapters for text
 * input of non-string values should use it:
 * <pre>{@code
 * public void doBind(TextField control, ObservableValue<Number> property) {
 *     TextBinding.bind(control, property, converter);
 * }
 * public void doUnbind(TextField control, ObservableValue<Number> property) {
 *     TextBinding.unbind(control);
 * }
 * }</pre>
 *
 * @param <T> the property's value type
 */
public final class TextBinding<T> {

	/**
	 * Pseudo-class set on the control while its text can't be converted:
	 * {@code :invalid} in CSS.
	 */
	public static final PseudoClass INVALID = PseudoClass.getPseudoClass("invalid");

	private static final Object KEY = TextBinding.class;

	private final TextInputControl control;
	private final ObservableValue<T> property;
	private final StringConverter<T> converter;
	private final ChangeListener<String> textListener = (o, oldText, newText) -> onTextChanged(newText);
	private final ChangeListener<T> propertyListener = (o, oldValue, newValue) -> onPropertyChanged(newValue);
	private boolean updating = false;

	private TextBinding(TextInputControl control, ObservableValue<T> property, StringConverter<T> converter) {
		this.control = control;
		this.property = property;
		this.converter = converter;
	}

	/**
	 * Binds a text control to a property. Any previous {@code TextBinding} of the
	 * control is removed first. The control's text is set from the property's
	 * current value.
	 *
	 * @param <T> the property's value type
	 * @param control the text control
	 * @param property the property; written only if it's a {@link WritableValue}
	 * @param converter converts between text and values; its {@code fromString}
	 *                  must throw a {@link RuntimeException} for invalid text
	 */
	public static <T> void bind(TextInputControl control, ObservableValue<T> property, StringConverter<T> converter) {
		unbind(control);
		TextBinding<T> binding = new TextBinding<>(control, property, converter);
		control.getProperties().put(KEY, binding);
		binding.onPropertyChanged(property.getValue());
		control.textProperty().addListener(binding.textListener);
		property.addListener(binding.propertyListener);
	}

	/**
	 * Removes the {@code TextBinding} of a control, if any, and clears its
	 * {@link #INVALID} mark. The control keeps its current text.
	 *
	 * @param control the text control
	 */
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

	@SuppressWarnings("unchecked")
	private void onTextChanged(String text) {
		if (updating || !(property instanceof WritableValue)) {
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
			((WritableValue<T>) property).setValue(value);
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
