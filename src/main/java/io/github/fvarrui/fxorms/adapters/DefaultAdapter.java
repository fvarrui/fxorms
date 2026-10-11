package io.github.fvarrui.fxorms.adapters;

import io.github.fvarrui.fxorms.BeanProperty;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;

/**
 * Fallback adapter for properties no other adapter supports: shows the
 * property's value as text in a read-only {@link Label}. It is not registered;
 * {@link io.github.fvarrui.fxorms.FieldFactory} uses it when no adapter
 * matches.
 */
public class DefaultAdapter extends FieldAdapter<Label, ObservableValue<Object>> {

	/**
	 * Creates the fallback adapter.
	 */
	public DefaultAdapter() {
		super();
	}
	
	@Override
	public boolean isPropertyType(BeanProperty property) {
		return true;
	}

	@Override
	public Label build(BeanProperty property) {
		return new Label(property.getType().getCanonicalName());
	}

	@Override
	public boolean isReadOnly(BeanProperty property) {
		return true;
	}

	@Override
	public void doBind(Label control, ObservableValue<Object> property) {
		control.textProperty().bind(Bindings.concat(property));
	}

	@Override
	public void doUnbind(Label control, ObservableValue<Object> property) {
		control.textProperty().unbind();
	}

}
