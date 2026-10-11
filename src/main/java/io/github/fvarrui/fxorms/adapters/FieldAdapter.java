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

public abstract class FieldAdapter<T extends Node, U> {
	
	private final List<Class<?>> forTypes = new ArrayList<>();
	
	public abstract T build(BeanProperty property);
	public abstract void doBind(T control, U observable);
	public abstract void doUnbind(T control, U observable);
	
	/**
	 * A property is read-only when it's annotated with {@link ReadOnly} or its
	 * accessor only returns a {@code ReadOnlyXxxProperty}.
	 */
	public boolean isReadOnly(BeanProperty property) {
		ReadOnly readonly = property.getAnnotation(ReadOnly.class);
		return (readonly != null && readonly.value()) || !property.isWritable();
	}
	
	/**
	 * @param types property types this adapter renders; list the read-only base
	 *              types (e.g. {@code ReadOnlyStringProperty}) so that read-only
	 *              accessors match too.
	 */
	@SafeVarargs
	public FieldAdapter(Class<?> ... types) {
		forTypes.addAll(Arrays.asList(types));
	}

	public boolean isPropertyType(BeanProperty property) {
		return forTypes.stream().anyMatch(type -> type.isAssignableFrom(property.getType()));
	}
	
	@SuppressWarnings("unchecked")
	public final void bind(Node control, Observable observable) {
		doBind((T)control, (U) observable);
	}
	
	@SuppressWarnings("unchecked")
	public final void unbind(Node control, Observable observable) {
		doUnbind((T) control, (U) observable);
	}

	/**
	 * Binds a control's value to the bean's property: both ways when the bean's
	 * property is writable, one way (control follows bean) when it's read-only.
	 */
	@SuppressWarnings("unchecked")
	protected static <V> void bindValue(Property<V> controlValue, ObservableValue<? extends V> beanValue) {
		if (beanValue instanceof Property) {
			controlValue.bindBidirectional((Property<V>) beanValue);
		} else {
			controlValue.bind(beanValue);
		}
	}

	@SuppressWarnings("unchecked")
	protected static <V> void unbindValue(Property<V> controlValue, ObservableValue<? extends V> beanValue) {
		if (beanValue instanceof Property) {
			controlValue.unbindBidirectional((Property<V>) beanValue);
		} else {
			controlValue.unbind();
		}
	}
	
}
