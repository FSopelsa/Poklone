package se.poklone.application;

import se.poklone.domain.Battle;
import se.poklone.domain.BattleStatus;
import se.poklone.domain.Creature;
import se.poklone.domain.Direction;
import se.poklone.domain.Position;
import se.poklone.domain.Trainer;
import se.poklone.domain.WorldMap;
import se.poklone.domain.WorldMoveResult;
import se.poklone.domain.WorldTile;

import java.util.Objects;
import java.util.Random;
import java.util.random.RandomGenerator;

public final class GameSession {

    private final Trainer player;
    private final WorldMap world;
    private final Position startPosition;
    private final RandomGenerator random;

    private Position playerPosition;
    private GamePhase phase = GamePhase.EXPLORING;
    private Battle battle;
    private boolean encounterCleared;
    private String worldMessage = "Find Scout Mira in the practice room.";

    public GameSession(
            Trainer player,
            WorldMap world,
            Position startPosition,
            RandomGenerator random
    ) {
        this.player = Objects.requireNonNull(player, "Player must not be null");
        this.world = Objects.requireNonNull(world, "World must not be null");
        this.startPosition = Objects.requireNonNull(startPosition, "Start position must not be null");
        this.random = Objects.requireNonNull(random, "Random generator must not be null");
        if (!world.isWalkable(startPosition)) {
            throw new IllegalArgumentException("Start position must be walkable");
        }
        this.playerPosition = startPosition;
    }

    public static GameSession createDefault() {
        return new GameSession(
                GameContent.createPlayer(),
                GameContent.createPracticeRoom(),
                GameContent.practiceRoomStart(),
                new Random()
        );
    }

    public Trainer player() {
        return player;
    }

    public WorldMap world() {
        return world;
    }

    public Position playerPosition() {
        return playerPosition;
    }

    public GamePhase phase() {
        return phase;
    }

    public String worldMessage() {
        return worldMessage;
    }

    public boolean encounterCleared() {
        return encounterCleared;
    }

    public Battle battle() {
        if (phase != GamePhase.BATTLING || battle == null) {
            throw new IllegalStateException("No battle is active");
        }
        return battle;
    }

    public WorldMoveResult move(Direction direction) {
        if (phase != GamePhase.EXPLORING) {
            throw new IllegalStateException("Cannot move while a battle is active");
        }

        Position target = playerPosition.move(direction);
        if (!world.isWalkable(target)) {
            worldMessage = "A wall blocks the way.";
            return new WorldMoveResult(playerPosition, false, false, worldMessage);
        }

        playerPosition = target;
        if (world.tileAt(target) == WorldTile.ENCOUNTER && !encounterCleared) {
            battle = GameContent.createBattle(player, random);
            phase = GamePhase.BATTLING;
            worldMessage = "Scout Mira challenges your party!";
            return new WorldMoveResult(playerPosition, true, true, worldMessage);
        }

        worldMessage = encounterCleared
                ? "Practice complete. Explore the room."
                : "Keep searching for Scout Mira.";
        return new WorldMoveResult(playerPosition, true, false, worldMessage);
    }

    public void returnToWorld() {
        Battle activeBattle = battle();
        if (activeBattle.status() == BattleStatus.IN_PROGRESS) {
            throw new IllegalStateException("Cannot leave an unfinished battle");
        }

        boolean playerWon = activeBattle.status() == BattleStatus.PLAYER_WON;
        if (playerWon) {
            encounterCleared = true;
            worldMessage = "Scout Mira defeated. Your party was restored.";
        } else {
            playerPosition = startPosition;
            worldMessage = "Your party recovered at the room entrance.";
        }

        player.party().forEach(Creature::restoreHealth);
        battle = null;
        phase = GamePhase.EXPLORING;
    }
}
