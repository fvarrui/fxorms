package io.github.fvarrui.fxorms.adapters;

import io.github.fvarrui.fxorms.BeanProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ComboBox;

public class EnumAdapter extends ObjectAdapter<ComboBox<Object>, ObservableValue<Object>> {

	public EnumAdapter() {
		super(Enum.class);
	}

	@Override
	public ComboBox<Object> build(BeanProperty property) {
		ComboBox<Object> control = new ComboBox<>();
		control.getItems().setAll(property.getGenericTypes().get(0).getEnumConstants());
		control.setDisable(isReadOnly(property));
		return control;
	}

	@Override
	public void doBind(ComboBox<Object> control, ObservableValue<Object> property) {
		bindValue(control.valueProperty(), property);
	}

	@Override
	public void doUnbind(ComboBox<Object> control, ObservableValue<Object> property) {
		unbindValue(control.valueProperty(), property);
	}

}
