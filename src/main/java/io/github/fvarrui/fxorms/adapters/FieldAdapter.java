package io.github.fvarrui.fxorms.adapters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.github.fvarrui.fxorms.BeanProperty;
import io.github.fvarrui.fxorms.annotations.ReadOnly;
import javafx.beans.Observable;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;

/**
 * Renders one family of property types: decides which properties it supports,
 * builds their control and binds the control to a bean's property object.
 * <p>
 * To support a new type, extend this class (or {@link ObjectAdapter} for
 * {@code ObjectProperty<X>} types) and register it with
 * {@link io.github.fvarrui.fxorms.FormBuilder#registerFieldAdapter(FieldAdapter)}:
 * <pre>{@code
 * public class ColorAdapter extends ObjectAdapter<ColorPicker, ObservableValue<Color>> {
 *     public ColorAdapter() {
 *         super(Color.class);
 *     }
 *     public ColorPicker build(BeanProperty property) {
 *         ColorPicker control = new ColorPicker();
 *         control.setDisable(isReadOnly(property));
 *         return control;
 *     }
 *     public void doBind(ColorPicker control, ObservableValue<Color> property) {
 *         bindValue(control.valueProperty(), property);
 *     }
 *     public void doUnbind(ColorPicker control, ObservableValue<Color> property) {
 *         unbindValue(control.valueProperty(), property);
 *     }
 * }
 * }</pre>
 * An adapter instance is shared by every form of a builder and by every
 * property it renders, so it must not keep per-control state in fields; store it
 * in the control (e.g. {@link Node#getProperties()}) instead.
 *
 * @param <T> the control type
 * @param <U> the type of the bean's property object the control binds to
 */
public abstract class FieldAdapter<T extends Node, U> {
	
	private final List<Class<?>> forTypes = new ArrayList<>();
	
	/**
	 * Builds the control of a property. The control isn't bound to any bean yet.
	 * Implementations should make it non-editable or disabled when
	 * {@link #isReadOnly(BeanProperty)} is {@code true}.
	 *
	 * @param property the property to render
	 * @return a new control
	 */
	public abstract T build(BeanProperty property);
	/**
	 * Binds a control built by this adapter to a bean's property object.
	 *
	 * @param control the control, built by {@link #build(BeanProperty)}
	 * @param observable the bean's property object; it implements
	 *                   {@link javafx.beans.property.Property} only if the property
	 *                   is writable (see {@link #bindValue(Property, ObservableValue)})
	 */
	public abstract void doBind(T control, U observable);
	/**
	 * Undoes {@link #doBind(Node, Object)} for the same control and property
	 * object.
	 *
	 * @param control the control
	 * @param observable the bean's property object it was bound to
	 */
	public abstract void doUnbind(T control, U observable);
	
	/**
	 * Tells whether a property must be rendered read-only: when it's annotated with
	 * {@link ReadOnly} or its accessor only returns a {@code ReadOnlyXxxProperty}.
	 *
	 * @param property the property
	 * @return {@code true} if the form must not let the user edit it
	 */
	public boolean isReadOnly(BeanProperty property) {
		ReadOnly readonly = property.getAnnotation(ReadOnly.class);
		return (readonly != null && readonly.value()) || !property.isWritable();
	}
	
	/**
	 * Creates an adapter for the given property types.
	 *
	 * @param types property types this adapter renders; a property matches when its
	 *              accessor's return type is assignable to one of them. List the
	 *              read-only base types (e.g. {@code ReadOnlyStringProperty}) so
	 *              that read-only accessors match too.
	 */
	@SafeVarargs
	public FieldAdapter(Class<?> ... types) {
		forTypes.addAll(Arrays.asList(types));
	}

	/**
	 * Tells whether this adapter renders a property. By default, when the
	 * accessor's return type is assignable to one of the types given to the
	 * constructor. Must give the same answer every time for the same property.
	 *
	 * @param property the property
	 * @return {@code true} if this adapter renders it
	 */
	public boolean isPropertyType(BeanProperty property) {
		return forTypes.stream().anyMatch(type -> type.isAssignableFrom(property.getType()));
	}
	
	/**
	 * Binds a control to a bean's property object; called by the form. Delegates
	 * to {@link #doBind(Node, Object)} with the right types.
	 *
	 * @param control a control built by this adapter
	 * @param observable the bean's property object
	 */
	@SuppressWarnings("unchecked")
	public final void bind(Node control, Observable observable) {
		doBind((T)control, (U) observable);
	}
	
	/**
	 * Unbinds a control from a bean's property object; called by the form.
	 * Delegates to {@link #doUnbind(Node, Object)} with the right types.
	 *
	 * @param control a control built by this adapter
	 * @param observable the bean's property object it was bound to
	 */
	@SuppressWarnings("unchecked")
	public final void unbind(Node control, Observable observable) {
		doUnbind((T) control, (U) observable);
	}

	/**
	 * Binds a control's value to a bean's property object: in both directions if
	 * the bean's property is writable, one way (the control follows the bean) if
	 * it's read-only.
	 *
	 * @param <V> the value type
	 * @param controlValue the control's value property, e.g. a check box's
	 *                     {@code selectedProperty()}
	 * @param beanValue the bean's property object
	 */
	@SuppressWarnings("unchecked")
	protected static <V> void bindValue(Property<V> controlValue, ObservableValue<? extends V> beanValue) {
		if (beanValue instanceof Property) {
			controlValue.bindBidirectional((Property<V>) beanValue);
		} else {
			controlValue.bind(beanValue);
		}
	}

	/**
	 * Undoes {@link #bindValue(Property, ObservableValue)}.
	 *
	 * @param <V> the value type
	 * @param controlValue the control's value property
	 * @param beanValue the bean's property object it was bound to
	 */
	@SuppressWarnings("unchecked")
	protected static <V> void unbindValue(Property<V> controlValue, ObservableValue<? extends V> beanValue) {
		if (beanValue instanceof Property) {
			controlValue.unbindBidirectional((Property<V>) beanValue);
		} else {
			controlValue.unbind();
		}
	}
	
}
