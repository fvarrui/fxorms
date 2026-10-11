package io.github.fvarrui.fxorms.examples;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Bean using the custom {@link Range} annotation, rendered with
 * {@link SliderAdapter}.
 */
public class AudioSettings {

	private final StringProperty device = new SimpleStringProperty();
	@Range(min = 0, max = 100)
	private final IntegerProperty volume = new SimpleIntegerProperty();
	@Range(min = -1, max = 1)
	private final DoubleProperty balance = new SimpleDoubleProperty();
	private final IntegerProperty sampleRate = new SimpleIntegerProperty();
	private final BooleanProperty muted = new SimpleBooleanProperty();

	public AudioSettings(String device, int volume, double balance, int sampleRate) {
		this.device.set(device);
		this.volume.set(volume);
		this.balance.set(balance);
		this.sampleRate.set(sampleRate);
	}

	public StringProperty deviceProperty() { return device; }
	public IntegerProperty volumeProperty() { return volume; }
	public DoubleProperty balanceProperty() { return balance; }
	public IntegerProperty sampleRateProperty() { return sampleRate; }
	public BooleanProperty mutedProperty() { return muted; }

}
