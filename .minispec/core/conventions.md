# Conventions

## Language

- Everything in the repo is in English: code, comments, commits, README, CLAUDE.md, `.minispec/`, GitHub issues.

## Code

- Match the existing style: tabs for indentation, one public class per file.
- Every supported property type gets its own `FieldAdapter` subclass in `adapters/`; don't special-case types in `FormBuilder` or `Form`.
- Adapters must honour `isReadOnly(Field)`: a read-only field must not be editable from the form.
- Don't swallow exceptions with `printStackTrace()`; throw an unchecked exception with a message that names the field.
- Keep the public API small: helpers used only inside the library stay package-private.

## Tests

- Every behaviour change comes with a test in `FormTest` (or a new test class next to it).
- Touch JavaFX only on the FX thread, through `FxThread.call(...)`/`FxThread.run(...)`.
- Tests must run headless (`mvn test` must not open windows); never call `Application.launch` from an enabled test.

## Issues

- Each feature or bugfix note in `.minispec/features/` has a GitHub issue (label `enhancement` or `bug`), linked from an `Issue:` line under its title.
- The commit that completes it says `Closes #N`, and the issue gets a comment summarizing the solution.
