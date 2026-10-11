# ADR-004: Named module, tests on the classpath

## Decision

The library is the named module `io.github.fvarrui.fxorms` (`src/main/java/module-info.java`), but surefire runs the tests on the classpath (`useModulePath=false`).

## Motivation

- Applications on the module path can now require the library, and JavaFX no longer warns about "Unsupported JavaFX configuration" there.
- Running the tests on the module path would need Monocle patched into `javafx.graphics` (`--patch-module`) and the test package opened to JUnit; that complexity buys little, since the library code is the same on both paths.
- Rejected: module-path tests. Module-specific behaviour (exports, FXML access, resources) was checked once with a sample modular app instead.

## Consequences

- An application module must export its bean packages to `io.github.fvarrui.fxorms` (forms call the beans' accessors reflectively); `BeanProperty.get` says so when access fails.
- `ListEditor`/`MapEditor` FXML controllers need `opens io.github.fvarrui.fxorms.components to javafx.fxml`; new FXML-backed components must live in an opened package.
- The classpath warning in test runs stays and is expected.
- Changes to `module-info.java` (new packages, FXML controllers) should be rechecked with a modular app, since the tests don't exercise the module path.
