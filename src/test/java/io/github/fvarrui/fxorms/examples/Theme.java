package io.github.fvarrui.fxorms.examples;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.paint.Color;

/**
 * Bean with {@code Color} properties, rendered with {@link ColorAdapter}.
 */
public class Theme {

	private final StringProperty name = new SimpleStringProperty();
	private final ObjectProperty<Color> background = new SimpleObjectProperty<>();
	private final ObjectProperty<Color> foreground = new SimpleObjectProperty<>();
	private final ObjectProperty<Color> accent = new SimpleObjectProperty<>();
	private final BooleanProperty dark = new SimpleBooleanProperty();

	public Theme(String name, Color background, Color foreground, Color accent, boolean dark) {
		this.name.set(name);
		this.background.set(background);
		this.foreground.set(foreground);
		this.accent.set(accent);
		this.dark.set(dark);
	}

	public StringProperty nameProperty() { return name; }
	public ObjectProperty<Color> backgroundProperty() { return background; }
	public ObjectProperty<Color> foregroundProperty() { return foreground; }
	public ObjectProperty<Color> accentProperty() { return accent; }
	public BooleanProperty darkProperty() { return dark; }

}
