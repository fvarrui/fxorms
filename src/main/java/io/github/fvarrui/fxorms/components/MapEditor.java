package io.github.fvarrui.fxorms.components;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.property.ListProperty;
import javafx.beans.property.MapProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener.Change;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.util.Pair;

public class MapEditor extends BorderPane implements Initializable {

	// model

	private MapProperty<String, Object> map = new SimpleMapProperty<>(FXCollections.observableHashMap());
	private ObjectProperty<Pair<String, Object>> selectedEntry = new SimpleObjectProperty<>();
	private ListProperty<Pair<String, Object>> entries = new SimpleListProperty<>(FXCollections.observableArrayList());

	// view

	@FXML
	private Button removeButton;

	@FXML
	private TableView<Pair<String, Object>> itemsTable;

	@FXML
	private TableColumn<Pair<String, Object>, String> nameColumn;

	@FXML
	private TableColumn<Pair<String, Object>, Object> valueColumn;

	@FXML
	private BorderPane view;

	public MapEditor() {
		super();
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/fxorms/components/MapEditor.fxml"));
			loader.setController(this);
			loader.setRoot(this);
			loader.load();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// cell factories

		nameColumn.setCellValueFactory(v -> new SimpleStringProperty(v.getValue().getKey()));
		valueColumn.setCellValueFactory(v -> new SimpleObjectProperty<>(v.getValue().getValue()));

		// bindings

		selectedEntry.bind(itemsTable.getSelectionModel().selectedItemProperty());
		removeButton.disableProperty().bind(selectedEntry.isNull());
		itemsTable.itemsProperty().bind(entries);
		
		// listeners
		
		map.addListener(this::onMapChanged);
		
	}

	private void onMapChanged(Change<? extends String, ? extends Object> change) {
		
		int index = change.wasRemoved() ? entries.indexOf(new Pair<>(change.getKey(), change.getValueRemoved())) : -1;
		Pair<String, Object> added = change.wasAdded() ? new Pair<>(change.getKey(), change.getValueAdded()) : null;
		
		if (added != null && index >= 0) {
			entries.set(index, added); // value replaced: keep its position
		} else if (added != null) {
			entries.add(added);
		} else if (index >= 0) {
			entries.remove(index);
		}
		
	}

	@FXML
	void onRemoveAction(ActionEvent event) {
		map.remove(selectedEntry.get().getKey());
	}

	public final MapProperty<String, Object> mapProperty() {
		return this.map;
	}

	public final ObservableMap<String, Object> getMap() {
		return this.mapProperty().get();
	}

	public final void setMap(final ObservableMap<String, Object> map) {
		this.mapProperty().set(map);
	}

}
