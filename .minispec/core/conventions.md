# Conventions

## Language

- Everything in the repo is in English: code, comments, commits, README, CLAUDE.md, `.minispec/`, GitHub issues.

## Code

- Match the existing style: tabs for indentation, one public class per file.
- Every supported property type gets its own `FieldAdapter` subclass in `adapters/`; don't special-case types in `FormBuilder` or `Form`.
- Adapters must honour `isReadOnly(BeanProperty)`: a read-only property must not be editable from the form.
- Adapters list the read-only base property types (`ReadOnlyStringProperty`, not `StringProperty`) and bind with `bindValue`/`unbindValue` (or `TextBinding`), so read-only accessors work too.
- Text-input adapters that convert values bind through `TextBinding` with a converter that throws on invalid text. Don't use `Bindings.bindBidirectional(text, property, converter)`: it only logs parse errors, so users get no feedback.
- Converters must reject partial input (e.g. "12abc"), not parse a prefix.
- Don't swallow exceptions with `printStackTrace()`; throw an unchecked exception with a message that names the property.
- Keep the public API small: helpers used only inside the library stay package-private.

## Tests

- Every behaviour change comes with a test in `FormTest` (or a new test class next to it).
- Touch JavaFX only on the FX thread, through `FxThread.call(...)`/`FxThread.run(...)`.
- Tests must run headless (`mvn test` must not open windows); never call `Application.launch` from an enabled test.

## Issues

- Each feature or bugfix note in `.minispec/features/` has a GitHub issue (label `enhancement` or `bug`), linked from an `Issue:` line under its title.
- The commit that completes it says `Closes #N`, and the issue gets a comment summarizing the solution.
