# Architecture

## Flow

```
FormBuilder.build() → BeanProperty.of(class) → FieldFactory.findAdapter(property) → FieldAdapter.build(property) → Form (GridPane rows)
Form.bind(bean)     → BeanProperty.get(bean) (calls xxxProperty()) → FieldAdapter.bind(control, property)
```

## Key pieces

- **`BeanProperty`** — one property of the bean class, found through its public `xxxProperty()` accessor, inherited ones included (ADR-003). Gives the name, accessor return type and type arguments, whether it's writable, and annotations (accessor first, then the backing field with the same name). `BeanProperty.of(class)` returns them in form order: `@Order` first, then backing-field declaration order (superclass first), then the rest by name.
- **`FormBuilder<T>`** — takes the bean class; `build()` adds a row per `BeanProperty` to a two-column `GridPane`: a label (`@Label`, or generated from the name: "lastLogin" → "Last login", "userID" → "User ID") and the control built by the property's adapter.
- **`Form<T>`** — the built `GridPane`. Stores its own `BeanProperty → control` map (`addControl`), so several forms from one builder are independent. `bind(bean)` first unbinds the previous bean, then gets each property object by calling its accessor and calls the property's adapter; `bind(null)` and `unbind()` detach it. `getControl(name)` returns a property's control by property name.
- **`FieldFactory`** — ordered list of adapters. `findAdapter(property)` returns the first whose `isPropertyType(property)` is true, falling back to `DefaultAdapter` (read-only `Label` showing the value). The lookup is repeated at bind/unbind time, so an adapter's matching must be deterministic per property. `registerAdapter` (exposed as `FormBuilder.registerFieldAdapter`) inserts at the front, so custom adapters override built-ins (ADR-002).
- **`FieldAdapter<T extends Node, U>`** — the extension point: `build(BeanProperty)` creates the control, `doBind`/`doUnbind` connect it to the bean's property object. Matches when the accessor's return type is assignable to one of its types; built-ins list the read-only bases (`ReadOnlyStringProperty`, …) so both writable and read-only accessors match. `bindValue`/`unbindValue` bind both ways for a writable property, one way (control follows bean) for a read-only one.
- **`ObjectAdapter`** — base for `ObjectProperty<X>` adapters; also checks that the type argument is assignable to `X` (so `EnumAdapter` uses `Enum.class` to match every enum). Excludes list, map and set properties, which are object properties too.
- **Read-only handling** — `isReadOnly(BeanProperty)`: `@ReadOnly`, or an accessor that returns only a `ReadOnlyXxxProperty`. Read-only text inputs are made non-editable (text stays selectable); controls without an editable flag (`CheckBox`, `DatePicker`, `ComboBox`, list/map editors) are disabled.
- **`@Multiline(rows = n)`** — on a `StringProperty`, makes `StringAdapter` render a `TextArea`.
- **`TextBinding`** — two-way binding between a text control and a property through a `StringConverter`. When the converter throws, the property is left unchanged and the control gets the `:invalid` pseudo-class. Used by `NumberAdapter` and `LocalDateTimeAdapter`.
- **`@Format("pattern")`** — per-property format: a `DecimalFormat` pattern for numbers, a `DateTimeFormatter` pattern for date-times and dates (`DatePicker` converter). Defaults: integers without grouping, decimals with the locale's number format, date-times with the locale's MEDIUM format (keeps seconds).
- **Default stylesheet** — `FormBuilder` adds `/css/fxorms/form.css` to every form (`.form` style class); it paints `:invalid` text inputs red.
- **`ListEditor` / `MapEditor`** — custom controls used by `ListAdapter`/`MapAdapter`. Each loads an FXML file from `src/main/resources/fxml/fxorms/components/` and is its own controller (`Initializable`). They edit the bean's collection in place: removing a row removes it from the bean.

## Folder map

- `src/main/java/module-info.java` — module `io.github.fvarrui.fxorms`: exports every package, opens `components` to `javafx.fxml` (ADR-004).
- `src/main/java/io/github/fvarrui/fxorms/` — `FormBuilder`, `Form`, `BeanProperty`, `FieldFactory`.
- `.../adapters/` — one adapter per supported property type.
- `.../annotations/` — annotations for a property's accessor or backing field (`@ReadOnly`, `@Multiline`, `@Format`, `@Label`, `@Order`).
- `.../components/` — custom controls (`ListEditor`, `MapEditor`).
- `src/test/java/.../test/` — `FormTest` suite, `FxThread` helper, `EditUserApp`/`User` manual demo.
