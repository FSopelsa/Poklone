package se.poklone.application;

import org.junit.jupiter.api.Test;
import se.poklone.domain.Battle;
import se.poklone.domain.BattleStatus;
import se.poklone.domain.Creature;
import se.poklone.domain.Direction;
import se.poklone.domain.ElementType;
import se.poklone.domain.Move;
import se.poklone.domain.Position;
import se.poklone.domain.Stats;
import se.poklone.domain.Trainer;
import se.poklone.domain.WorldMap;
import se.poklone.domain.WorldMoveResult;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameSessionTest {

    @Test
    void wallCollisionLeavesPlayerInPlace() {
        GameSession session = sessionWith(new Move("Tap", ElementType.NORMAL, 1));

        WorldMoveResult move = session.move(Direction.UP);

        assertFalse(move.moved());
        assertEquals(new Position(1, 1), session.playerPosition());
        assertEquals("A wall blocks the way.", session.worldMessage());
    }

    @Test
    void encounterStartsBattleWithSessionsPlayerParty() {
        GameSession session = sessionWith(new Move("Tap", ElementType.NORMAL, 1));

        WorldMoveResult move = session.move(Direction.RIGHT);

        assertTrue(move.encounterStarted());
        assertEquals(GamePhase.BATTLING, session.phase());
        assertSame(session.player(), session.battle().player());
        assertThrows(IllegalStateException.class, () -> session.move(Direction.LEFT));
    }

    @Test
    void victoryReturnsToWorldClearsEncounterAndRestoresParty() {
        Move finish = new Move("Finish", ElementType.NORMAL, 1_000);
        GameSession session = sessionWith(finish);
        session.move(Direction.RIGHT);
        Battle battle = session.battle();

        battle.takeTurn(finish);
        battle.takeTurn(finish);
        assertEquals(BattleStatus.PLAYER_WON, battle.status());

        session.player().party().getFirst().takeDamage(5);
        session.returnToWorld();

        assertEquals(GamePhase.EXPLORING, session.phase());
        assertTrue(session.encounterCleared());
        assertEquals(30, session.player().party().getFirst().currentHealth());
        assertEquals(new Position(2, 1), session.playerPosition());
        assertFalse(session.move(Direction.LEFT).encounterStarted());
    }

    @Test
    void cannotLeaveUnfinishedBattle() {
        GameSession session = sessionWith(new Move("Tap", ElementType.NORMAL, 1));
        session.move(Direction.RIGHT);

        assertThrows(IllegalStateException.class, session::returnToWorld);
    }

    @Test
    void defeatRestoresPartyAndReturnsPlayerToEntrance() {
        Move tap = new Move("Tap", ElementType.NORMAL, 1);
        Creature fragile = new Creature(
                "Sprig",
                ElementType.GRASS,
                1,
                new Stats(1, 1, 1),
                List.of(tap)
        );
        GameSession session = new GameSession(
                new Trainer("Player", fragile),
                new WorldMap(List.of("#####", "#.E.#", "#####")),
                new Position(1, 1),
                new Random(1)
        );
        session.move(Direction.RIGHT);

        session.battle().takeTurn(tap);
        assertEquals(BattleStatus.OPPONENT_WON, session.battle().status());

        session.returnToWorld();

        assertEquals(GamePhase.EXPLORING, session.phase());
        assertEquals(new Position(1, 1), session.playerPosition());
        assertEquals(1, fragile.currentHealth());
        assertFalse(session.encounterCleared());
    }

    private static GameSession sessionWith(Move move) {
        Creature creature = new Creature(
                "Sprig",
                ElementType.GRASS,
                30,
                new Stats(30, 30, 30),
                List.of(move)
        );
        return new GameSession(
                new Trainer("Player", creature),
                new WorldMap(List.of("#####", "#.E.#", "#####")),
                new Position(1, 1),
                new Random(1)
        );
    }
}
