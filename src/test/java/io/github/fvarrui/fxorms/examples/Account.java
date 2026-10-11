package io.github.fvarrui.fxorms.examples;

import java.time.LocalDateTime;

import io.github.fvarrui.fxorms.annotations.ReadOnly;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Read-only properties: {@code @ReadOnly} on a writable property, and accessors
 * that only expose a read-only view.
 */
public class Account {

	public enum Status { ACTIVE, SUSPENDED, CLOSED }

	@ReadOnly
	private final IntegerProperty id = new SimpleIntegerProperty();
	private final StringProperty username = new SimpleStringProperty();
	private final ObjectProperty<Status> status = new SimpleObjectProperty<>(Status.ACTIVE);
	private final ReadOnlyObjectWrapper<LocalDateTime> createdAt = new ReadOnlyObjectWrapper<>();
	private final ReadOnlyIntegerWrapper logins = new ReadOnlyIntegerWrapper();

	public Account(int id, String username, LocalDateTime createdAt) {
		this.id.set(id);
		this.username.set(username);
		this.createdAt.set(createdAt);
	}

	public IntegerProperty idProperty() { return id; }
	public StringProperty usernameProperty() { return username; }
	public ObjectProperty<Status> statusProperty() { return status; }
	public ReadOnlyObjectProperty<LocalDateTime> createdAtProperty() { return createdAt.getReadOnlyProperty(); }
	public ReadOnlyIntegerProperty loginsProperty() { return logins.getReadOnlyProperty(); }

	/** Business logic changes read-only values; the form follows them. */
	public void login() {
		logins.set(logins.get() + 1);
	}

}
