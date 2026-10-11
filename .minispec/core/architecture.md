# Architecture

## Flow

```
FormBuilder.build() → FieldFactory.findAdapter(field) → FieldAdapter.build(field) → Form (GridPane rows)
Form.bind(bean)     → read each property field → FieldAdapter.bind(control, property)
```

## Key pieces

- **`FormBuilder<T>`** — takes the bean class; `build()` collects every declared field whose type is assignable to `javafx.beans.property.Property` and adds a row per field to a two-column `GridPane`: a label from the field name (camelCase → "Camel case:") and the control built by the field's adapter.
- **`Form<T>`** — the built `GridPane`. Stores its own `Field → control` map (`addControl`), so several forms from one builder are independent. `bind(bean)` first unbinds the previous bean, then reads each field reflectively (`setAccessible(true)`) and calls the field's adapter; `bind(null)` and `unbind()` detach it.
- **`FieldFactory`** — ordered list of adapters. `findAdapter(field)` returns the first whose `isPropertyType(field)` is true, falling back to `DefaultAdapter` (read-only `Label` showing the value). The lookup is repeated at bind/unbind time, so an adapter's matching must be deterministic per field. `registerAdapter` (exposed as `FormBuilder.registerFieldAdapter`) inserts at the front, so custom adapters override built-ins (ADR-002).
- **`FieldAdapter<T extends Node, U>`** — the extension point: `build(Field)` creates the control, `doBind`/`doUnbind` connect it to the property. Matches when the field's declared type is assignable to one of its types, so `SimpleStringProperty` and `ReadOnlyStringWrapper` both match `StringAdapter`.
- **`ObjectAdapter`** — base for `ObjectProperty<X>` adapters; also checks the field's generic type argument (see `LocalDateTimeAdapter`).
- **Read-only handling** — per adapter via `isReadOnly(Field)`: the base checks `@ReadOnly`; adapters also treat `ReadOnly*Wrapper` fields as read-only. Editable text controls use bidirectional bindings; read-only ones are non-editable or disabled.
- **`@Multiline(rows = n)`** — on a `StringProperty`, makes `StringAdapter` render a `TextArea`.
- **`ListEditor` / `MapEditor`** — custom controls used by `ListAdapter`/`MapAdapter`. Each loads an FXML file from `src/main/resources/fxml/fxorms/components/` and is its own controller (`Initializable`). They edit the bean's collection in place: removing a row removes it from the bean.

## Folder map

- `src/main/java/io/github/fvarrui/fxorms/` — `FormBuilder`, `Form`, `FieldFactory`.
- `.../adapters/` — one adapter per supported property type.
- `.../annotations/` — field annotations (`@ReadOnly`, `@Multiline`).
- `.../components/` — custom controls (`ListEditor`, `MapEditor`).
- `src/test/java/.../test/` — `FormTest` suite, `FxThread` helper, `EditUserApp`/`User` manual demo.
