---
name: add-adapter
description: Add a built-in fxorms adapter so a new property type (e.g. ObjectProperty<Color>, SetProperty, LocalTime) gets its own control in generated forms, following the project's conventions for read-only handling, binding, validation, registration, tests and docs. Use when the user wants the library to support or render a new property type, or to change which control a type gets.
---

# Add a built-in adapter

An adapter renders one family of property types: it decides which properties it supports, builds their control and binds it to the bean. Read `.minispec/core/conventions.md` and `.minispec/core/architecture.md` first (MiniSpec reading contract). If the work comes from a MiniSpec feature, implement it through `minispec-implement` and use this skill for the adapter itself.

## 1. Pick the base class

- **`ObjectProperty<X>`** → extend `ObjectAdapter<Control, ObservableValue<X>>` and call `super(X.class)`. It matches when the type argument is assignable to `X` (`Enum.class` matches every enum) and never matches list, map or set properties.
- **Any other property type** → extend `FieldAdapter<Control, ObservableValue<V>>` and pass the **read-only base types** to `super(...)`, e.g. `ReadOnlySetProperty.class`, never `SetProperty.class`, so read-only accessors match too.
- Check that no existing adapter in `FieldFactory` already matches the type; the first match wins.

## 2. Implement

```java
/**
 * Renders {@code ObjectProperty<Color>} as a {@link ColorPicker}, disabled when
 * the property is read-only.
 */
public class ColorAdapter extends ObjectAdapter<ColorPicker, ObservableValue<Color>> {

	/**
	 * Creates the adapter for {@code Color} object properties.
	 */
	public ColorAdapter() {
		super(Color.class);
	}

	@Override
	public ColorPicker build(BeanProperty property) {
		ColorPicker control = new ColorPicker();
		control.setDisable(isReadOnly(property));
		return control;
	}

	@Override
	public void doBind(ColorPicker control, ObservableValue<Color> property) {
		bindValue(control.valueProperty(), property);
	}

	@Override
	public void doUnbind(ColorPicker control, ObservableValue<Color> property) {
		unbindValue(control.valueProperty(), property);
	}

}
```

Rules:

- **Read-only:** use `isReadOnly(property)`, which covers `@ReadOnly` and read-only accessors. Text inputs get `setEditable(false)` (text stays selectable); other controls get `setDisable(true)`.
- **Binding:** `bindValue`/`unbindValue` bind both ways for writable properties and one way for read-only ones. Never call `bindBidirectional` directly: read-only properties aren't `Property`.
- **Text input of non-string values:** bind with `TextBinding.bind(control, property, converter)` and unbind with `TextBinding.unbind(control)`. The converter's `fromString` must throw on invalid text and reject partial input ("12abc"), so the field is marked `:invalid`. Don't use `Bindings.bindBidirectional(text, property, converter)`.
- **Annotations:** read them with `property.getAnnotation(...)` (accessor first, then backing field). Support `@Format` if the type has a textual format.
- **No per-control state in fields:** one adapter instance serves every form and property. Keep per-control data in `control.getProperties()` (see `NumberAdapter`).
- New annotations go in `annotations/` with `@Target({ ElementType.FIELD, ElementType.METHOD })` and `RUNTIME` retention.

## 3. Register

Add an instance to the built-in list in `FieldFactory`. Put it before any adapter that could also match the type.

## 4. Test

In `FormTest` (or a new test class next to it), with a bean that has `xxxProperty()` accessors:

- the property gets the expected control type (`control(form, "Label", Type.class)`);
- bean → control and control → bean sync after `bind`;
- `@ReadOnly` and a read-only accessor (`ReadOnlyXxxWrapper` exposed as `ReadOnlyXxxProperty`) can't be edited, and the control still follows the bean;
- `@Format` and invalid input, if they apply.

Touch JavaFX only through `FxThread.call`/`run`.

## 5. Document

- Javadoc on the class and constructor (the build fails without them).
- README: add a row to the "Supported types" table.
- `.minispec/core/glossary.md` ("Built-in adapter" list) and `.minispec/core/project.md` (control types list).
- `.minispec/core/architecture.md` only if the adapter introduces a new concept.

## 6. Verify

Run the `verify` skill (tests and Javadoc; the module smoke test only if packages or FXML changed).
