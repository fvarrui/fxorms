package io.github.fvarrui.fxorms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.github.fvarrui.fxorms.adapters.BooleanAdapter;
import io.github.fvarrui.fxorms.adapters.DefaultAdapter;
import io.github.fvarrui.fxorms.adapters.EnumAdapter;
import io.github.fvarrui.fxorms.adapters.FieldAdapter;
import io.github.fvarrui.fxorms.adapters.ListAdapter;
import io.github.fvarrui.fxorms.adapters.LocalDateAdapter;
import io.github.fvarrui.fxorms.adapters.LocalDateTimeAdapter;
import io.github.fvarrui.fxorms.adapters.MapAdapter;
import io.github.fvarrui.fxorms.adapters.NumberAdapter;
import io.github.fvarrui.fxorms.adapters.StringAdapter;
import javafx.beans.Observable;
import javafx.scene.Node;

/**
 * Holds the adapters of a {@link FormBuilder} and picks the one that renders
 * each property.
 * <p>
 * Adapters are tried in order and the first whose
 * {@link FieldAdapter#isPropertyType(BeanProperty)} returns {@code true} wins:
 * custom adapters (latest registered first), then the built-in ones for
 * strings, numbers, date-times, dates, enums, booleans, maps and lists. If none
 * matches, a {@link DefaultAdapter} shows the value in a read-only label.
 */
public class FieldFactory {

	/**
	 * Creates a factory with the built-in adapters.
	 */
	public FieldFactory() {
		super();
	}
	
	private final List<FieldAdapter<? extends Node, ? extends Observable>> adapters = new ArrayList<>(Arrays.asList(
			new StringAdapter(),
			new NumberAdapter(),
			new LocalDateTimeAdapter(),
			new LocalDateAdapter(),
			new EnumAdapter(),
			new BooleanAdapter(),
			new MapAdapter(),
			new ListAdapter()
	));
	
	/**
	 * Returns the registered adapters, in lookup order.
	 *
	 * @return an unmodifiable view of the adapters
	 */
	public List<FieldAdapter<? extends Node, ? extends Observable>> getAdapters() {
		return Collections.unmodifiableList(adapters);
	}

	/**
	 * Registers a custom adapter. Custom adapters are looked up before the
	 * built-in ones (and the latest registered first), so they can override them.
	 *
	 * @param adapter the adapter to register; {@code null} is ignored
	 */
	public void registerAdapter(FieldAdapter<? extends Node, ? extends Observable> adapter) {
		if (adapter != null) {
			adapters.add(0, adapter);
		}
	}
	
	/**
	 * Finds the adapter that renders a property.
	 *
	 * @param property the bean property
	 * @return the first adapter that supports the property, or a
	 *         {@link DefaultAdapter} if none does
	 */
	public FieldAdapter<? extends Node, ? extends Observable> findAdapter(BeanProperty property) { 
		return adapters
			.stream()
			.filter(adapter -> adapter.isPropertyType(property))
			.findFirst()
			.orElse(new DefaultAdapter());
	}
	
	/**
	 * Builds the control of a property with its adapter.
	 *
	 * @param property the bean property
	 * @return a new control for the property, not yet bound to any bean
	 */
	public Node buildControl(BeanProperty property) {
		FieldAdapter<? extends Node, ? extends Observable> adapter = findAdapter(property);
		return adapter.build(property);
	}

}
