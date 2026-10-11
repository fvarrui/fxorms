package io.github.fvarrui.fxorms.examples;

import javafx.beans.property.ListProperty;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;

/**
 * Collections: list and map properties get editors that work on the bean's own
 * collections.
 */
public class Project {

	private final StringProperty name = new SimpleStringProperty();
	private final ListProperty<Object> tags = new SimpleListProperty<>(FXCollections.observableArrayList());
	private final MapProperty<String, Object> settings = new SimpleMapProperty<>(FXCollections.observableHashMap());

	public StringProperty nameProperty() { return name; }
	public ListProperty<Object> tagsProperty() { return tags; }
	public MapProperty<String, Object> settingsProperty() { return settings; }

}
