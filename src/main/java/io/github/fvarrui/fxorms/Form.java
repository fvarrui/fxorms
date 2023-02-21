package io.github.fvarrui.fxorms;

import javafx.beans.property.Property;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;

public class Form<T> extends GridPane {

	private T bean;
	private FormBuilder<T> builder;

	public Form(FormBuilder<T> builder) {
		super();
		this.builder = builder;
	}

	public Form<T> bind(T bean) {
		if (bean == null) {
			unbind();
		} else {
			this.bean = bean;
			builder.getFields().forEach(field -> {
				try {
					field.setAccessible(true);
					Property<?> property = (Property<?>) field.get(this.bean);
					builder.bind(field, property);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			});
		}
		return this;
	}

	public Form<T> unbind() {
		if (this.bean != null) {
			builder.getFields().forEach(field -> {
				try {
					field.setAccessible(true);
					Property<?> property = (Property<?>) field.get(this.bean);
					builder.unbind(field, property);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			});
		}
		return this;
	}

	protected int getRowCount() {
		int numRows = getRowConstraints().size();
		for (int i = 0; i < getChildren().size(); i++) {
			Node child = getChildren().get(i);
			if (child.isManaged()) {
				Integer rowIndex = GridPane.getRowIndex(child);
				if (rowIndex != null) {
					numRows = Math.max(numRows, rowIndex + 1);
				}
			}
		}
		return numRows;
	}

}
