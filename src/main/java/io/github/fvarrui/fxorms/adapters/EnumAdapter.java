package io.github.fvarrui.fxorms.adapters;

import java.lang.reflect.Field;

import javafx.beans.property.Property;
import javafx.scene.control.ComboBox;

public class EnumAdapter extends ObjectAdapter<ComboBox<Object>, Property<Object>> {

	public EnumAdapter() {
		super(Enum.class);
	}

	@Override
	public ComboBox<Object> build(Field field) {
		ComboBox<Object> control = new ComboBox<>();
		control.getItems().setAll(getGenericTypes(field).get(0).getEnumConstants());
		control.setDisable(isReadOnly(field));
		return control;
	}

	@Override
	public void doBind(ComboBox<Object> control, Property<Object> property) {
		control.valueProperty().bindBidirectional(property);
	}

	@Override
	public void doUnbind(ComboBox<Object> control, Property<Object> property) {
		control.valueProperty().unbindBidirectional(property);
	}

}
