# @Label and @Order annotations

Issue: [#3](https://github.com/fvarrui/fxorms/issues/3)

## Goal

Let users set the text and the position of each form row.

## Context

- Labels are generated from the field name by `FormBuilder.getReadableStringFromCamelCase()`; they can't be changed or translated.
- Acronyms break: `userID` becomes "User i d:".
- Rows follow `getDeclaredFields()`, whose order the JDK doesn't guarantee.

## Changes

- `@Label("...")`: replaces the generated label text. Optionally a resource bundle key for translations.
- `@Order(n)`: rows with it come first, sorted by `n`; the rest keep their relative order after them.
- Keep consecutive capitals together in the generated label (`userID` → "User ID").

## Acceptance

- `@Label` replaces the generated label text.
- Rows follow `@Order` values.
- `userID` produces "User ID:".
- Tests cover all three.
