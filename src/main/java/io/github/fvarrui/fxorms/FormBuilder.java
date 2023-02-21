package io.github.fvarrui.fxorms;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.github.fvarrui.fxorms.adapters.FieldAdapter;
import javafx.beans.Observable;
import javafx.beans.property.Property;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

public class FormBuilder<T> {
	
	private Class<T> beanClass;
	private FieldFactory factory;
	private Map<Field, Node> controls = new HashMap<>();

	public FormBuilder(Class<T> beanClass) {
		this.factory = new FieldFactory();
		this.beanClass = beanClass;
	}
	
	public Form<T> build() {
		
		try { 

			Form<T> form = new Form<>(this);
			form.setHgap(5);
			form.setVgap(5);
			form.setPadding(new Insets(5));
			form.getStyleClass().add("form");
			
			ColumnConstraints [] cols = {
				new ColumnConstraints(),
				new ColumnConstraints()
			};
			form.getColumnConstraints().setAll(cols);
			
			cols[0].setHalignment(HPos.RIGHT);
			cols[0].setMinWidth(Control.USE_PREF_SIZE);
			cols[1].setHgrow(Priority.ALWAYS);
			cols[1].setFillWidth(false);
			
			form.getRowConstraints().clear();
	
			getFields().forEach(field -> { 
		
				Label label = new Label(getReadableStringFromCamelCase(field.getName()) + ":");
				Node control = factory.buildControl(field); 
				
				controls.put(field, control);
				
				form.addRow(
					form.getRowCount(), 
					label, 
					control
				);
				RowConstraints row = new RowConstraints();
				row.setMinHeight(30);
				form.getRowConstraints().add(row);
				
			});
			
			return form;
			
		} catch (Exception e) {
			
			throw new RuntimeException(e);
			
		}
			
	}
	
	protected List<Field> getFields() {
		return Arrays.asList(beanClass.getDeclaredFields())
			.stream()
			.filter(field -> Property.class.isAssignableFrom(field.getType()))
			.collect(Collectors.toList());		
	}
	
	public void bind(Field field, Observable property) {
		Node control = controls.get(field);
		factory.findAdapter(field).bind(control, property);
	}
	
	public void unbind(Field field, Property<?> property) {
		Node control = controls.get(field);
		factory.findAdapter(field).unbind(control, property);		
	}
	
	public FormBuilder<T> registerFieldAdapter(FieldAdapter<? extends Node, ? extends Observable> adapter) {
		if (adapter != null) {
			factory.getAdapters().add(adapter);
		}
		return this;
	}
	
	private String getReadableStringFromCamelCase(String camelCaseString) {
		boolean firstChar = true;
		StringBuffer buffer = new StringBuffer();
		for (char ch : camelCaseString.toCharArray()) {
			if (firstChar) {
				buffer.append(Character.toUpperCase(ch));
				firstChar = false;
			} else if (Character.isUpperCase(ch)) {
				buffer.append(" " + Character.toLowerCase(ch));
				firstChar = false;
			} else {
				buffer.append(ch);				
			}
		}		
		return buffer.toString();
	}

}
