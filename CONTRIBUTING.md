# Contributing to DisableVillagerTrade

Thank you for your interest in contributing to DisableVillagerTrade! This document provides guidelines and information for contributors.

## Getting Started

1. Fork the repository
2. Clone your fork locally
3. Create a new branch from `develop` for your feature or fix
4. Make your changes
5. Submit a pull request to `develop`

## Branch Strategy

```
develop → master
   ↑
 Your PR
```

| Branch | Purpose |
|--------|---------|
| `develop` | Active development, submit PRs here |
| `master` | Stable production releases |

**Always create PRs targeting `develop`**, not `master`.

## Development Setup

### Prerequisites

- Java 21
- Git

### Project Structure

This is a **composite multi-module Gradle project**. Each platform is an independent Gradle build that references `common` via composite build substitution.

```
DisableVillagerTrade/
├── common/        ← Platform-agnostic core logic (no platform deps)
├── bukkit/        ← Bukkit/Spigot/Paper implementation
├── fabric/        ← Fabric (Quilt-compatible) implementation
├── forge/         ← Forge implementation
├── neoforge/      ← NeoForge implementation
└── gradle/
    └── libs.versions.toml  ← Central version catalog for all platforms
```

All business logic belongs in `common/`. Platform modules only wire platform APIs to the common core.

### Building

Each platform has its own Gradle wrapper. Build from the platform directory:

```bash
# Build Bukkit (Gradle 8)
cd bukkit && ./gradlew shadowJar --no-daemon

# Build Fabric (Gradle 9)
cd fabric && ./gradlew build --no-daemon

# Build Forge (Gradle 8)
cd forge && ./gradlew shadowJar --no-daemon

# Build NeoForge (Gradle 8)
cd neoforge && ./gradlew build --no-daemon
```

Build outputs are in `<platform>/build/libs/`.

### Running Tests

```bash
# Common module tests
cd common && ./gradlew test --no-daemon

# Bukkit module tests
cd bukkit && ./gradlew test --no-daemon

# Or run all tests from root (runs common + bukkit)
./gradlew test --no-daemon
```

## Commit Message Convention

This project uses **Conventional Commits** for automatic versioning and changelog generation.

### Format

```
<type>(<scope>): <description>

[optional body]

[optional footer]
```

### Types & Version Bumps

| Type | Description | Version Bump |
|------|-------------|--------------|
| `feat` | A new user-facing feature | Minor |
| `fix` | A bug fix | Patch |
| `perf` | Performance improvement | Patch |
| `revert` | Reverting a previous commit | Patch |
| `refactor` | Code restructuring, no behavior change | None |
| `test` | Adding or fixing tests only | None |
| `docs` | Documentation only | None |
| `chore` | Maintenance (deps, build, config) | None |
| `ci` | CI/CD workflow changes | None |

A `BREAKING CHANGE:` footer or `!` after the type triggers a **Major** bump.

### Scopes

Use the platform name as scope for platform-specific changes:

| Scope | Applies to |
|-------|-----------|
| `bukkit` | `bukkit/` module |
| `fabric` | `fabric/` module |
| `forge` | `forge/` module |
| `neoforge` | `neoforge/` module |
| `common` | `common/` module |
| `deps` | Dependency updates |
| `ci` | Workflow files |

### Examples

```
feat(bukkit): add per-world trade blocking configuration
fix(forge): prevent NPE when villager profession is null
refactor(common): extract profession check into dedicated method
test(common): add parameterized tests for TradeBlocker world list logic
chore(deps): bump neoforge-moddev from 2.0.126 to 2.0.134
docs: update installation instructions for multi-platform setup
```

### Breaking Changes

```
feat!: rename permission node

BREAKING CHANGE: disablevillagertrade.bypass renamed to disablevillagertrade.admin.bypass
```

## Pull Request Process

1. **Create a descriptive PR title** following the commit convention format
2. **Ensure all checks pass** — Tests & Build + Integration Tests must be green
3. **Request a review** from maintainers

## Code Style

- Java 21, no Kotlin, no records
- Private fields with public getters/setters — no public fields
- All `public` classes and `public`/`protected` methods require Javadoc
- Test method naming: `should<Outcome>_when<Condition>`
- Use `@Nested` classes to group related test scenarios
- Mock platform APIs with Mockito — never use real platform APIs in unit tests

## Code Coverage

JaCoCo coverage is enforced for `common` and `bukkit` modules.

### Running Coverage Locally

```bash
cd common && ./gradlew test jacocoTestReport --no-daemon
# Report: common/build/reports/jacoco/test/html/index.html

cd bukkit && ./gradlew test jacocoTestReport --no-daemon
# Report: bukkit/build/reports/jacoco/test/html/index.html
```

Coverage reports are uploaded to Codecov on every PR.

## Adding a New Feature

1. Implement logic in `common/` — add to `ModConfig` interface if config is needed
2. Add unit tests in `common/` covering all branches
3. Implement `ModConfig` method in each platform's config class
4. Wire into the relevant listener/handler for each platform
5. Update `plugin.yml` (Bukkit), `fabric.mod.json` (Fabric), or Forge/NeoForge resources as needed

## Reporting Issues

When reporting bugs, please include:

- Minecraft version and platform (Bukkit/Fabric/Forge/NeoForge)
- Mod/plugin version
- Steps to reproduce
- Expected vs actual behavior
- Relevant console logs/errors

## Release Pipeline (For Maintainers)

| Branch | Purpose | Version | Published To |
|--------|---------|---------|--------------|
| `develop` | 🔧 Development builds | `1.2.3-dev.456` | GitHub (pre-release) |
| `master` | 🚀 Stable releases | `1.2.3` | GitHub + Modrinth |

Merge `develop` → `master` to trigger a stable release.

<details>
<summary><b>🔧 Required Secrets & Variables</b></summary>

#### Repository Variables (Settings → Secrets and variables → Actions → Variables)

| Variable | Description |
|----------|-------------|
| `MODRINTH_PROJECT_ID` | Your Modrinth project ID |

#### Repository Secrets (Settings → Secrets and variables → Actions → Secrets)

| Secret | How to Get |
|--------|------------|
| `MODRINTH_TOKEN` | [Modrinth Settings](https://modrinth.com/settings/pats) → Create PAT with `Write projects` scope |

</details>

## Questions?

Feel free to open an issue for any questions about contributing.

---

Thank you for contributing! 🎉

