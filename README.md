# Poklone

Poklone is an original creature-battling game used to learn Java and
object-oriented design. The current milestone is deliberately small: it is a
runnable terminal battle that keeps the game rules independent from any future
graphics framework.

No Pokémon characters, names, worlds, or assets are used.

## Requirements

- JDK 21 or newer
- Maven 3.9 or newer

## Run it

```powershell
mvn.cmd clean package
java -jar target/Poklone-1.0-SNAPSHOT.jar
```

Choose a numbered move each turn. Enter `q` to leave the game.

For a non-interactive smoke test:

```powershell
java -jar target/Poklone-1.0-SNAPSHOT.jar --demo
```

Run the tests with:

```powershell
mvn.cmd test
```

## Current structure

```text
src/main/java/se/poklone/
|-- Main.java                 Application entry point
|-- application/
|   |-- ConsoleGame.java      Terminal input and presentation
|   `-- GameContent.java      Small set of starter game data
`-- domain/
    |-- Battle.java           Turn orchestration and battle rules
    |-- BattleStatus.java     Battle lifecycle
    |-- Creature.java         Mutable creature health and known moves
    |-- ElementType.java      Element effectiveness rules
    |-- Move.java             Immutable move definition
    |-- Trainer.java          Trainer and active creature
    `-- TurnResult.java       Events produced by one turn
```

The domain package does not know about the terminal. That boundary lets a later
LibGDX screen, JavaFX UI, or other presentation layer use the same battle rules.

