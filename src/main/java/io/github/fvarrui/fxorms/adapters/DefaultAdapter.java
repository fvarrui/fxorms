package io.github.fvarrui.fxorms.adapters;

import java.lang.reflect.Field;

import javafx.beans.binding.Bindings;
import javafx.beans.property.Property;
import javafx.scene.control.Label;

public class DefaultAdapter extends FieldAdapter<Label, Property<Object>> {
	
	@Override
	public boolean isPropertyType(Field field) {
		return true;
	}

	@Override
	public Label build(Field field) {
		return new Label(field.getType().getCanonicalName());
	}

	@Override
	public boolean isReadOnly(Field field) {
		return true;
	}

	@Override
	public void doBind(Label control, Property<Object> property) {
		control.textProperty().bind(Bindings.concat(property));
	}

	@Override
	public void doUnbind(Label control, Property<Object> property) {
		control.textProperty().unbind();
	}

}
