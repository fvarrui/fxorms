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

/**
 * Builds {@link Form forms} for a bean class.
 * <p>
 * Typical use:
 * <pre>{@code
 * Form<User> form = new FormBuilder<>(User.class).build();
 * form.bind(user);
 * }</pre>
 * Each bean property (see {@link BeanProperty}) becomes a row with a label and a
 * control. The control is chosen by the first {@link FieldAdapter adapter} that
 * supports the property's type: custom adapters registered with
 * {@link #registerFieldAdapter(FieldAdapter)} first, then the built-in ones,
 * and a read-only label as fallback.
 * <p>
 * A builder can build several forms; each one is independent.
 *
 * @param <T> the bean type
 */
public class FormBuilder<T> {
	
	private Class<T> beanClass;
	private FieldFactory factory;

	/**
	 * Creates a builder for a bean class, with the built-in adapters.
	 *
	 * @param beanClass the bean class whose properties the forms will show
	 */
	public FormBuilder(Class<T> beanClass) {
		this.factory = new FieldFactory();
		this.beanClass = beanClass;
	}
	
	/**
	 * Builds a new, unbound form with a row per bean property. Row labels come
	 * from {@link io.github.fvarrui.fxorms.annotations.Label}, or are generated from
	 * the property name ({@code "lastLogin"} becomes "Last login",
	 * {@code "userID"} becomes "User ID").
	 *
	 * @return a new form; bind it to a bean with {@link Form#bind(Object)}
	 */
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
	
	/**
	 * Returns the properties that become form rows, in row order. Subclasses can
	 * override it to filter or reorder them.
	 *
	 * @return the bean class properties (see {@link BeanProperty#of(Class)})
	 */
	protected List<BeanProperty> getProperties() {
		return BeanProperty.of(beanClass);
	}
	
	/**
	 * Registers a custom adapter to render the properties it supports. Custom
	 * adapters take precedence over the built-in ones, and the last one registered
	 * wins over earlier ones.
	 *
	 * @param adapter the adapter to register; {@code null} is ignored
	 * @return this builder, for chaining
	 */
	public FormBuilder<T> registerFieldAdapter(FieldAdapter<? extends Node, ? extends Observable> adapter) {
		factory.registerAdapter(adapter);
		return this;
	}
	
	/**
	 * Label text of a property: its {@link io.github.fvarrui.fxorms.annotations.Label}
	 * annotation, or a text generated from its name.
	 */
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
