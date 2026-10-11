package io.github.fvarrui.fxorms.adapters;

import java.lang.reflect.Type;
import java.util.List;

import io.github.fvarrui.fxorms.BeanProperty;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyMapProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlySetProperty;
import javafx.scene.Node;

/**
 * Base for adapters of {@code ObjectProperty<X>}: matches object properties
 * whose type argument is assignable to {@code X}. For example, {@link EnumAdapter}
 * passes {@code Enum.class}, so it matches {@code ObjectProperty<Size>} for
 * any enum {@code Size}.
 * <p>
 * List, map and set properties are never matched, although they are object
 * properties too.
 *
 * @param <T> the control type
 * @param <U> the type of the bean's property object the control binds to
 */
public abstract class ObjectAdapter<T extends Node, U> extends FieldAdapter<T, U> {
	
	private Type genericType; 
	
	/**
	 * Creates an adapter for object properties of the given value type.
	 *
	 * @param type the value type {@code X} of the {@code ObjectProperty<X>}
	 *             properties to match; subtypes match too
	 */
	public ObjectAdapter(Type type) {
		super(ReadOnlyObjectProperty.class);
		this.genericType = type;
	}
	
	@Override
	public boolean isPropertyType(BeanProperty property) {
		if (!super.isPropertyType(property) || isCollection(property.getType())) {
			return false;
		}
		List<Class<?>> genericTypes = property.getGenericTypes();
		return !genericTypes.isEmpty() && ((Class<?>) genericType).isAssignableFrom(genericTypes.get(0));
	}

	/** List, map and set properties are object properties too; their type argument is the element type. */
	private static boolean isCollection(Class<?> type) {
		return ReadOnlyListProperty.class.isAssignableFrom(type)
				|| ReadOnlyMapProperty.class.isAssignableFrom(type)
				|| ReadOnlySetProperty.class.isAssignableFrom(type);
	}
	
}
