#!/usr/bin/env bash
# Smoke test of fxorms on the module path. The test suite runs on the classpath
# (ADR-004), so this is the only check of module-info.java, FXML access from the
# module and resource loading. Needs a display (it doesn't use Monocle).
set -euo pipefail

here="$(cd "$(dirname "$0")" && pwd)"
root="$(git -C "$here" rev-parse --show-toplevel)"
MVN="${MVN:-$(command -v mvn || ls -d ~/.m2/wrapper/dists/apache-maven-*/*/bin/mvn 2>/dev/null | sort -V | tail -1)}"

"$MVN" -B -q -f "$root/pom.xml" package -DskipTests
jar="$(ls "$root"/target/fxorms-*.jar | grep -v -e '-sources' -e '-javadoc' | head -1)"
fxver="$(grep -o '<javafx.version>[^<]*' "$root/pom.xml" | cut -d'>' -f2)"

case "$(uname -s)" in
	MINGW*|MSYS*|CYGWIN*) classifier=win ;;
	Darwin) classifier=$([ "$(uname -m)" = arm64 ] && echo mac-aarch64 || echo mac) ;;
	*) classifier=linux ;;
esac

# Windows java/javac need ';' and Windows paths
if command -v cygpath >/dev/null; then sep=';'; topath() { cygpath -w "$1"; }; else sep=':'; topath() { echo "$1"; }; fi

mp="$(topath "$jar")"
for module in base graphics controls fxml; do
	fxjar=~/.m2/repository/org/openjfx/javafx-$module/$fxver/javafx-$module-$fxver-$classifier.jar
	[ -f "$fxjar" ] || { echo "Missing $fxjar (run mvn compile first)"; exit 1; }
	mp="$mp$sep$(topath "$fxjar")"
done

out="$(mktemp -d)"
trap 'rm -rf "$out"' EXIT
javac --release 17 -d "$out" --module-path "$mp" --module-source-path "$(topath "$here/modapp")" -m sample
output="$(java --module-path "$mp$sep$(topath "$out")" -m sample/com.example.app.Main 2>&1)"
echo "$output"

fail() { echo "MODULE SMOKE TEST FAILED: $1"; exit 1; }
grep -q 'row: Tags:' <<<"$output" || fail "form rows missing (the FXML-backed ListEditor row must be there)"
grep -q 'bound name: Grace' <<<"$output" || fail "binding didn't update the bean"
grep -q 'module: io.github.fvarrui.fxorms' <<<"$output" || fail "library not loaded as a named module"
grep -q 'Unsupported JavaFX configuration' <<<"$output" && fail "JavaFX warns about the classpath configuration"
grep -qE 'Exception|Error:' <<<"$output" && fail "exception during the run"
echo "MODULE SMOKE TEST PASSED"
