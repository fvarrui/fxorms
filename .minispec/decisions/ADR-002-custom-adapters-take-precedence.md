# ADR-002: Custom adapters take precedence over built-ins

## Decision

`FieldFactory.registerAdapter` inserts the adapter at the front of the list, so the first matching adapter is always the most recently registered custom one, then the built-ins, then `DefaultAdapter`.

## Motivation

- Before, custom adapters were appended after the built-ins, so they could never change how a type with a built-in adapter is rendered (for example, a `StringProperty` as a `PasswordField`).
- Adapter matching uses assignability, so several adapters can match the same field; a fixed precedence rule keeps the choice predictable.

## Consequences

- A custom adapter that matches broadly (for example, every `Property`) hides all built-ins; adapters should match as narrowly as possible.
- If two custom adapters match the same field, the one registered last wins.
- `FieldFactory.getAdapters()` returns an unmodifiable view; adapters are only added through `registerAdapter`.
