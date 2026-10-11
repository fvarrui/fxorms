---
name: verify
description: Run the fxorms checks - headless test suite, Javadoc lint (doclint=all, fails on any warning) and, when module-info.java, packages, FXML or resources changed, a smoke test of the library on the module path. Use after code changes and before commits, pull requests and releases, or when the user asks to check, test or verify the project.
---

# Verify fxorms

Runs the project's checks and reports what passes and what fails. Never "fix" a failure by weakening a check (disabling a test, relaxing doclint, skipping the smoke test); fix the cause or report it.

## Maven

`mvn` may not be on the PATH on this machine. Resolve it once and reuse it:

```bash
MVN="$(command -v mvn || ls -d ~/.m2/wrapper/dists/apache-maven-*/*/bin/mvn | sort -V | tail -1)"
```

## Steps

1. **Tests.** `"$MVN" -B test`. Expect `Tests run: N, Failures: 0, Errors: 0, Skipped: 2` (the skipped ones are `AppTest`, the manual demo, and `ScreenshotsTest`, which only runs with `-Dscreenshots=true`). The warning "Unsupported JavaFX configuration: classes were loaded from 'unnamed module'" is expected: tests run on the classpath (ADR-004). For one test: `-Dtest=FormTest#methodName`.
2. **Javadoc.** `"$MVN" -B compile javadoc:javadoc` (without `compile`, a clean checkout fails with "named and unnamed modules"). It fails on any warning; list them with `| grep -E "warning|error"`. Every public and protected type and member needs complete Javadoc (`@param`, `@return`, `@throws`), and every package a `package-info.java`.
3. **Module path**, only when the change touches `module-info.java`, adds or renames packages, adds FXML-backed components, or changes how resources are loaded: run `.claude/skills/verify/module-smoke-test.sh`. It builds the jar, compiles the sample application module in `modapp/` against it and runs it; it needs a display. It must end with `MODULE SMOKE TEST PASSED`.

## Report

Give the user one line per check: passed, failed (with the failing tests or warnings) or skipped (and why, e.g. "module smoke test skipped: no module or package changes").

## When a check fails

- **Test failure:** read the assertion message; tests are named after the behaviour they check. Touch JavaFX only on the FX thread (`FxThread.call`/`run`) when adding or fixing tests.
- **Javadoc warning:** add the missing comment or tag; don't add `@SuppressWarnings` or change the plugin configuration.
- **Smoke test:** a missing `exports`/`opens` in `module-info.java` is the usual cause; new FXML controllers need their package opened to `javafx.fxml`.
