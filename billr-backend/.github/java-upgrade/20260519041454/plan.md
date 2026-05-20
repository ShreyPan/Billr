# Upgrade Plan: billr-backend (20260519041454)

- **Generated**: 2026-05-19 09:45:45 +05:30
- **HEAD Branch**: N/A
- **HEAD Commit ID**: N/A
- **Version Control**: Not available in this workspace; changes will not be tracked by git during this upgrade.

## Available Tools

**JDKs**
- JDK 21.0.10: C:\Program Files\Eclipse Adoptium\jdk-21.0.10.7-hotspot\bin (current project JDK, used by baseline)
- JDK 25.0.2: C:\Users\Utkarsh Pandey\.jdk\jdk-25\bin (target JDK, used by final validation)

**Build Tools**
- Maven Wrapper: 3.9.15 -> **<TO_BE_UPGRADED>** to 4.0.0+ (required for Java 25)
- Standalone Maven: not available, wrapper will be used

## Guidelines

- No additional user-specified constraints were provided.

> Note: You can add any specific guidelines or constraints for the upgrade process here if needed, bullet points are preferred.

## Options

- Working branch: appmod/java-upgrade-20260519041454
- Run tests before and after the upgrade: true

## Upgrade Goals

- Upgrade the application runtime to Java 25, the latest LTS version.

## Technology Stack

| Technology/Dependency | Current | Min Compatible Version | Why Incompatible |
| --------------------- | ------- | ---------------------- | ---------------- |
| Java | 21 | 25 | User requested latest LTS runtime |
| Spring Boot parent | 3.4.5 | 3.4.5 | Retained; no framework migration required for this Java-only upgrade |
| Maven Wrapper | 3.9.15 | 4.0.0 | Maven 3.9.x is not sufficient for Java 25 |
| maven-compiler-plugin | inherited | 3.11.0 | Older compiler lines do not reliably target Java 21+ bytecode |
| maven-surefire-plugin | inherited | 3.1.0 | Modern test execution is required for current JDK support |
| spring-boot-starter-web | 3.4.5 | 3.4.5 | Compatible; no change required |
| spring-boot-starter-security | 3.4.5 | 3.4.5 | Compatible; no change required |
| spring-boot-starter-data-jpa | 3.4.5 | 3.4.5 | Compatible; no change required |
| spring-boot-starter-validation | 3.4.5 | 3.4.5 | Compatible; no change required |
| spring-boot-starter-actuator | 3.4.5 | 3.4.5 | Compatible; no change required |
| jjwt 0.12.6 | 0.12.6 | 0.12.6 | Compatible; no change required |
| Lombok | inherited | 1.18.30+ | Keep on a current Lombok line to avoid JDK annotation-processing issues |
| javax.* runtime/module APIs | not used | N/A | Source scan found no usage that blocks Java 25 |

## Derived Upgrades

- Maven Wrapper must move to Maven 4.0.0+ because Java 25 requires a Maven 4-compatible toolchain.
- `java.version` must be raised from 21 to 25 so Spring Boot compiles and runs against the target LTS runtime.
- Compiler and test plugins should remain on Spring Boot-managed versions unless validation shows they need an explicit bump for Java 25.
- No JDK source-compatibility rewrites are currently required; the initial scan found no internal JDK APIs, reflective encapsulation workarounds, or removed `javax.*` APIs in the application sources.

## Upgrade Steps

- Step 1: Upgrade Maven Wrapper for Java 25
  - **Rationale**: Java 25 needs a Maven 4-compatible build tool, and the repository currently pins the wrapper to Maven 3.9.15.
  - **Changes to Make**:
    - Update `.mvn/wrapper/maven-wrapper.properties` to a Maven 4.0.0+ distribution URL.
    - Keep the build on the wrapper so the project remains self-contained.
    - Verify the wrapper starts successfully before changing the runtime level.
  - **Verification**: Run `.mvnw.cmd -version` with JDK 21, expected result is a Maven 4.x wrapper launch without errors.

- Step 2: Setup Baseline on Java 21
  - **Rationale**: Capture the current behavior before the runtime bump so any regressions can be attributed to the Java 25 change rather than pre-existing issues.
  - **Changes to Make**:
    - No source changes; this is a clean build-and-test checkpoint.
    - Record the current compile/test outcome against the existing Java 21 codebase.
  - **Verification**: Run `.mvnw.cmd clean compile test-compile -q && .mvnw.cmd clean test -q` with JDK 21, expected result is a successful baseline or a documented pre-upgrade failure state.

- Step 3: Raise Runtime Target to Java 25
  - **Rationale**: This is the primary product goal and should happen after the build tool is capable of running on Java 25.
  - **Changes to Make**:
    - Change `<java.version>` in `pom.xml` from `21` to `25`.
    - Keep the Spring Boot parent and dependency set unchanged unless compilation exposes a specific incompatibility.
    - Fix any compile-time issues revealed by the Java 25 compiler.
  - **Verification**: Run `.mvnw.cmd clean test-compile -q` with JDK 25, expected result is a clean compile of main and test sources.

- Step 4: Final Validation on Java 25
  - **Rationale**: Confirm the upgraded runtime works end-to-end and that the full test suite passes on the target LTS.
  - **Changes to Make**:
    - Fix any build or test failures discovered during final validation.
    - Remove any temporary workarounds introduced during the upgrade if they are no longer needed.
  - **Verification**: Run `.mvnw.cmd clean test -q` with JDK 25, expected result is 100% test pass rate and a clean final build.

## Key Challenges

- **Maven wrapper and JDK 25 compatibility**
  - **Challenge**: The current wrapper is pinned to Maven 3.9.15, which is not sufficient for Java 25.
  - **Strategy**: Upgrade the wrapper first so the rest of the build can execute on the target runtime.

- **Potential compiler/test-plugin drift**
  - **Challenge**: Java 25 may surface plugin-version issues even when application code is unchanged.
  - **Strategy**: Prefer Spring Boot-managed plugin versions first, then bump only if final validation exposes a concrete failure.

- **Runtime-only Java uplift**
  - **Challenge**: The codebase is small and mostly framework-driven, so failures may come from tooling rather than application code.
  - **Strategy**: Keep code changes minimal and rely on compile/test feedback to decide whether any source edits are necessary.

- **No source-level JDK incompatibilities found in the initial scan**
  - **Challenge**: There is no obvious application code to rewrite for Java 25.
  - **Strategy**: Treat this as a low-risk code migration; validate behavior through the full test suite after the runtime bump.
