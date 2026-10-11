# Number field formatting and validation

Issue: [#4](https://github.com/fvarrui/fxorms/issues/4)

## Goal

Show integers without grouping separators by default and give feedback on invalid numeric input.

## Context

- `NumberAdapter` binds a `TextField` with a default `NumberStringConverter`, which uses the locale's grouping: an `id` of 12345 shows as "12,345" or "12.345".
- Unparseable input (for example "abc") only logs an exception; the property keeps its old value and the UI shows nothing.

## Changes

- No grouping for `IntegerProperty`/`LongProperty` by default.
- Allow a per-field format (for example a `@Format` annotation).
- Use a `TextFormatter` that rejects invalid input, or mark the field invalid (CSS pseudo-class) while it can't be parsed.

## Acceptance

- An `IntegerProperty` with value 12345 shows "12345" by default.
- Invalid input is rejected or visibly marked as invalid.
- Tests cover both.
