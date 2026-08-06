# Poklone next steps

## Completed foundation

### 1. Stats and turn order

`Stats` models attack, defence, and speed. `Battle` resolves faster creatures
first, gives ties to the player, stops a fainted creature from acting, and uses
`max(1, round(power * attack / defence * effectiveness))` damage.

### 2. Parties and switching

`Trainer` owns an immutable party snapshot. `Battle` owns active indexes and
accepts `MoveChoice` or `SwitchChoice`. Switching consumes a turn; a player
with a fainted active creature must select a healthy reserve, while the
opponent automatically selects its first healthy reserve.

### 3. Session and one-room world

`GameSession` owns the player party, map position, active phase, battle, and
encounter completion. `WorldMap` handles walkability. Swing's `GamePanel`
switches between `WorldPanel` and `BattlePanel`; victory clears the encounter
and defeat returns the player to the entrance.

### Audio placeholder slice

Swing now plays CC0 room music and short cues for movement, walls, encounters,
attacks, switching, victory, and defeat. Playback uses an injected `AudioPlayer`,
stays outside `domain`, and fails softly when a machine has no audio line.

## Recommended next work

### 4. Stable content identity and progression

- Separate immutable species/move definitions from mutable owned-creature state.
- Add stable IDs before saves or external files reference content.
- Add level, experience, stat growth, and explicit progression events.
- Award a small deterministic reward after Scout Mira as the next playable slice.

### 5. External content and saves

- Move definitions and encounters from `GameContent` into validated data.
- Reject duplicate IDs, missing references, and invalid values at startup.
- Save plain session state and stable IDs, never Swing components or live `Battle` objects.

### 6. Expand exploration

- Add map exits, interaction targets, and multiple encounter definitions.
- Select a renderer only when animation, camera, or asset needs exceed Swing.
- Preserve `GameSession` and domain APIs so presentation changes stay isolated.

## Fun slices after content identity

- Render elemental attack flashes and small screen shake from `BattleEvent` data.
- Add one talkable NPC and one treasure tile before adding another map.
- Give Scout Mira a rematch with a changed party after the first victory.
- Crossfade separate exploration and battle music once the audio placeholders settle.
