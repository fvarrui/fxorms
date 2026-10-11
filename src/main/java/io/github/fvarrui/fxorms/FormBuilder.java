package io.github.fvarrui.fxorms;

import java.util.List;

import io.github.fvarrui.fxorms.adapters.FieldAdapter;
import javafx.beans.Observable;
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

	public FormBuilder(Class<T> beanClass) {
		this.factory = new FieldFactory();
		this.beanClass = beanClass;
	}
	
	public Form<T> build() {
		
		try { 

			Form<T> form = new Form<>(factory);
			form.setHgap(5);
			form.setVgap(5);
			form.setPadding(new Insets(5));
			form.getStyleClass().add("form");
			form.getStylesheets().add(FormBuilder.class.getResource("/css/fxorms/form.css").toExternalForm());
			
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
	
			getProperties().forEach(property -> { 
		
				Label label = new Label(getLabelText(property) + ":");
				Node control = factory.buildControl(property); 
				
				form.addControl(property, control);
				
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
	
	protected List<BeanProperty> getProperties() {
		return BeanProperty.of(beanClass);
	}
	
	public FormBuilder<T> registerFieldAdapter(FieldAdapter<? extends Node, ? extends Observable> adapter) {
		factory.registerAdapter(adapter);
		return this;
	}
	
	private static String getLabelText(BeanProperty property) {
		io.github.fvarrui.fxorms.annotations.Label label = property.getAnnotation(io.github.fvarrui.fxorms.annotations.Label.class);
		return label != null ? label.value() : getReadableStringFromCamelCase(property.getName());
	}

	/**
	 * "lastLogin" → "Last login"; runs of capitals are kept as acronyms:
	 * "userID" → "User ID", "urlPath" → "Url path", "URLPath" → "URL path".
	 */
	static String getReadableStringFromCamelCase(String camelCaseString) {
		String[] words = camelCaseString.split("(?<=[a-z0-9])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])");
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < words.length; i++) {
			String word = words[i];
			boolean acronym = word.length() > 1 && word.equals(word.toUpperCase());
			if (i == 0) {
				buffer.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
			} else {
				buffer.append(' ').append(acronym ? word : word.toLowerCase());
			}
		}
		return buffer.toString();
	}

}
