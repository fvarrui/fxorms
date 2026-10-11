# ADR-001: Java 17 and JavaFX 21 LTS baseline

## Decision

The library targets Java 17 (`maven.compiler.release` 17) and depends on JavaFX 21 LTS (`javafx-controls` and `javafx-fxml`, `provided` scope).

## Motivation

- The original POM targeted Java 8 with JavaFX 11 and didn't build with Maven: `javafx-fxml` was missing and `Form` overrode `GridPane.getRowCount()`, which is `final` since JavaFX 9.
- JavaFX 21 is the latest LTS line that runs on JDK 17; JavaFX 22+ needs JDK 22+, which would exclude users on the current LTS JDKs (17 and 21).
- Java 17 is also the minimum for JUnit 6.
- Rejected: JavaFX 27 (latest) — it would force users onto a non-LTS JDK.

## Consequences

- Don't bump JavaFX past 21 without raising the Java baseline in the same change (and a new ADR).
- Java 17 language features (records, `instanceof` patterns, switch expressions) are allowed.
- JavaFX stays `provided`: the application using the library brings its own JavaFX runtime.
