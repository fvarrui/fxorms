package io.github.fvarrui.fxorms.adapters;

import java.lang.reflect.Field;

import io.github.fvarrui.fxorms.annotations.Multiline;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;

public class StringAdapter extends FieldAdapter<TextInputControl, Property<String>> {
	
	public StringAdapter() {
		super(StringProperty.class);
	}
	
	@Override
	public TextInputControl build(Field field) {
		Multiline multiline = field.getAnnotation(Multiline.class);
		TextInputControl control;
		if (multiline != null && multiline.value()) {
			TextArea textArea = new TextArea();
			textArea.setPrefColumnCount(50);
			textArea.setPrefRowCount(multiline.rows());
			control = textArea;
		} else {
			TextField textField = new TextField();
			textField.setPrefColumnCount(50);
			control = textField;
		}
		control.setEditable(!isReadOnly(field));
		return control;
	}

	@Override
	public boolean isReadOnly(Field field) {
		return super.isReadOnly(field) || ReadOnlyStringWrapper.class.isAssignableFrom(field.getType());
	}

	@Override
	public void doBind(TextInputControl control, Property<String> property) {
		control.textProperty().bindBidirectional(property);
	}

	@Override
	public void doUnbind(TextInputControl control, Property<String> property) {
		control.textProperty().unbindBidirectional(property);
	}
	
}
