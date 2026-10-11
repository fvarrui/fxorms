package com.example.model;

import java.util.List;
import javafx.beans.property.*;
import javafx.collections.FXCollections;

public class Person {
	private final StringProperty name = new SimpleStringProperty("Ada");
	private final IntegerProperty age = new SimpleIntegerProperty(36);
	private final ListProperty<Object> tags = new SimpleListProperty<>(FXCollections.observableArrayList(List.of("x")));
	public StringProperty nameProperty() { return name; }
	public IntegerProperty ageProperty() { return age; }
	public ListProperty<Object> tagsProperty() { return tags; }
}
