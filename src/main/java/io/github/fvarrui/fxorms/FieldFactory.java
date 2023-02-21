package io.github.fvarrui.fxorms;

import java.util.Arrays;
import java.util.List;

import io.github.fvarrui.fxorms.adapters.BooleanAdapter;
import io.github.fvarrui.fxorms.adapters.DefaultAdapter;
import io.github.fvarrui.fxorms.adapters.FieldAdapter;
import io.github.fvarrui.fxorms.adapters.ListAdapter;
import io.github.fvarrui.fxorms.adapters.LocalDateTimeAdapter;
import io.github.fvarrui.fxorms.adapters.MapAdapter;
import io.github.fvarrui.fxorms.adapters.NumberAdapter;
import io.github.fvarrui.fxorms.adapters.StringAdapter;
import javafx.beans.Observable;
import javafx.scene.Node;

public class FieldFactory {
	
	private List<FieldAdapter<? extends Node, ? extends Observable>> adapters = Arrays.asList(
			new StringAdapter(),
			new NumberAdapter(),
			new LocalDateTimeAdapter(),
			new BooleanAdapter(),
			new MapAdapter(),
			new ListAdapter()
	);
	
	public List<FieldAdapter<? extends Node, ? extends Observable>> getAdapters() {
		return adapters;
	}
	
	public FieldAdapter<? extends Node, ? extends Observable> findAdapter(java.lang.reflect.Field field) { 
		return adapters
			.stream()
			.filter(adapter -> adapter.isPropertyType(field))
			.findFirst()
			.orElse(new DefaultAdapter());
	}
	
	public Node buildControl(java.lang.reflect.Field field) {
		FieldAdapter<? extends Node, ? extends Observable> adapter = findAdapter(field);
		return adapter.build(field);
	}

}
