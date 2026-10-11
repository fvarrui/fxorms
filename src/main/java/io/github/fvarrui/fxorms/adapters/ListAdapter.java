package io.github.fvarrui.fxorms.adapters;

import io.github.fvarrui.fxorms.BeanProperty;

import io.github.fvarrui.fxorms.components.ListEditor;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;

public class ListAdapter extends FieldAdapter<ListEditor, ObservableValue<ObservableList<Object>>> {
	
	public ListAdapter() {
		super(ReadOnlyListProperty.class);
	}
	
	@Override
	public ListEditor build(BeanProperty property) {
		ListEditor editor = new ListEditor();
		editor.setDisable(isReadOnly(property));
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
