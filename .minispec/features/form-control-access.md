# Access a property's control from the form

Issue: [#8](https://github.com/fvarrui/fxorms/issues/8)

## Goal

Let applications get the control that a form built for a given property.

## Context

- `Form` keeps a private `BeanProperty → control` map; there is no public way to get a row's control.
- Applications may need it to focus a field, add a tooltip, listen to the control, or check its state in tests.
- The examples work around it with `Examples.controlOf(form, label)`, which searches the grid by label text and breaks if the label changes.

## Changes

- Add `Form.getControl(String propertyName)`, returning the control of that property (the name as in `BeanProperty.getName()`, e.g. `"email"` for `emailProperty()`).
- Unknown property name: `IllegalArgumentException` naming the property and the class.
- Javadoc with an example (focus a field).
- Replace `Examples.controlOf` with `getControl` in the examples, and use it in the README validation snippet.

## Acceptance

- `form.getControl("email")` returns the `TextField` of the Email row, before and after `bind`.
- An unknown name throws `IllegalArgumentException`.
- Tests cover both; the examples no longer search controls by label.
