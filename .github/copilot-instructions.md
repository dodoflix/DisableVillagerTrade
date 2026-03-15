# GitHub Copilot Instructions — DisableVillagerTrade

> **These instructions are authoritative.** Copilot must read this file at the start of every session and follow every rule here. If you notice that any rule is outdated, a pattern is missing, or the codebase has drifted from what is documented here, **update this file as part of the same task** — keep it accurate and complete at all times.

---

## File Sync Rules

When any of the files below are updated, you **must** also update all listed dependents in the same commit:

| File changed | Also update |
|---|---|
| `.github/workflows/ci.yml` or `cd.yml` renamed/replaced | This file's **CI/CD** section; `README.md` badge URLs |
| `README.md` CI/CD badge URLs | Verify workflow file names match actual `.github/workflows/` contents |
| `gradle.properties` (`modVersion`) | No additional sync needed — version is source of truth |
| `CHANGELOG.md` | Verify version matches latest git tag |

---

## Project Overview

A lightweight, multi-platform Minecraft mod/plugin that prevents players from trading with villagers. Supports **Bukkit/Spigot/Paper**, **Fabric**, **Forge**, and **NeoForge** from a single shared core.

---

## Architecture

### Module Layout

```
DisableVillagerTrade/          ← Root Gradle project
├── common/                    ← Platform-agnostic core (ZERO platform deps)
├── bukkit/                    ← Bukkit/Spigot/Paper implementation
├── fabric/                    ← Fabric (Quilt-compatible) implementation
├── forge/                     ← Forge implementation
└── neoforge/                  ← NeoForge implementation
```

### The "Common First" Rule

All business logic lives in `common/`. Platform modules only wire platform APIs to the common core — they contain **no business logic**.

- `common` has **no dependencies** on Bukkit, Fabric, Forge, or NeoForge
- Each platform's config class implements `ModConfig`
- Each platform's event handler calls `TradeBlocker` and reacts to its result
- When adding a feature, always start in `common/`, then add thin platform adapters

---

## Package Conventions

```
me.dodo.disablevillagertrade
├── common.*          ← shared interfaces, logic, utilities
├── bukkit.*          ← Bukkit-specific (listeners/, commands/, config/, update/)
├── fabric.*          ← Fabric-specific (mixin/, config/)
├── forge.*           ← Forge-specific (events/, config/)
└── neoforge.*        ← NeoForge-specific (events/, config/)
```

Sub-package names mirror each other across platforms (e.g. `*.config`, `*.events`/`*.listeners`).

---

## Naming Conventions

| Concept | Pattern | Example |
|---|---|---|
| Main mod/plugin class | `DisableVillagerTrade<Platform>` | `DisableVillagerTradeForge` |
| Platform config | `<Platform>Config` implements `ModConfig` | `BukkitConfig` |
| Event handler | `VillagerTradeHandler` or `VillagerTradeListener` | `VillagerTradeHandler` |
| Constants | `UPPER_SNAKE_CASE` static final fields in `Constants.java` | `MOD_ID`, `PERMISSION_BYPASS` |
| Booleans | `is*`, `has*`, `should*` prefixes | `isMessageEnabled()`, `shouldBlockTrade()` |
| Classes/Interfaces | PascalCase | `TradeBlocker`, `ModConfig` |
| Methods/Fields | camelCase | `getDisabledWorlds()` |

---

## Language & Java Style

- **Java 21**, no Kotlin, no records (for now), no sealed classes
- **UTF-8** source encoding (enforced in all `build.gradle.kts`)
- Private fields with public getters/setters — no public fields
- Utility classes (like `Constants`) must have a private constructor
- Prefer explicit null checks (`!= null`) over `Optional` for simple guards
- Fail silently in non-critical paths (e.g. update checker gracefully swallows API errors)

### Javadoc

All `public` classes and `public`/`protected` methods require Javadoc:

```java
/**
 * Determines if a trade interaction should be blocked.
 *
 * @param isVillager          whether the entity is a villager
 * @param profession          the villager's profession string
 * @param hasAI               whether the entity has AI enabled
 * @param hasGravity          whether the entity has gravity (filters fake NPCs)
 * @param worldName           the world the interaction occurred in
 * @param disabledWorlds      list of world names where the mod is disabled
 * @param hasBypassPermission whether the player holds the bypass permission
 * @return {@code true} if the trade should be blocked
 */
public boolean shouldBlockTrade(...) { ... }
```

Internal/private helpers do not require Javadoc but may have a short comment when non-obvious.

---

## Adding a New Feature Checklist

1. **common/** — implement logic; if it needs config, add a method to `ModConfig` interface
2. **common/** — add unit tests covering all branches
3. **bukkit/config/BukkitConfig** — implement the new `ModConfig` method
4. **bukkit/…** — wire into the relevant listener/command
5. **fabric/config/FabricConfig**, **forge/config/ForgeConfig**, **neoforge/config/NeoForgeConfig** — implement the new method
6. **fabric/**, **forge/**, **neoforge/** — wire into the relevant mixin/event handler
7. Update `plugin.yml` (Bukkit), `fabric.mod.json` (Fabric), and Forge/NeoForge `@Mod` resources as needed

---

## Testing

- Framework: **JUnit 5** + **JUnit Params** + **Mockito 5**
- JaCoCo coverage reports for `common` and `bukkit` modules
- Parameterized tests preferred for logic with multiple input combinations
- Use nested `@Nested` classes to group related scenarios
- Mock platform objects (e.g. `Player`, `Villager`) with Mockito — never use real platform APIs in unit tests
- Run tests: `./gradlew test` from the root

### Test Naming

```java
@Test
void shouldBlockTrade_whenVillagerHasProfession() { ... }

@Test
void shouldNotBlockTrade_whenWorldIsDisabled() { ... }
```

Format: `should<Outcome>_when<Condition>`

---

## Build System

- **Gradle** (multi-module); each platform is a subproject of the root
- Version catalog: `gradle/libs.versions.toml` — all dependency versions are defined here, never hardcode versions inline
- **Always verify the latest available version** before adding or updating any dependency. Check the relevant registry (Maven Central, Fabric Maven, Forge Maven, NeoForge Maven, SpigotMC Nexus) via the internet and use the latest stable release unless there is a documented reason to pin an older version. Never assume a version in the file is already the latest.
- `modVersion` is the single source of truth in root `gradle.properties`
- Bukkit and Forge use **Shadow JAR** with package relocation to avoid classpath conflicts:
  - Bukkit relocation: `me.dodo.disablevillagertrade.common` → `me.dodo.disablevillagertrade.bukkit.common`
  - Forge relocation: `me.dodo.disablevillagertrade.common` → `me.dodo.disablevillagertrade.forge.common`
- Fabric uses `include(project(":common"))` (bundled inside the mod JAR)
- NeoForge uses `jarJar(project(":common"))` (JAR-in-JAR)

### Useful Tasks

```bash
./gradlew test                      # Run all unit tests
./gradlew :common:test              # Common module tests only
./gradlew :bukkit:shadowJar         # Build Bukkit JAR
./gradlew :fabric:build             # Build Fabric JAR
./gradlew :forge:shadowJar          # Build Forge JAR
./gradlew :neoforge:build           # Build NeoForge JAR
```

---

## Conventional Commits

Every commit message **must** follow the [Conventional Commits](https://www.conventionalcommits.org/) specification. The `cd.yml` caller (via `mc-multiplatform-toolkit`) parses commit messages to determine the next version, so correct types are critical.

### Format

```
<type>(<scope>): <short description>

[optional body]

[optional footer(s)]
```

- **type** and **description** are mandatory
- **scope** is optional but strongly encouraged for platform-specific changes
- Description is lowercase, imperative mood, no trailing period
- Body/footer are free-form; use for breaking changes or issue references

### Types

| Type | When to use | Triggers version bump |
|---|---|---|
| `feat` | A new user-facing feature | Minor |
| `fix` | A bug fix | Patch |
| `chore` | Maintenance (deps, build, config, tooling) | None |
| `refactor` | Code restructuring without behavior change | None |
| `perf` | Performance improvement | Patch |
| `test` | Adding or fixing tests only | None |
| `docs` | Documentation only | None |
| `ci` | CI/CD workflow changes | None |
| `revert` | Reverting a previous commit | Patch |

A `BREAKING CHANGE:` footer or `!` after the type (e.g. `feat!:`) triggers a **Major** bump.

### Scopes

Use the platform name as scope for platform-specific changes:

| Scope | Applies to |
|---|---|
| `bukkit` | `bukkit/` module |
| `fabric` | `fabric/` module |
| `forge` | `forge/` module |
| `neoforge` | `neoforge/` module |
| `common` | `common/` module |
| `deps` | Dependency updates (used by Dependabot) |
| `ci` | Workflow files under `.github/workflows/` |

Omit scope when a change spans multiple platforms or is truly project-wide.

### Examples

```
feat(bukkit): add per-world trade blocking configuration
fix(forge): prevent NPE when villager profession is null
fix: calculate version before build to prevent false update notifications
chore(deps): bump net.neoforged.moddev from 2.0.126 to 2.0.134
chore: replace Renovate configuration with Dependabot
refactor(common): extract profession check into dedicated method
test(common): add parameterized tests for TradeBlocker world list logic
ci: add timeout to integration test server startup
feat!: rename permission node — existing configs must be updated

BREAKING CHANGE: `disablevillagertrade.bypass` renamed to `disablevillagertrade.admin.bypass`
```

### CI Flags (append to commit message)

- `[skip ci]` or `[ci skip]` — skips all CI jobs
- `[force deploy]` — forces a release even when no code changes are detected

### No Co-authored-by Trailers

Do **not** add `Co-authored-by:` or any automated-author trailers to commit messages. Keep commits clean with only the type/scope/description (and body/footer when needed for breaking changes or issue refs).

---

## CI/CD

Workflows are **thin callers** delegating to [`mc-multiplatform-toolkit`](https://github.com/dodoflix/mc-multiplatform-toolkit):

- **ci.yml** — calls `mc-multiplatform-toolkit/.github/workflows/ci.yml@main`; runs unit tests, parallel platform builds, and integration tests
- **cd.yml** — calls `mc-multiplatform-toolkit/.github/workflows/cd.yml@main`; automated versioning and Modrinth publishing; version bump is derived from commit types (see Conventional Commits above)

**Never edit CI logic directly in this repo.** Changes to build/test/release logic go in the toolkit repo (`~/Projects/mc-multiplatform-toolkit`). This project's workflow files are ≤ 25 lines each.

---

## Platform Entry Points

| Platform | Entry Point Class | Annotation/Base |
|---|---|---|
| Bukkit | `DisableVillagerTrade` | `extends JavaPlugin` |
| Fabric | `DisableVillagerTradeFabric` | `implements ModInitializer` |
| Forge | `DisableVillagerTradeForge` | `@Mod(Constants.MOD_ID)` |
| NeoForge | `DisableVillagerTradeNeoForge` | `@Mod(Constants.MOD_ID)` |

---

## Constants

All shared string literals (mod ID, permission nodes, config defaults) live in `common/Constants.java`. Never hardcode these values in platform code — always reference `Constants.*`.

---

## Key Design Decisions

- **AI + gravity check in `TradeBlocker`** — filters out NPC villagers from mods like Citizens so they are never blocked
- **Unemployed villagers (profession `NONE`) are never blocked** — deliberate UX choice
- **World-based disable list** — admins can whitelist specific worlds where trading is allowed
- **Bypass permission** — operators/admins can trade freely via `Constants.PERMISSION_BYPASS`
