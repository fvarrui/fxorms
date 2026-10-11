package io.github.fvarrui.fxorms;

import java.util.LinkedHashMap;
import java.util.Map;

import javafx.scene.Node;
import javafx.scene.layout.GridPane;

/**
 * A form built by {@link FormBuilder} for a bean class: a {@link GridPane}
 * with one row per bean property, made of a label and a control.
 * <p>
 * A form is created unbound. {@link #bind(Object)} connects its controls to the
 * properties of a bean instance, so that editing the form updates the bean and
 * changes in the bean show up in the form. A form is bound to at most one bean
 * at a time and can be rebound to other instances of the same class.
 * <p>
 * The form has the {@code form} style class and a default stylesheet that marks
 * invalid input; applications can add their own stylesheets to restyle it.
 *
 * @param <T> the bean type
 */
public class Form<T> extends GridPane {

	private T bean;
	private final Class<T> beanClass;
	private final FieldFactory factory;
	private final Map<BeanProperty, Node> controls = new LinkedHashMap<>();

	Form(Class<T> beanClass, FieldFactory factory) {
		super();
		this.beanClass = beanClass;
		this.factory = factory;
	}

	void addControl(BeanProperty property, Node control) {
		controls.put(property, control);
	}

	/**
	 * Returns the control the form built for a property, for example to focus
	 * it, add a tooltip or listen to it:
	 * <pre>{@code
	 * Form<Person> form = new FormBuilder<>(Person.class).build();
	 * form.getControl("email").requestFocus();
	 * }</pre>
	 * The control is the same before and after binding a bean; its type depends
	 * on the property's adapter (e.g. a {@code TextField} for a string).
	 *
	 * @param propertyName the property name, as returned by
	 *                     {@link BeanProperty#getName()}: {@code "email"} for
	 *                     {@code emailProperty()}
	 * @return the property's control
	 * @throws IllegalArgumentException if the form has no property with that name
	 */
	public Node getControl(String propertyName) {
		return controls.entrySet().stream()
				.filter(entry -> entry.getKey().getName().equals(propertyName))
				.map(Map.Entry::getValue)
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("No property '" + propertyName + "' in the form of " + beanClass.getName()));
	}

	/**
	 * Returns the bean currently bound to this form.
	 *
	 * @return the bound bean, or {@code null} if the form is unbound
	 */
	public T getBean() {
		return bean;
	}

	/**
	 * Binds a bean to this form. The previously bound bean, if any, is unbound
	 * first, so it no longer receives changes from the form.
	 * <p>
	 * Writable properties are bound in both directions; read-only properties only
	 * update the form.
	 *
	 * @param bean the bean to bind, or {@code null} to just unbind the current one
	 * @return this form, for chaining
	 * @throws IllegalStateException if a property of the bean can't be read (see
	 *         {@link BeanProperty#get(Object)})
	 */
	public Form<T> bind(T bean) {
		unbind();
		if (bean != null) {
			this.bean = bean;
			controls.forEach((property, control) -> factory.findAdapter(property).bind(control, property.get(bean)));
		}
		return this;
	}

	/**
	 * Unbinds the current bean from this form, if any. The controls keep showing
	 * the last values, but changes no longer reach the bean.
	 *
	 * @return this form, for chaining
	 */
	public Form<T> unbind() {
		if (this.bean != null) {
			controls.forEach((property, control) -> factory.findAdapter(property).unbind(control, property.get(this.bean)));
			this.bean = null;
		}
		return this;
	}

}
