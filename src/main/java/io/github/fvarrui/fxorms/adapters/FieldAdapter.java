package io.github.fvarrui.fxorms.adapters;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import io.github.fvarrui.fxorms.annotations.ReadOnly;
import javafx.beans.Observable;
import javafx.scene.Node;

public abstract class FieldAdapter<T extends Node, U> {
	
	private final List<Class<?>> forTypes = new ArrayList<>();
	
	public abstract T build(Field field);
	public abstract void doBind(T control, U observable);
	public abstract void doUnbind(T control, U observable);
	
	public boolean isReadOnly(Field field) {
		ReadOnly readonly = field.getAnnotation(ReadOnly.class);		
		return readonly != null && readonly.value();
	}
	
	@SafeVarargs
	public FieldAdapter(Class<?> ... types) {
		forTypes.addAll(Arrays.asList(types));
	}

	public boolean isPropertyType(Field field) {
		return forTypes.contains(field.getType());
	}
	
	@SuppressWarnings("unchecked")
	public final void bind(Node control, Observable observable) {
		doBind((T)control, (U) observable);
	}
	
	@SuppressWarnings("unchecked")
	public final void unbind(Node control, Observable observable) {
		doUnbind((T) control, (U) observable);
	}
	
	public static List<Class<?>> getGenericTypes(Field field) {
		ParameterizedType genericType = (ParameterizedType) field.getGenericType();
		return  Arrays.asList(genericType.getActualTypeArguments())
				.stream()
				.map(type -> (Class<?>) type)
				.collect(Collectors.toList());
	}
	
}
