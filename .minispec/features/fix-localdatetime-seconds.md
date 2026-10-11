# LocalDateTime fields lose seconds when edited

Issue: [#5](https://github.com/fvarrui/fxorms/issues/5)

## Problem

- A `LocalDateTime` field doesn't show its seconds.
- After editing the field, the property's seconds and nanoseconds are reset to zero (`2024-01-02T03:04:05` → `...T03:05:00`).
- Unparseable input only logs an exception.

## Cause

`LocalDateTimeAdapter` binds the `TextField` with a default `LocalDateTimeStringConverter`, which formats and parses with the locale's `FormatStyle.SHORT` (no seconds).

## Solution

To do. Proposed:

- Use a format that keeps seconds (`FormatStyle.MEDIUM` or ISO-8601), configurable per field.
- Reject or visibly mark unparseable input.
- Consider a `DatePicker` plus time field instead of free text.

## Verification

To do: a test that binds `2024-01-02T03:04:05`, edits the minute and checks the seconds are kept, plus a test for invalid input.
