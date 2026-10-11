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

public class FieldFactory {
	
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
	
	public List<FieldAdapter<? extends Node, ? extends Observable>> getAdapters() {
		return Collections.unmodifiableList(adapters);
	}

	/**
	 * Registers a custom adapter. Custom adapters are looked up before the
	 * built-in ones (and the latest registered first), so they can override them.
	 */
	public void registerAdapter(FieldAdapter<? extends Node, ? extends Observable> adapter) {
		if (adapter != null) {
			adapters.add(0, adapter);
		}
	}
	
	public FieldAdapter<? extends Node, ? extends Observable> findAdapter(BeanProperty property) { 
		return adapters
			.stream()
			.filter(adapter -> adapter.isPropertyType(property))
			.findFirst()
			.orElse(new DefaultAdapter());
	}
	
	public Node buildControl(BeanProperty property) {
		FieldAdapter<? extends Node, ? extends Observable> adapter = findAdapter(property);
		return adapter.build(property);
	}

}
