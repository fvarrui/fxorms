package io.github.fvarrui.fxorms;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

import javafx.beans.property.Property;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;

public class Form<T> extends GridPane {

	private T bean;
	private final FieldFactory factory;
	private final Map<Field, Node> controls = new LinkedHashMap<>();

	Form(FieldFactory factory) {
		super();
		this.factory = factory;
	}

	void addControl(Field field, Node control) {
		controls.put(field, control);
	}

	public T getBean() {
		return bean;
	}

	public Form<T> bind(T bean) {
		unbind();
		if (bean != null) {
			this.bean = bean;
			controls.forEach((field, control) -> factory.findAdapter(field).bind(control, getProperty(field, bean)));
		}
		return this;
	}

	public Form<T> unbind() {
		if (this.bean != null) {
			controls.forEach((field, control) -> factory.findAdapter(field).unbind(control, getProperty(field, this.bean)));
			this.bean = null;
		}
		return this;
	}

	private Property<?> getProperty(Field field, T bean) {
		try {
			field.setAccessible(true);
			Property<?> property = (Property<?>) field.get(bean);
			if (property == null) {
				throw new IllegalStateException("Property field '" + field.getName() + "' is not initialized");
			}
			return property;
		} catch (IllegalAccessException e) {
			throw new IllegalStateException("Cannot read property field '" + field.getName() + "'", e);
		}
	}

}
