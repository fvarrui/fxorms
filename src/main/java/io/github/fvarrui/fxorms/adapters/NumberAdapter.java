package io.github.fvarrui.fxorms.adapters;

import java.lang.reflect.Field;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyFloatWrapper;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.scene.control.TextField;
import javafx.util.converter.NumberStringConverter;

public class NumberAdapter extends FieldAdapter<TextField, Property<Number>> {
	
	public NumberAdapter() {
		super(DoubleProperty.class, LongProperty.class, IntegerProperty.class, FloatProperty.class);
	}
	
	@Override
	public TextField build(Field field) {
		TextField control = new TextField();
		control.setPrefColumnCount(5);
		control.setEditable(!isReadOnly(field));
		return control;
	}

	@Override
	public boolean isReadOnly(Field field) {
		return (ReadOnlyDoubleWrapper.class.isAssignableFrom(field.getType()) ||
				ReadOnlyLongWrapper.class.isAssignableFrom(field.getType()) ||
				ReadOnlyIntegerWrapper.class.isAssignableFrom(field.getType()) ||
				ReadOnlyFloatWrapper.class.isAssignableFrom(field.getType()));
	}

	@Override
	public void doBind(TextField control, Property<Number> property) {
		control.textProperty().bindBidirectional(property, new NumberStringConverter());
	}

	@Override
	public void doUnbind(TextField control, Property<Number> property) {
		control.textProperty().unbindBidirectional(property);		
	}
	
}
