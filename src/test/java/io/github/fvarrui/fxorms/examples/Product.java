package io.github.fvarrui.fxorms.examples;

import java.time.LocalDateTime;

import io.github.fvarrui.fxorms.annotations.Format;
import io.github.fvarrui.fxorms.annotations.Label;
import io.github.fvarrui.fxorms.annotations.Multiline;
import io.github.fvarrui.fxorms.annotations.Order;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Customizing rows: labels, order, formats, multi-line text and enums.
 */
public class Product {

	public enum Category { BOOKS, ELECTRONICS, GARDEN, TOYS }

	private final StringProperty name = new SimpleStringProperty();
	@Multiline(rows = 3)
	private final StringProperty description = new SimpleStringProperty();
	@Order(0)
	@Label("SKU")
	private final StringProperty code = new SimpleStringProperty();
	private final ObjectProperty<Category> category = new SimpleObjectProperty<>();
	@Format("#,##0.00")
	private final DoubleProperty price = new SimpleDoubleProperty();
	@Label("Units in stock")
	private final IntegerProperty stock = new SimpleIntegerProperty();
	@Format("yyyy-MM-dd HH:mm")
	private final ObjectProperty<LocalDateTime> availableFrom = new SimpleObjectProperty<>();

	public StringProperty nameProperty() { return name; }
	public StringProperty descriptionProperty() { return description; }
	public StringProperty codeProperty() { return code; }
	public ObjectProperty<Category> categoryProperty() { return category; }
	public DoubleProperty priceProperty() { return price; }
	public IntegerProperty stockProperty() { return stock; }
	public ObjectProperty<LocalDateTime> availableFromProperty() { return availableFrom; }

}
