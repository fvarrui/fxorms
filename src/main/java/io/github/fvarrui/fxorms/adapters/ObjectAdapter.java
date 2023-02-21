package io.github.fvarrui.fxorms.adapters;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;

public abstract class ObjectAdapter<T extends Node, U extends Observable> extends FieldAdapter<T, U> {
	
	private Type genericType; 
	
	public ObjectAdapter(Type type) {
		super(ObjectProperty.class);
		this.genericType = type;
	}
	
	@Override
	public boolean isPropertyType(Field field) {
		return super.isPropertyType(field) && ((Class<?>) genericType).isAssignableFrom(getGenericTypes(field).get(0));
	}	
	
}
