# Discover properties through accessor methods

Issue: [#1](https://github.com/fvarrui/fxorms/issues/1)

## Goal

Find a bean's properties through its public `xxxProperty()` methods instead of its private fields.

## Context

- `FormBuilder.getFields()` uses `getDeclaredFields()` and `Form` reads them with `setAccessible(true)`.
- Properties declared in a superclass are ignored.
- On the module path, every user module has to open its bean packages to fxorms.
- It bypasses the bean's API: the demo `User` exposes `enabledProperty()` as a read-only view, but the form reads the private `ReadOnlyBooleanWrapper`.

## Changes

- Discover properties with `getMethods()`: public, no-arg methods named `*Property` returning a `ReadOnlyProperty`.
- Writable when the return type is a `Property`; read-only when it's only a `ReadOnlyProperty`. This replaces the `ReadOnly*Wrapper` checks in the adapters.
- Adapters receive the property's metadata (name, type, generic type, annotations) instead of a `java.lang.reflect.Field`.
- Read `@ReadOnly`/`@Multiline` from the accessor method, and from the backing field with the same name for compatibility.
- Decide the row order (see [label-and-order-annotations](label-and-order-annotations.md)); record the new adapter API in an ADR.

## Acceptance

- Properties declared in a superclass appear in the form.
- A property exposed only as `ReadOnlyXxxProperty` is rendered as read-only.
- `@ReadOnly` and `@Multiline` still work.
- All existing `FormTest` tests pass, adapted to the new adapter API.
