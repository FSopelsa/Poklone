package se.poklone.domain;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BattleTest {

    @Test
    void faintedOpponentDoesNotCounterattack() {
        Move leafBurst = new Move("Leaf Burst", ElementType.GRASS, 20);
        Creature playerCreature = new Creature(
                "Sprig",
                ElementType.GRASS,
                30,
                List.of(leafBurst)
        );
        Creature opponentCreature = new Creature(
                "Drop",
                ElementType.WATER,
                25,
                List.of(new Move("Splash Hit", ElementType.WATER, 8))
        );
        Battle battle = new Battle(
                new Trainer("Player", playerCreature),
                new Trainer("Opponent", opponentCreature),
                new Random(1)
        );

        TurnResult turn = battle.takeTurn(leafBurst);

        assertEquals(1, turn.attacks().size());
        assertEquals(30, turn.attacks().getFirst().damage());
        assertEquals(BattleStatus.PLAYER_WON, turn.status());
        assertEquals(30, playerCreature.currentHealth());
    }

    @Test
    void survivingOpponentCounterattacks() {
        Move playerMove = new Move("Tap", ElementType.NORMAL, 5);
        Move opponentMove = new Move("Bump", ElementType.NORMAL, 4);
        Creature playerCreature = new Creature(
                "Sprig",
                ElementType.GRASS,
                30,
                List.of(playerMove)
        );
        Creature opponentCreature = new Creature(
                "Drop",
                ElementType.WATER,
                25,
                List.of(opponentMove)
        );
        Battle battle = new Battle(
                new Trainer("Player", playerCreature),
                new Trainer("Opponent", opponentCreature),
                new Random(1)
        );

        TurnResult turn = battle.takeTurn(playerMove);

        assertEquals(2, turn.attacks().size());
        assertEquals("Bump", turn.attacks().get(1).moveName());
        assertEquals(26, playerCreature.currentHealth());
        assertEquals(BattleStatus.IN_PROGRESS, turn.status());
    }

    @Test
    void moveMustBelongToPlayersCreature() {
        Move knownMove = new Move("Tap", ElementType.NORMAL, 5);
        Battle battle = battleWith(knownMove, 30, 30);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> battle.takeTurn(new Move("Unknown", ElementType.FIRE, 99))
        );

        assertEquals("The player's creature does not know that move", exception.getMessage());
    }

    @Test
    void finishedBattleRejectsAnotherTurn() {
        Move finishingMove = new Move("Finish", ElementType.NORMAL, 30);
        Battle battle = battleWith(finishingMove, 30, 10);
        battle.takeTurn(finishingMove);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> battle.takeTurn(finishingMove)
        );

        assertEquals("The battle is already finished", exception.getMessage());
    }

    private static Battle battleWith(Move playerMove, int playerHealth, int opponentHealth) {
        Creature playerCreature = new Creature(
                "Sprig",
                ElementType.GRASS,
                playerHealth,
                List.of(playerMove)
        );
        Creature opponentCreature = new Creature(
                "Drop",
                ElementType.WATER,
                opponentHealth,
                List.of(new Move("Bump", ElementType.NORMAL, 4))
        );
        return new Battle(
                new Trainer("Player", playerCreature),
                new Trainer("Opponent", opponentCreature),
                new Random(1)
        );
    }
}

