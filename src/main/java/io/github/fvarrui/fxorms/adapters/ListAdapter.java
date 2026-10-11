package io.github.fvarrui.fxorms.adapters;

import io.github.fvarrui.fxorms.BeanProperty;
import io.github.fvarrui.fxorms.components.ListEditor;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;

/**
 * Renders list properties as a {@link ListEditor}, which shows the items and
 * lets the user remove them from the bean's list. Disabled when the property is
 * read-only.
 */
public class ListAdapter extends FieldAdapter<ListEditor, ObservableValue<ObservableList<Object>>> {
	
	/**
	 * Creates the adapter for {@code ListProperty} and {@code ReadOnlyListProperty}.
	 */
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
