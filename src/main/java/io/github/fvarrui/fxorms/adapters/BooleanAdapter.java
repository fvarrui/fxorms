package io.github.fvarrui.fxorms.adapters;

import io.github.fvarrui.fxorms.BeanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;

/**
 * Renders boolean properties as a {@link CheckBox}, disabled when the property
 * is read-only.
 */
public class BooleanAdapter extends FieldAdapter<CheckBox, ObservableValue<Boolean>> {
	
	/**
	 * Creates the adapter for {@code BooleanProperty} and {@code ReadOnlyBooleanProperty}.
	 */
	public BooleanAdapter() {
		super(ReadOnlyBooleanProperty.class);
	}
	
	@Override
	public CheckBox build(BeanProperty property) {
		CheckBox control = new CheckBox(); 
		control.setDisable(isReadOnly(property));
		return control;
	}

	@Override
	public void doBind(CheckBox control, ObservableValue<Boolean> property) {
		bindValue(control.selectedProperty(), property);
	}

	@Override
	public void doUnbind(CheckBox control, ObservableValue<Boolean> property) {
		unbindValue(control.selectedProperty(), property);
	}
	
}
