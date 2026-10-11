# Project

## What

f(x)orms is a Java library that builds JavaFX forms at runtime from a bean whose fields are JavaFX properties, and binds bean instances to them.

## What it does

- Generates one labelled row per property, with a control chosen by the property's type (text, number, boolean, date-time, list, map).
- Binds a bean instance to the form in both directions, and rebinds it to other instances of the same class.
- Honours field annotations such as `@ReadOnly` and `@Multiline`.
- Lets users plug in their own adapters to render specific types with specific controls.

## For whom

- Java developers building JavaFX desktop apps who want edit forms without hand-writing them.

## Goal

Turn a JavaFX bean into a working, bound edit form with one line of code: `new FormBuilder<>(Bean.class).build().bind(bean)`.
