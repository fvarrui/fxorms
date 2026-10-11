# Distinct look for read-only text inputs

Issue: [#7](https://github.com/fvarrui/fxorms/issues/7)

## Goal

Make read-only text inputs look different from editable ones in generated forms.

## Context

- Read-only string, number and date-time properties are rendered as non-editable `TextField`/`TextArea`, so their text stays selectable.
- They look exactly like editable fields (see `assets/examples/read-only.png`: Id, Created at and Logins), so users can't tell which fields they can change.
- Other read-only controls (`CheckBox`, `DatePicker`, `ComboBox`, list and map editors) are disabled, so they already look greyed out.
- JavaFX sets the `:readonly` pseudo-class on non-editable text inputs; the default stylesheet `/css/fxorms/form.css` only styles `:invalid`.

## Changes

- Add a `.form .text-input:readonly` rule to `form.css`: a greyed inner background, no focus highlight; text stays selectable and readable.
- Regenerate the README screenshots (`ScreenshotsTest`) and mention `:readonly` in the README "Styling" section.

## Acceptance

- In a rendered form, a read-only text field's background differs from an editable one's, and editable fields look as before.
- Read-only text can still be selected and copied.
- A test checks the rendered difference; the README screenshot shows it.
