# LocalDate and enum adapters

Issue: [#6](https://github.com/fvarrui/fxorms/issues/6)

## Goal

Render `ObjectProperty<LocalDate>` with a `DatePicker` and `ObjectProperty<SomeEnum>` with a `ComboBox`.

## Context

- Types without a built-in adapter fall back to `DefaultAdapter`: a read-only `Label`.
- `LocalDate` and enums are common in beans and end up there.

## Changes

- `LocalDateAdapter` (based on `ObjectAdapter`): `DatePicker` bound bidirectionally.
- `EnumAdapter`: `ComboBox` filled with the constants of the property's enum type (from `FieldAdapter.getGenericTypes()`). Needs its own `isPropertyType()` that accepts any enum, since `ObjectAdapter` matches one class.
- Register both in `FieldFactory`; both honour `@ReadOnly`.

## Acceptance

- `ObjectProperty<LocalDate>` renders as a `DatePicker` and syncs both ways.
- `ObjectProperty<SomeEnum>` renders as a `ComboBox` with all constants and syncs both ways.
- `@ReadOnly` fields of both types can't be edited.
- Tests cover both adapters.
