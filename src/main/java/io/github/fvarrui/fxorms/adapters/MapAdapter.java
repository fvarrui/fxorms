package io.github.fvarrui.fxorms.adapters;

import java.lang.reflect.Field;

import io.github.fvarrui.fxorms.components.MapEditor;
import javafx.beans.property.MapProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableMap;

public class MapAdapter extends FieldAdapter<MapEditor, ObservableValue<ObservableMap<String, Object>>> {
	
	public MapAdapter() {
		super(MapProperty.class);
	}
	
	@Override
	public MapEditor build(Field field) {
		MapEditor editor = new MapEditor();
		editor.setDisable(isReadOnly(field));
		return editor;
	}

	@Override
	public void doBind(MapEditor control, ObservableValue<ObservableMap<String, Object>> property) {
		control.mapProperty().bind(property);
	}

	@Override
	public void doUnbind(MapEditor control, ObservableValue<ObservableMap<String, Object>> property) {
		control.mapProperty().unbind();
	}
	
}
