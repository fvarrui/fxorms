package io.github.fvarrui.fxorms.adapters;

import java.lang.reflect.Field;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.scene.control.CheckBox;

public class BooleanAdapter extends FieldAdapter<CheckBox, Property<Boolean>> {
	
	public BooleanAdapter() {
		super(BooleanProperty.class, ReadOnlyBooleanWrapper.class);
	}
	
	@Override
	public CheckBox build(Field field) {
		CheckBox control = new CheckBox(); 
		control.setDisable(isReadOnly(field));
		return control;
	}

	@Override
	public boolean isReadOnly(Field field) {
		return super.isReadOnly(field) || ReadOnlyBooleanWrapper.class.isAssignableFrom(field.getType());
	}

	@Override
	public void doBind(CheckBox control, Property<Boolean> property) {
		control.selectedProperty().bindBidirectional(property);
	}

	@Override
	public void doUnbind(CheckBox control, Property<Boolean> property) {
		control.selectedProperty().unbindBidirectional(property);		
	}
	
}
