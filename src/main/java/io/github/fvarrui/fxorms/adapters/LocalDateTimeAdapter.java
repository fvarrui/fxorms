package io.github.fvarrui.fxorms.adapters;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.TextField;
import javafx.util.converter.LocalDateTimeStringConverter;

public class LocalDateTimeAdapter extends ObjectAdapter<TextField, Property<LocalDateTime>> {
	
	public LocalDateTimeAdapter() {
		super(LocalDateTime.class);
	}

	@Override
	public TextField build(Field field) {
		TextField control = new TextField();
		control.setPrefColumnCount(20);
		control.setEditable(!isReadOnly(field));				
		return control;
	}

	@Override
	public boolean isReadOnly(Field field) {
		return super.isReadOnly(field) || ReadOnlyObjectWrapper.class.isAssignableFrom(field.getType());
	}

	@Override
	public void doBind(TextField control, Property<LocalDateTime> property) {
		control.textProperty().bindBidirectional(property, new LocalDateTimeStringConverter());		
	}

	@Override
	public void doUnbind(TextField control, Property<LocalDateTime> property) {
		control.textProperty().unbindBidirectional(property);				
	}

}
