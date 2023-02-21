package io.github.fvarrui.fxorms.test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.github.fvarrui.fxorms.annotations.Multiline;
import io.github.fvarrui.fxorms.annotations.ReadOnly;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.MapProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.image.Image;

public class User {

	@ReadOnly
	private StringProperty id = new SimpleStringProperty();

	private DoubleProperty coins = new SimpleDoubleProperty();

	@Multiline
	private StringProperty displayName = new SimpleStringProperty();

	@ReadOnly
	private ObjectProperty<LocalDateTime> lastLogin = new SimpleObjectProperty<>();

	private ObjectProperty<Image> photo = new SimpleObjectProperty<>();

	private MapProperty<String, Object> preferences = new SimpleMapProperty<>(FXCollections.observableHashMap());

	private DoubleProperty rating = new SimpleDoubleProperty();

	private ListProperty<String> reserved = new SimpleListProperty<>(FXCollections.observableArrayList());

	private ObjectProperty<UUID> shared = new SimpleObjectProperty<>();

	private ReadOnlyStringWrapper name = new ReadOnlyStringWrapper();

	private ReadOnlyStringWrapper email = new ReadOnlyStringWrapper();

	private ReadOnlyBooleanWrapper enabled = new ReadOnlyBooleanWrapper();

	public User(String id, Double coins, String displayName,
			LocalDateTime lastLogin, Image photo,
			Map<String, Object> preferences, Double rating, List<String> reserved,
			UUID shared, String name, String email,
			Boolean enabled) {
		super();
		setId(id);
		setCoins(coins);
		setDisplayName(displayName);
		setLastLogin(lastLogin);
		setPhoto(photo);
		getPreferences().putAll(preferences);
		setRating(rating);
		getReserved().setAll(reserved);
		setShared(shared);		
		this.name.set(name);
		this.email.set(email);
		this.enabled.set(true);
	}

	public final StringProperty idProperty() {
		return this.id;
	}

	public final String getId() {
		return this.idProperty().get();
	}

	public final void setId(final String id) {
		this.idProperty().set(id);
	}

	public final DoubleProperty coinsProperty() {
		return this.coins;
	}

	public final double getCoins() {
		return this.coinsProperty().get();
	}

	public final void setCoins(final double coins) {
		this.coinsProperty().set(coins);
	}

	public final StringProperty displayNameProperty() {
		return this.displayName;
	}

	public final String getDisplayName() {
		return this.displayNameProperty().get();
	}

	public final void setDisplayName(final String displayName) {
		this.displayNameProperty().set(displayName);
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

	public final ObjectProperty<Image> photoProperty() {
		return this.photo;
	}

	public final Image getPhoto() {
		return this.photoProperty().get();
	}

	public final void setPhoto(final Image photo) {
		this.photoProperty().set(photo);
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

	public final DoubleProperty ratingProperty() {
		return this.rating;
	}

	public final double getRating() {
		return this.ratingProperty().get();
	}

	public final void setRating(final double rating) {
		this.ratingProperty().set(rating);
	}

	public final ListProperty<String> reservedProperty() {
		return this.reserved;
	}

	public final ObservableList<String> getReserved() {
		return this.reservedProperty().get();
	}

	public final void setReserved(final ObservableList<String> reserved) {
		this.reservedProperty().set(reserved);
	}

	public final ObjectProperty<UUID> sharedProperty() {
		return this.shared;
	}

	public final UUID getShared() {
		return this.sharedProperty().get();
	}

	public final void setShared(final UUID shared) {
		this.sharedProperty().set(shared);
	}

	public final javafx.beans.property.ReadOnlyStringProperty nameProperty() {
		return this.name.getReadOnlyProperty();
	}

	public final String getName() {
		return this.nameProperty().get();
	}

	public final javafx.beans.property.ReadOnlyStringProperty emailProperty() {
		return this.email.getReadOnlyProperty();
	}

	public final String getEmail() {
		return this.emailProperty().get();
	}

	public final javafx.beans.property.ReadOnlyBooleanProperty enabledProperty() {
		return this.enabled.getReadOnlyProperty();
	}

	public final boolean isEnabled() {
		return this.enabledProperty().get();
	}

}
