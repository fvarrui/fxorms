---
name: release
description: Release a new fxorms version - pick the version, write release notes, build, GPG-sign and upload to the Maven Central Portal, tag, create the GitHub release with the signed jars and bump to the next snapshot. Also publishes an already tagged version to Maven Central (e.g. when Central failed earlier) and checks the state of a Central deployment. Use when the user wants to release, publish, deploy or tag a version, or asks about a Central deployment.
---

# Release fxorms

A release is a signed set of jars (binary, sources, javadoc, each with its `.asc`) published in two places: a GitHub release on tag `vX.Y.Z`, and Maven Central as `io.github.fvarrui:fxorms:X.Y.Z`. Everything in the repo, including release notes, is in English.

Publishing is outward-facing and hard to undo: a version on Maven Central can never be replaced or deleted. Confirm the version and the release notes with the user once before tagging, pushing or uploading anything.

## Setup on this machine

- **Maven:** `MVN="$(command -v mvn || ls -d ~/.m2/wrapper/dists/apache-maven-*/*/bin/mvn | sort -V | tail -1)"`.
- **Central Portal token:** server `central` in `~/.m2/settings.xml`. If it's missing, the same token is in `~/.gradle/gradle.properties` (`ossrhUsername`/`ossrhPassword`); copy it without printing it, and ask the user first.
- **GPG:** key `F113D3E7`, published on keyserver.ubuntu.com. The signing plugin reads the passphrase from `MAVEN_GPG_PASSPHRASE`; if it isn't set, take it from `signing.password` in `~/.gradle/gradle.properties` inside the same command, without printing it: `export MAVEN_GPG_PASSPHRASE="$(grep -E '^signing.password=' ~/.gradle/gradle.properties | cut -d= -f2-)"`.
- **Namespace:** the Central Portal account must have `io.github.fvarrui` verified (central.sonatype.com → Namespaces). Only the user can do that. Until it's verified, every upload fails with "Namespace 'io.github.fvarrui' is not allowed".

## New release

1. **Preconditions.** On `main`, clean working tree, up to date with `origin`. `.minispec/features/` should be empty (otherwise ask whether the pending work goes in). Run the `verify` skill, including the module smoke test.
2. **Version.** Semantic versioning; while the version is `0.x`, a breaking change bumps the minor version and anything else the patch. Breaking means a bean, an annotation or a custom adapter that worked before needs changes. Look at `git log vPREV..HEAD` and the issues closed since the last tag.
3. **Release notes.** Written for library users: a short intro, then what's new, what's fixed and what breaks (with what to change), linking issues as `[#N](https://github.com/fvarrui/fxorms/issues/N)`. End with the Maven dependency snippet and the signing key. Save them to a file in the scratchpad. **Confirm the version and the notes with the user.**
4. **Set the version.** `pom.xml` `<version>X.Y.Z</version>`, and the version in the README "Installation" section (Maven and Gradle snippets). Commit `Release X.Y.Z` and create an annotated tag `vX.Y.Z`. Don't push yet.
5. **Build, sign and upload.** `"$MVN" -B -P release clean deploy`. It runs the tests, builds the sources and javadoc jars, signs everything, uploads the bundle and waits until the portal has validated it (`autoPublish=false`, so it's not published yet). The log prints the `deploymentId`.
   - If it fails, run `python .claude/skills/release/central_status.py <deploymentId>` to see the errors. Fix them and deploy again (move the tag if the fix needs a commit). For a namespace error, stop and tell the user how to verify the namespace; carry on with the GitHub release only if they want, and note in the release notes that Maven Central is pending.
6. **Push.** `git push origin main vX.Y.Z`.
7. **GitHub release.** `gh release create vX.Y.Z --title "f(x)orms X.Y.Z" -F <notes>` with the six files from `target/`: `fxorms-X.Y.Z.jar`, `-sources.jar`, `-javadoc.jar` and their `.asc`.
8. **Publish on Central.** Tell the user to review and click Publish at https://central.sonatype.com/publishing/deployments. When they say it's done, `central_status.py` should show `PUBLISHED`; the artifact reaches https://repo1.maven.org/maven2/io/github/fvarrui/fxorms/ within about 30 minutes.
9. **Next snapshot.** Set `pom.xml` to the next minor `-SNAPSHOT` (e.g. `0.3.0-SNAPSHOT` after `0.2.0`), commit `Prepare X.Y+1.0-SNAPSHOT` and push.

## Publish an existing tag on Maven Central

When a version is already tagged and on GitHub, but its Central upload failed or was never done:

1. Check it isn't on Central already: `curl -s -o /dev/null -w "%{http_code}" https://repo1.maven.org/maven2/io/github/fvarrui/fxorms/X.Y.Z/` (404 means it isn't).
2. Build from the tag in a separate worktree, so `main` isn't touched: `git worktree add <scratchpad>/fxorms-X.Y.Z vX.Y.Z`, then `"$MVN" -B -P release clean deploy` inside it.
3. Remove the worktree: `git worktree remove <path>`.
4. After the user publishes it on the portal, edit the GitHub release notes to remove the "Maven Central publication is pending" note (`gh release edit vX.Y.Z -F <notes>`).

## Rules

- Never publish a version that differs from its tag: always build the tagged commit.
- Never retag a version that's already published on Central; release a new patch version instead.
- Keep the token and the GPG passphrase out of the output, commit messages and files in the repo.
