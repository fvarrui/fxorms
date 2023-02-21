package io.github.fvarrui.fxorms.components;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;

public class ListEditor extends BorderPane implements Initializable {

	// model

	private ObjectProperty<Object> selectedItem = new SimpleObjectProperty<>();
	private ListProperty<Object> items = new SimpleListProperty<>(FXCollections.observableArrayList());

	// view

	@FXML
	private Button removeButton;

	@FXML
	private ListView<Object> itemsList;

	@FXML
	private BorderPane view;

	public ListEditor() {
		super();
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/fxorms/components/ListEditor.fxml"));
			loader.setController(this);
			loader.setRoot(this);
			loader.load();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// bindings

		selectedItem.bind(itemsList.getSelectionModel().selectedItemProperty());
		removeButton.disableProperty().bind(selectedItem.isNull());
		itemsList.itemsProperty().bind(items);

	}

	@FXML
	void onRemoveAction(ActionEvent event) {
		items.remove(selectedItem.get());
	}

	public final ListProperty<Object> itemsProperty() {
		return this.items;
	}

	public final ObservableList<Object> getItems() {
		return this.itemsProperty().get();
	}

	public final void setItems(final ObservableList<Object> items) {
		this.itemsProperty().set(items);
	}

}
