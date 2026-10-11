package io.github.fvarrui.fxorms;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.github.fvarrui.fxorms.annotations.Order;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;

/**
 * A JavaFX property of a bean class, found through its public accessor method
 * ({@code public XxxProperty nameProperty()}), following the JavaFX bean
 * convention.
 */
public final class BeanProperty {

	private static final String SUFFIX = "Property";

	private final String name;
	private final Method accessor;
	private final Field field;

	private BeanProperty(String name, Method accessor, Field field) {
		this.name = name;
		this.accessor = accessor;
		this.field = field;
	}

	/**
	 * Every property of the class, including inherited ones, in form order:
	 * properties with {@link Order} first (by value), then the rest in the
	 * declaration order of their backing fields (superclass first), then the
	 * ones without a backing field, by name.
	 */
	public static List<BeanProperty> of(Class<?> beanClass) {
		Map<String, BeanProperty> properties = new LinkedHashMap<>();
		for (Method method : beanClass.getMethods()) {
			if (isAccessor(method)) {
				String name = method.getName().substring(0, method.getName().length() - SUFFIX.length());
				method.trySetAccessible(); // public methods of non-public bean classes
				properties.putIfAbsent(name, new BeanProperty(name, method, findField(beanClass, name)));
			}
		}
		List<Field> fields = getFieldsInDeclarationOrder(beanClass);
		Comparator<BeanProperty> byOrder = Comparator.comparingInt(BeanProperty::getOrder);
		Comparator<BeanProperty> byField = Comparator.comparingInt(property -> property.field == null ? Integer.MAX_VALUE : fields.indexOf(property.field));
		List<BeanProperty> sorted = new ArrayList<>(properties.values());
		sorted.sort(byOrder.thenComparing(byField).thenComparing(BeanProperty::getName));
		return sorted;
	}

	private static boolean isAccessor(Method method) {
		return method.getName().endsWith(SUFFIX)
				&& method.getName().length() > SUFFIX.length()
				&& method.getParameterCount() == 0
				&& !Modifier.isStatic(method.getModifiers())
				&& !method.isBridge()
				&& !method.isSynthetic()
				&& ReadOnlyProperty.class.isAssignableFrom(method.getReturnType())
				&& !method.getDeclaringClass().getName().startsWith("javafx.");
	}

	private static Field findField(Class<?> beanClass, String name) {
		for (Class<?> type = beanClass; type != null && type != Object.class; type = type.getSuperclass()) {
			try {
				return type.getDeclaredField(name);
			} catch (NoSuchFieldException e) {
				// keep looking in the superclass
			}
		}
		return null;
	}

	private static List<Field> getFieldsInDeclarationOrder(Class<?> beanClass) {
		List<Class<?>> hierarchy = new ArrayList<>();
		for (Class<?> type = beanClass; type != null && type != Object.class; type = type.getSuperclass()) {
			hierarchy.add(0, type);
		}
		return hierarchy.stream()
				.flatMap(type -> Arrays.stream(type.getDeclaredFields()))
				.collect(Collectors.toList());
	}

	public String getName() {
		return name;
	}

	/** Declared return type of the accessor, e.g. {@code StringProperty}. */
	public Class<?> getType() {
		return accessor.getReturnType();
	}

	/** Type arguments of the accessor's return type, e.g. {@code [LocalDate]} for {@code ObjectProperty<LocalDate>}. */
	public List<Class<?>> getGenericTypes() {
		Type genericType = accessor.getGenericReturnType();
		if (!(genericType instanceof ParameterizedType)) {
			return Collections.emptyList();
		}
		return Arrays.stream(((ParameterizedType) genericType).getActualTypeArguments())
				.map(BeanProperty::toClass)
				.collect(Collectors.toList());
	}

	private static Class<?> toClass(Type type) {
		if (type instanceof Class) {
			return (Class<?>) type;
		}
		if (type instanceof ParameterizedType) {
			return (Class<?>) ((ParameterizedType) type).getRawType();
		}
		return Object.class; // wildcards and type variables
	}

	/** True when the accessor returns a writable {@link Property}, not only a {@link ReadOnlyProperty}. */
	public boolean isWritable() {
		return Property.class.isAssignableFrom(getType());
	}

	/** The annotation on the accessor method, or else on the backing field with the same name. */
	public <A extends Annotation> A getAnnotation(Class<A> annotationClass) {
		A annotation = accessor.getAnnotation(annotationClass);
		if (annotation == null && field != null) {
			annotation = field.getAnnotation(annotationClass);
		}
		return annotation;
	}

	private int getOrder() {
		Order order = getAnnotation(Order.class);
		return order != null ? order.value() : Integer.MAX_VALUE;
	}

	/** The property object of the given bean instance. */
	public ReadOnlyProperty<?> get(Object bean) {
		try {
			ReadOnlyProperty<?> property = (ReadOnlyProperty<?>) accessor.invoke(bean);
			if (property == null) {
				throw new IllegalStateException("Property '" + name + "' is null: " + accessor.getName() + "() returned null");
			}
			return property;
		} catch (IllegalAccessException e) {
			String module = BeanProperty.class.getModule().getName();
			throw new IllegalStateException("Cannot access " + accessor.getDeclaringClass().getName() + "." + accessor.getName() + "()"
					+ (module != null ? "; if the bean is in a named module, export its package to " + module : ""), e);
		} catch (InvocationTargetException e) {
			throw new IllegalStateException("Cannot read property '" + name + "' through " + accessor.getName() + "()", e.getCause());
		}
	}

	@Override
	public String toString() {
		return name;
	}

}
