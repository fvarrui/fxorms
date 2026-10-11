package io.github.fvarrui.fxorms.examples;

import io.github.fvarrui.fxorms.BeanProperty;
import io.github.fvarrui.fxorms.adapters.FieldAdapter;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Slider;

/**
 * Custom adapter driven by a custom annotation: integer and double properties
 * annotated with {@link Range} become a {@link Slider}; the rest keep the
 * built-in text field, because this adapter only claims annotated properties.
 */
public class SliderAdapter extends FieldAdapter<Slider, ObservableValue<Number>> {

	public SliderAdapter() {
		super(ReadOnlyIntegerProperty.class, ReadOnlyDoubleProperty.class);
	}

	@Override
	public boolean isPropertyType(BeanProperty property) {
		return super.isPropertyType(property) && property.getAnnotation(Range.class) != null;
	}

	@Override
	public Slider build(BeanProperty property) {
		Range range = property.getAnnotation(Range.class);
		Slider slider = new Slider(range.min(), range.max(), range.min());
		slider.setMajorTickUnit((range.max() - range.min()) / 4);
		slider.setShowTickMarks(true);
		slider.setShowTickLabels(true);
		slider.setPrefWidth(260);
		slider.setDisable(isReadOnly(property));
		return slider;
	}

	@Override
	public void doBind(Slider control, ObservableValue<Number> property) {
		bindValue(control.valueProperty(), property);
	}

	@Override
	public void doUnbind(Slider control, ObservableValue<Number> property) {
		unbindValue(control.valueProperty(), property);
	}

}
