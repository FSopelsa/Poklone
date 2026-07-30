# Poklone agent guide

## Scope and architecture

- Poklone is a Java 21/Maven learning project with one completed terminal-battle vertical slice; it is an original creature battler, not a Pokémon asset or lore clone. Read `README.md` and `docs/PROJECT_PLAN.md` before changing roadmap-scale behavior.
- Preserve the boundary: `src/main/java/se/poklone/domain/` owns battle state and rules and must not depend on console, graphics, files, or framework APIs. `application/` adapts input/output and assembles content; `Main` selects the interactive or `--demo` entry point.
- The flows are `Main -> SwingGame/BattlePanel -> GameContent -> Battle` and `Main -> ConsoleGame -> GameContent -> Battle`. Both adapters render `TurnResult`/`AttackResult`; keep UI text and widgets out of `Battle`.
- The design deliberately starts with small, explicit types: immutable `record`s for `Move`, `Trainer`, and result data; mutable health only in `Creature`; and `Battle` as the turn coordinator. Do not introduce a framework or speculative move/effect hierarchy for the current slice.

## Domain rules to preserve

- `Battle.takeTurn(Move)` validates that the battle is active and that the player's active creature knows the exact move. It resolves the player attack first and skips the opponent response if it faints (see `BattleTest`).
- Damage is `max(1, round(move.power() * effectiveness))`; `Creature.takeDamage` clamps health at zero. Elemental strengths are the 1.5/0.75 fire-water-grass triangle; `NORMAL` and matching types are neutral (`ElementType`).
- Keep randomness injected as `RandomGenerator`: `ConsoleGame.createDemo()` uses `new Random(7)` and `ConsoleGameTest` injects streams/randomness. Avoid hidden `Math.random()`/new RNG calls in domain behavior.
- Preserve constructor validation and immutable collection snapshots (`List.copyOf`) when adding domain data. Return immutable event data suitable for another UI.

## Content and console conventions

- Put current built-in creatures and moves in `GameContent`; call `GameContent.createBattle(RandomGenerator)` so each game receives independent mutable creature state.
- Keep console input/output in `ConsoleGame`, injected through `Scanner` and `PrintStream`. Keep Swing code under `ui.swing`; `BattlePanel` accepts a battle supplier for deterministic UI tests.
- The default launch is Swing; `--console` selects numbered terminal input and `--demo` chooses the highest-power move for a deterministic smoke run.
- Extend the domain model before choosing LibGDX/JavaFX or another renderer. `docs/PROJECT_PLAN.md` intentionally defers framework, stat formula, map format, persistence, and assets.

## Build, test, and verify

- Use the project-pinned launchers: `.\mvn.cmd test` runs JUnit 5 on Temurin 21 and `.\mvn.cmd clean package` produces `target/Poklone-1.0-SNAPSHOT.jar`. `.\run.cmd` builds and opens the GUI.
- Exercise the end-to-end noninteractive path with `.\run.cmd --demo`; it should finish with `You won the practice battle!`.
- Place focused JUnit 5 tests under the matching package in `src/test/java`. Existing tests assert observable rules/results (damage, turn count/status, output) rather than private implementation.
- `Plan.md` is historical brainstorming; treat `README.md` and `docs/PROJECT_PLAN.md` as the current project description. `package.json`/`node_modules` are untracked tooling artifacts, not part of the Java application.
