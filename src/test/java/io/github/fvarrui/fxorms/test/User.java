package io.github.fvarrui.fxorms.test;

import java.time.LocalDateTime;
import java.util.Map;

import io.github.fvarrui.fxorms.annotations.ReadOnly;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.MapProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

public class User {

	@ReadOnly
	private IntegerProperty id = new SimpleIntegerProperty();

	private StringProperty name = new SimpleStringProperty();

	@ReadOnly
	private ObjectProperty<LocalDateTime> lastLogin = new SimpleObjectProperty<>();

	private MapProperty<String, Object> preferences = new SimpleMapProperty<>(FXCollections.observableHashMap());

	private ReadOnlyBooleanWrapper enabled = new ReadOnlyBooleanWrapper();

	public User(int id, String name, LocalDateTime lastLogin, Map<String, Object> preferences, Boolean enabled) {
		super();
		setId(id);
		setName(name);
		setLastLogin(lastLogin);
		getPreferences().putAll(preferences);
		this.enabled.set(true);
	}

	public final IntegerProperty idProperty() {
		return this.id;
	}

	public final int getId() {
		return this.idProperty().get();
	}

	public final void setId(final int id) {
		this.idProperty().set(id);
	}

	public final StringProperty nameProperty() {
		return this.name;
	}

	public final String getName() {
		return this.nameProperty().get();
	}

	public final void setName(final String name) {
		this.nameProperty().set(name);
	}

	public final ObjectProperty<LocalDateTime> lastLoginProperty() {
		return this.lastLogin;
	}

	public final LocalDateTime getLastLogin() {
		return this.lastLoginProperty().get();
	}

	public final void setLastLogin(final LocalDateTime lastLogin) {
		this.lastLoginProperty().set(lastLogin);
	}

	public final MapProperty<String, Object> preferencesProperty() {
		return this.preferences;
	}

	public final ObservableMap<String, Object> getPreferences() {
		return this.preferencesProperty().get();
	}

	public final void setPreferences(final ObservableMap<String, Object> preferences) {
		this.preferencesProperty().set(preferences);
	}

	public final javafx.beans.property.ReadOnlyBooleanProperty enabledProperty() {
		return this.enabled.getReadOnlyProperty();
	}

	public final boolean isEnabled() {
		return this.enabledProperty().get();
	}

}
