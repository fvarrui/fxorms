package io.github.fvarrui.fxorms;

import java.util.LinkedHashMap;
import java.util.Map;

import javafx.scene.Node;
import javafx.scene.layout.GridPane;

public class Form<T> extends GridPane {

	private T bean;
	private final FieldFactory factory;
	private final Map<BeanProperty, Node> controls = new LinkedHashMap<>();

	Form(FieldFactory factory) {
		super();
		this.factory = factory;
	}

	void addControl(BeanProperty property, Node control) {
		controls.put(property, control);
	}

	public T getBean() {
		return bean;
	}

	public Form<T> bind(T bean) {
		unbind();
		if (bean != null) {
			this.bean = bean;
			controls.forEach((property, control) -> factory.findAdapter(property).bind(control, property.get(bean)));
		}
		return this;
	}

	public Form<T> unbind() {
		if (this.bean != null) {
			controls.forEach((property, control) -> factory.findAdapter(property).unbind(control, property.get(this.bean)));
			this.bean = null;
		}
		return this;
	}

}
