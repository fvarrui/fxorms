package io.github.fvarrui.fxorms.adapters;

import java.lang.reflect.Field;

import io.github.fvarrui.fxorms.components.ListEditor;
import javafx.beans.property.ListProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;

public class ListAdapter extends FieldAdapter<ListEditor, ObservableValue<ObservableList<Object>>> {
	
	public ListAdapter() {
		super(ListProperty.class);
	}
	
	@Override
	public ListEditor build(Field field) {
		ListEditor editor = new ListEditor();
		editor.setDisable(isReadOnly(field));
		return editor;
	}

	@Override
	public void doBind(ListEditor control, ObservableValue<ObservableList<Object>> observable) {
		control.itemsProperty().bind(observable);
	}

	@Override
	public void doUnbind(ListEditor control, ObservableValue<ObservableList<Object>> observable) {
		control.itemsProperty().unbind();		
	}
	
}
