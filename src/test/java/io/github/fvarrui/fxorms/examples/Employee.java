package io.github.fvarrui.fxorms.examples;

import java.time.LocalDate;

import io.github.fvarrui.fxorms.annotations.Format;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Inheritance: the form shows the properties of {@link Person} first, then the
 * ones declared here.
 */
public class Employee extends Person {

	private final StringProperty department = new SimpleStringProperty();
	@Format("#,##0.00")
	private final DoubleProperty salary = new SimpleDoubleProperty();

	public Employee(String firstName, String lastName, String email, LocalDate birthDate, String department, double salary) {
		super(firstName, lastName, email, birthDate, false);
		this.department.set(department);
		this.salary.set(salary);
	}

	public StringProperty departmentProperty() { return department; }
	public DoubleProperty salaryProperty() { return salary; }

}
