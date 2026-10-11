package io.github.fvarrui.fxorms.examples;

import java.time.LocalDate;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Quick start: a plain JavaFX bean. Only the {@code xxxProperty()} accessors are
 * needed by fxorms; getters and setters are for the rest of the application.
 */
public class Person {

	private final StringProperty firstName = new SimpleStringProperty();
	private final StringProperty lastName = new SimpleStringProperty();
	private final StringProperty email = new SimpleStringProperty();
	private final ObjectProperty<LocalDate> birthDate = new SimpleObjectProperty<>();
	private final BooleanProperty subscribed = new SimpleBooleanProperty();

	public Person() {
	}

	public Person(String firstName, String lastName, String email, LocalDate birthDate, boolean subscribed) {
		setFirstName(firstName);
		setLastName(lastName);
		setEmail(email);
		setBirthDate(birthDate);
		setSubscribed(subscribed);
	}

	public StringProperty firstNameProperty() { return firstName; }
	public StringProperty lastNameProperty() { return lastName; }
	public StringProperty emailProperty() { return email; }
	public ObjectProperty<LocalDate> birthDateProperty() { return birthDate; }
	public BooleanProperty subscribedProperty() { return subscribed; }

	public String getFirstName() { return firstName.get(); }
	public void setFirstName(String value) { firstName.set(value); }
	public String getLastName() { return lastName.get(); }
	public void setLastName(String value) { lastName.set(value); }
	public String getEmail() { return email.get(); }
	public void setEmail(String value) { email.set(value); }
	public LocalDate getBirthDate() { return birthDate.get(); }
	public void setBirthDate(LocalDate value) { birthDate.set(value); }
	public boolean isSubscribed() { return subscribed.get(); }
	public void setSubscribed(boolean value) { subscribed.set(value); }

	public void copyFrom(Person other) {
		setFirstName(other.getFirstName());
		setLastName(other.getLastName());
		setEmail(other.getEmail());
		setBirthDate(other.getBirthDate());
		setSubscribed(other.isSubscribed());
	}

	@Override
	public String toString() {
		return getFirstName() + " " + getLastName();
	}

}
