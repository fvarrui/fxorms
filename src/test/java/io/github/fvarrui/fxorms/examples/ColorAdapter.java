package io.github.fvarrui.fxorms.examples;

import io.github.fvarrui.fxorms.BeanProperty;
import io.github.fvarrui.fxorms.adapters.ObjectAdapter;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;

/**
 * Custom adapter for a type fxorms doesn't support: {@code ObjectProperty<Color>}
 * edited with a {@link ColorPicker}.
 */
public class ColorAdapter extends ObjectAdapter<ColorPicker, ObservableValue<Color>> {

	public ColorAdapter() {
		super(Color.class);
	}

	@Override
	public ColorPicker build(BeanProperty property) {
		ColorPicker control = new ColorPicker();
		control.setDisable(isReadOnly(property));
		return control;
	}

	@Override
	public void doBind(ColorPicker control, ObservableValue<Color> property) {
		bindValue(control.valueProperty(), property);
	}

	@Override
	public void doUnbind(ColorPicker control, ObservableValue<Color> property) {
		unbindValue(control.valueProperty(), property);
	}

}
