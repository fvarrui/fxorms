# Glossary

## Bean

The user's class exposing JavaFX properties through `xxxProperty()` accessors (`StringProperty`, `IntegerProperty`, …). A form is built from a bean class and bound to bean instances.

## Property

A public, non-static, no-arg `xxxProperty()` method of the bean returning a `ReadOnlyProperty`, described by `BeanProperty`. Each one becomes a row of the form; fields and other methods are ignored.

## Backing field

The bean field with the same name as a property (anywhere in the class hierarchy). Optional; used for annotations and row order.

## Form

The `GridPane` built by `FormBuilder` for a bean class: one row per property, a label and a control.

## Adapter

A `FieldAdapter` subclass that knows how to render one family of property types: it builds the control and binds it to the property.

## Built-in adapter

An adapter registered by default in `FieldFactory` (string, number, date-time, date, enum, boolean, map, list). `DefaultAdapter` is the fallback, not a built-in.

## Custom adapter

An adapter registered by the user with `FormBuilder.registerFieldAdapter`. It takes precedence over the built-in adapters.

## Bind / unbind

Connecting a form's controls to a bean instance's properties, and disconnecting them. A form is bound to at most one bean at a time.

## Read-only property

A property the form must not let the user edit: annotated with `@ReadOnly`, or whose accessor returns only a `ReadOnlyXxxProperty`.
