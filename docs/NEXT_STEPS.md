# Poklone next steps

## Baseline

The repository now has a reproducible Java 21 build, a tested 1v1 battle
domain, console/demo adapters, and a Swing battle screen. The next work should
increase domain depth without making the UI responsible for rules.

## 1. Add stats and turn order

- Introduce a small immutable stats value (attack, defence, speed) and keep
  current health as battle state.
- Replace the fixed player-first sequence in `Battle.takeTurn` with
  speed-based ordering and an explicit tie rule.
- Extend `AttackResult` only with data a renderer needs; do not print or animate
  inside `Battle`.
- Keep one deterministic scenario in `--demo` and add rule tests for both
  attack orders, ties, fainting, and minimum damage.

This is the next implementation slice because parties, switching, and status
effects all need a trustworthy action-order model.

## 2. Add parties and switching

- Let a `Trainer` own an immutable creature roster and track an active slot in
  battle state.
- Model a turn choice as either a move or a switch so console and Swing can use
  the same application-facing operation.
- Define forced replacement after fainting before adding voluntary switching.
- Update the GUI with a compact party selector only after the domain contract
  and tests are stable.

## 3. Add progression

- Separate reusable creature/content definitions from per-save mutable state.
- Add level, experience, stat growth, and move-learning rules in small steps.
- Keep progression results as explicit events so a future screen can present
  level-ups without domain-side dialogs.

## 4. Externalize content and saves

- Move built-in definitions out of `GameContent` into a validated data format.
- Fail startup with useful validation errors for duplicate IDs, missing
  references, or invalid values.
- Save only plain game state plus stable content IDs; never serialize Swing or
  live `Battle` objects.

## 5. Choose the exploration renderer

Choose LibGDX, JavaFX, or another renderer only when the next playable slice
needs a map, movement, collision, and battle transitions. Keep the existing
Swing battle screen as a fast domain/debug client even if a game framework is
added later.
