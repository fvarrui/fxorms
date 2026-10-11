package io.github.fvarrui.fxorms.adapters;

import io.github.fvarrui.fxorms.BeanProperty;
import io.github.fvarrui.fxorms.components.MapEditor;
import javafx.beans.property.ReadOnlyMapProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableMap;

/**
 * Renders map properties as a {@link MapEditor}, which shows the entries in a
 * table and lets the user remove them from the bean's map. Disabled when the
 * property is read-only.
 */
public class MapAdapter extends FieldAdapter<MapEditor, ObservableValue<ObservableMap<String, Object>>> {
	
	/**
	 * Creates the adapter for {@code MapProperty} and {@code ReadOnlyMapProperty}.
	 */
	public MapAdapter() {
		super(ReadOnlyMapProperty.class);
	}
	
	@Override
	public MapEditor build(BeanProperty property) {
		MapEditor editor = new MapEditor();
		editor.setDisable(isReadOnly(property));
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
