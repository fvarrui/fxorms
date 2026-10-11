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

/**
 * Control that shows the items of a list and lets the user remove them. Used by
 * {@link io.github.fvarrui.fxorms.adapters.ListAdapter}.
 * <p>
 * The editor works on the list itself: when {@link #itemsProperty()} is bound
 * to a bean's list property, removing an item removes it from the bean's
 * list.
 */
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

	/**
	 * Creates an empty editor, loading its layout from
	 * {@code /fxml/fxorms/components/ListEditor.fxml}.
	 *
	 * @throws RuntimeException if the layout can't be loaded
	 */
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

	/**
	 * Sets up the bindings between the list view, the remove button and the items.
	 * Called by the {@link FXMLLoader}; not meant to be called directly.
	 *
	 * @param location the location of the FXML file
	 * @param resources the resource bundle, or {@code null}
	 */
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

	/**
	 * The items shown by the editor.
	 *
	 * @return the items property
	 */
	public final ListProperty<Object> itemsProperty() {
		return this.items;
	}

	/**
	 * Gets the items shown by the editor.
	 *
	 * @return the items
	 */
	public final ObservableList<Object> getItems() {
		return this.itemsProperty().get();
	}

	/**
	 * Sets the items shown by the editor.
	 *
	 * @param items the items
	 */
	public final void setItems(final ObservableList<Object> items) {
		this.itemsProperty().set(items);
	}

}
