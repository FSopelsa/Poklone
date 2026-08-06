# Poklone agent guide

## Scope and architecture

- Poklone is a Java 21/Maven learning project and original creature battler. Read `README.md` and `docs/PROJECT_PLAN.md` before roadmap-scale changes; `Plan.md` is historical brainstorming.
- Preserve the boundary: `domain/` owns battle and map rules with no Swing, console, file, or framework dependencies. `application/` assembles content and owns cross-screen state; `ui.swing/` renders and forwards choices.
- Current Swing flow is `Main -> SwingGame -> GamePanel -> WorldPanel/BattlePanel -> GameSession -> WorldMap/Battle`. Console flow is `Main -> ConsoleGame -> GameContent -> Battle`.
- `GameSession` is the source of truth for player party, world position, phase, current battle, and encounter completion. Do not duplicate these in panels.
- Keep the current small explicit model: immutable records and `List.copyOf` snapshots, mutable health only in `Creature`, active indexes only in `Battle`, screen transitions only in `GameSession`/`GamePanel`.

## Rules and patterns

- `Battle.takeTurn(Move)` delegates to `MoveChoice`; general callers can send `MoveChoice` or `SwitchChoice`. Results are ordered `BattleEvent` values (`AttackResult`, `SwitchResult`) rendered by both adapters.
- A voluntary switch consumes the turn before the opponent attacks. A fainted player active requires `replaceFaintedPlayer`; a fainted opponent auto-selects its first healthy reserve. Battle ends only when a whole party faints (`BattleTest`).
- Damage is `max(1, round(power * attack / defence * effectiveness))`. Faster creatures act first; the player wins speed ties. Fire/water/grass use 1.5/0.75 effectiveness; normal and matching types are neutral.
- Keep randomness injected as `RandomGenerator`. `GameSession` passes its generator into fresh encounters; demo/tests use seeded `Random` instances. Never hide `Math.random()` or new RNG calls in domain behavior.
- `WorldMap` parses rectangular `#` wall, `.` floor, and `E` encounter rows. Movement/collision stays in `GameSession`; drawing and key bindings stay in `WorldPanel`.
- Put built-in mutable object construction in `GameContent`. `createBattle(Trainer, RandomGenerator)` must reuse the session's player but create fresh opponent state.
- Console I/O remains injected through `Scanner`/`PrintStream`. Swing panels expose package-private injectable constructors and named controls such as `move-0`, `party-1`, and `move-right` for component tests.
- Keep audio in `ui.swing`: panels emit `SoundEffect`/`MusicTrack` cues through injected `AudioPlayer`; tests use silent/recording players. Store distributable files under `src/main/resources/audio/` and record source URL, creator, original filename, and license in `audio/LICENSES.md`.

## Build and verification

- Use `.\mvn.cmd test` for JUnit Jupiter on the project-pinned Temurin 21. Use `.\mvn.cmd clean package` for `target/Poklone-1.0-SNAPSHOT.jar` and `.\run.cmd` for the GUI.
- Run `.\run.cmd --demo` for the noninteractive end-to-end path; it must finish with `You won the practice battle!`.
- Tests mirror packages under `src/test/java` and assert public outcomes/events, session transitions, output, and named Swing components—not private implementation.
- `run.cmd` launches `target/classes` to avoid locking the JAR. The project-local `.m2/`, generated `target/`, `qodana.sarif.json`, `package.json`, and `node_modules/` are tooling artifacts, not application sources.
