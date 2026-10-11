package io.github.fvarrui.fxorms.adapters;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;

import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.Node;

public abstract class ObjectAdapter<T extends Node, U extends Observable> extends FieldAdapter<T, U> {
	
	private Type genericType; 
	
	public ObjectAdapter(Type type) {
		super(ObjectProperty.class);
		this.genericType = type;
	}
	
	@Override
	public boolean isPropertyType(Field field) {
		if (!super.isPropertyType(field)) {
			return false;
		}
		List<Class<?>> genericTypes = getGenericTypes(field);
		return !genericTypes.isEmpty() && ((Class<?>) genericType).isAssignableFrom(genericTypes.get(0));
	}	

	@Override
	public boolean isReadOnly(Field field) {
		return super.isReadOnly(field) || ReadOnlyObjectWrapper.class.isAssignableFrom(field.getType());
	}
	
}
