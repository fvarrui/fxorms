# ADR-003: Discover properties through accessor methods

## Decision

A bean's form properties are its public, non-static, no-arg `xxxProperty()` methods returning a `ReadOnlyProperty` (found with `getMethods()`, so inherited ones included; methods declared in `javafx.*` classes excluded). Each is described by a `BeanProperty`, which is what adapters receive instead of a `java.lang.reflect.Field`.

## Motivation

- Reading private fields with `setAccessible(true)` ignored inherited properties, forced user modules to open their packages, and bypassed the bean's public API (a `ReadOnlyBooleanWrapper` exposed as `ReadOnlyBooleanProperty` was edited through the wrapper).
- The accessor's return type says whether the property is writable, which replaces the per-adapter `ReadOnly*Wrapper` checks.
- Rejected: supporting both fields and accessors. It would keep two discovery paths alive forever; beans without accessors don't follow the JavaFX convention.

## Consequences

- Breaking change: beans without `xxxProperty()` methods produce empty forms, and custom adapters must implement `build(BeanProperty)` / `isPropertyType(BeanProperty)` / `isReadOnly(BeanProperty)`.
- Read-only = `@ReadOnly` or an accessor that returns only a `ReadOnlyXxxProperty`. Adapters list the read-only base types (`ReadOnlyStringProperty`, …) and bind one way when the bean's property isn't a `Property` (`FieldAdapter.bindValue`).
- Annotations are read from the accessor, or else from the field with the same name (anywhere in the hierarchy), so they can go on either.
- Row order: `@Order` first (by value), then by backing-field declaration order (superclass first), then properties without backing field by name. `getMethods()` order is unspecified, so the backing field is the only source-order hint.
- `FieldAdapter` keeps its name despite no longer taking a `Field`, to limit the churn.
