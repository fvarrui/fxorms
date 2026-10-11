# Glossary

## Bean

The user's class whose fields are JavaFX properties (`StringProperty`, `IntegerProperty`, …). A form is built from a bean class and bound to bean instances.

## Property field

A bean field whose declared type is assignable to `javafx.beans.property.Property`. Each one becomes a row of the form; other fields are ignored.

## Form

The `GridPane` built by `FormBuilder` for a bean class: one row per property field, a label and a control.

## Adapter

A `FieldAdapter` subclass that knows how to render one family of property types: it builds the control and binds it to the property.

## Built-in adapter

An adapter registered by default in `FieldFactory` (string, number, date-time, date, enum, boolean, map, list). `DefaultAdapter` is the fallback, not a built-in.

## Custom adapter

An adapter registered by the user with `FormBuilder.registerFieldAdapter`. It takes precedence over the built-in adapters.

## Bind / unbind

Connecting a form's controls to a bean instance's properties, and disconnecting them. A form is bound to at most one bean at a time.

## Read-only field

A property field the form must not let the user edit: annotated with `@ReadOnly`, or declared as a `ReadOnly*Wrapper`.
