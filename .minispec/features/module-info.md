# Add module-info.java

Issue: [#2](https://github.com/fvarrui/fxorms/issues/2)

## Goal

Ship the library as a named Java module that works on the module path.

## Context

- There is no `module-info.java`; the library only works on the classpath.
- With JavaFX on the classpath, JavaFX warns "Unsupported JavaFX configuration: classes were loaded from 'unnamed module'".
- Depends on [property-accessor-discovery](property-accessor-discovery.md): while forms read private fields, user modules must open their bean packages to fxorms.

## Changes

- Add `module io.github.fvarrui.fxorms`: `requires transitive javafx.controls`, `requires javafx.fxml`.
- Export `fxorms`, `adapters`, `annotations` and `components`.
- `opens io.github.fvarrui.fxorms.components to javafx.fxml` (FXML controllers), and keep the FXML resources loadable from inside the module.
- Run the tests on the module path (surefire), keeping them headless.

## Acceptance

- The library builds as a named module and `mvn test` passes on the module path.
- A sample app on the module path builds and binds a form with no "Unsupported JavaFX configuration" warning.
