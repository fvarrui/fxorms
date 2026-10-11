package io.github.fvarrui.fxorms.adapters;

import io.github.fvarrui.fxorms.BeanProperty;
import io.github.fvarrui.fxorms.annotations.Multiline;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;

public class StringAdapter extends FieldAdapter<TextInputControl, ObservableValue<String>> {
	
	public StringAdapter() {
		super(ReadOnlyStringProperty.class);
	}
	
	@Override
	public TextInputControl build(BeanProperty property) {
		Multiline multiline = property.getAnnotation(Multiline.class);
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
		control.setEditable(!isReadOnly(property));
		return control;
	}

	@Override
	public void doBind(TextInputControl control, ObservableValue<String> property) {
		bindValue(control.textProperty(), property);
	}

	@Override
	public void doUnbind(TextInputControl control, ObservableValue<String> property) {
		unbindValue(control.textProperty(), property);
	}
	
}
