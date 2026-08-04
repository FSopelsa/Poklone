package se.poklone.domain;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BattleTest {

    @Test
    void faintedOpponentDoesNotCounterattack() {
        Move leafBurst = new Move("Leaf Burst", ElementType.GRASS, 20);
        Creature playerCreature = creature(
                "Sprig", ElementType.GRASS, 30, new Stats(10, 10, 10), leafBurst
        );
        Creature opponentCreature = creature(
                "Drop",
                ElementType.WATER,
                25,
                new Stats(10, 10, 5),
                new Move("Splash Hit", ElementType.WATER, 8)
        );
        Battle battle = battle(playerCreature, opponentCreature);

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
        Creature playerCreature = creature(
                "Sprig", ElementType.GRASS, 30, new Stats(10, 10, 10), playerMove
        );
        Creature opponentCreature = creature(
                "Drop", ElementType.WATER, 25, new Stats(10, 10, 5), opponentMove
        );
        Battle battle = battle(playerCreature, opponentCreature);

        TurnResult turn = battle.takeTurn(playerMove);

        assertEquals(2, turn.attacks().size());
        assertEquals("Bump", turn.attacks().get(1).moveName());
        assertEquals(26, playerCreature.currentHealth());
        assertEquals(BattleStatus.IN_PROGRESS, turn.status());
    }

    @Test
    void fasterOpponentAttacksFirst() {
        Move playerMove = new Move("Tap", ElementType.NORMAL, 5);
        Creature playerCreature = creature(
                "Sprig", ElementType.GRASS, 30, new Stats(10, 10, 5), playerMove
        );
        Creature opponentCreature = creature(
                "Drop",
                ElementType.WATER,
                30,
                new Stats(10, 10, 10),
                new Move("Bump", ElementType.NORMAL, 4)
        );
        Battle battle = battle(playerCreature, opponentCreature);

        TurnResult turn = battle.takeTurn(playerMove);

        assertEquals("Drop", turn.attacks().getFirst().attackerName());
        assertEquals("Sprig", turn.attacks().get(1).attackerName());
    }

    @Test
    void speedTieFavorsPlayer() {
        Move playerMove = new Move("Tap", ElementType.NORMAL, 5);
        Stats tiedStats = new Stats(10, 10, 8);
        Creature playerCreature = creature(
                "Sprig", ElementType.GRASS, 30, tiedStats, playerMove
        );
        Creature opponentCreature = creature(
                "Drop",
                ElementType.WATER,
                30,
                tiedStats,
                new Move("Bump", ElementType.NORMAL, 4)
        );
        Battle battle = battle(playerCreature, opponentCreature);

        TurnResult turn = battle.takeTurn(playerMove);

        assertEquals("Sprig", turn.attacks().getFirst().attackerName());
    }

    @Test
    void fasterOpponentCanFinishTurnBeforePlayerActs() {
        Move playerMove = new Move("Tap", ElementType.NORMAL, 20);
        Creature playerCreature = creature(
                "Sprig", ElementType.GRASS, 4, new Stats(10, 10, 5), playerMove
        );
        Creature opponentCreature = creature(
                "Drop",
                ElementType.WATER,
                30,
                new Stats(10, 10, 10),
                new Move("Bump", ElementType.NORMAL, 4)
        );
        Battle battle = battle(playerCreature, opponentCreature);

        TurnResult turn = battle.takeTurn(playerMove);

        assertEquals(1, turn.attacks().size());
        assertEquals(BattleStatus.OPPONENT_WON, turn.status());
        assertEquals(30, opponentCreature.currentHealth());
    }

    @Test
    void attackAndDefenceScaleDamage() {
        Move playerMove = new Move("Tap", ElementType.NORMAL, 5);
        Creature playerCreature = creature(
                "Sprig", ElementType.GRASS, 30, new Stats(20, 10, 10), playerMove
        );
        Creature opponentCreature = creature(
                "Drop",
                ElementType.WATER,
                30,
                new Stats(10, 10, 5),
                new Move("Bump", ElementType.NORMAL, 1)
        );
        Battle battle = battle(playerCreature, opponentCreature);

        TurnResult turn = battle.takeTurn(playerMove);

        assertEquals(10, turn.attacks().getFirst().damage());
        assertEquals(20, opponentCreature.currentHealth());
    }

    @Test
    void damageIsAlwaysAtLeastOne() {
        Move playerMove = new Move("Tiny Tap", ElementType.NORMAL, 1);
        Creature playerCreature = creature(
                "Sprig", ElementType.GRASS, 30, new Stats(1, 10, 10), playerMove
        );
        Creature opponentCreature = creature(
                "Drop",
                ElementType.WATER,
                30,
                new Stats(10, 100, 5),
                new Move("Bump", ElementType.NORMAL, 1)
        );
        Battle battle = battle(playerCreature, opponentCreature);

        TurnResult turn = battle.takeTurn(playerMove);

        assertEquals(1, turn.attacks().getFirst().damage());
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

    @Test
    void switchingUsesTheTurnBeforeOpponentAttacks() {
        Move firstMove = new Move("Tap", ElementType.NORMAL, 5);
        Creature first = creature(
                "Sprig", ElementType.GRASS, 30, new Stats(10, 10, 10), firstMove
        );
        Creature reserve = creature(
                "Cinder", ElementType.FIRE, 24, new Stats(10, 10, 10), firstMove
        );
        Creature opponent = creature(
                "Drop",
                ElementType.WATER,
                30,
                new Stats(10, 10, 5),
                new Move("Bump", ElementType.NORMAL, 4)
        );
        Battle battle = new Battle(
                new Trainer("Player", List.of(first, reserve)),
                new Trainer("Opponent", opponent),
                new Random(1)
        );

        TurnResult turn = battle.takeTurn(new SwitchChoice(1));

        SwitchResult switched = (SwitchResult) turn.events().getFirst();
        assertEquals("Cinder", switched.newCreatureName());
        assertFalse(switched.forced());
        assertEquals(1, battle.playerActiveIndex());
        assertEquals(20, reserve.currentHealth());
        assertEquals(1, turn.attacks().size());
    }

    @Test
    void faintedPlayerMustChooseAHealthyReplacement() {
        Move tap = new Move("Tap", ElementType.NORMAL, 1);
        Creature first = creature(
                "Sprig", ElementType.GRASS, 3, new Stats(10, 10, 5), tap
        );
        Creature reserve = creature(
                "Cinder", ElementType.FIRE, 20, new Stats(10, 10, 10), tap
        );
        Creature opponent = creature(
                "Drop",
                ElementType.WATER,
                30,
                new Stats(10, 10, 20),
                new Move("Bump", ElementType.NORMAL, 4)
        );
        Battle battle = new Battle(
                new Trainer("Player", List.of(first, reserve)),
                new Trainer("Opponent", opponent),
                new Random(1)
        );

        TurnResult turn = battle.takeTurn(tap);

        assertEquals(BattleStatus.IN_PROGRESS, turn.status());
        assertTrue(battle.playerNeedsReplacement());
        assertThrows(IllegalStateException.class, () -> battle.takeTurn(tap));

        SwitchResult replacement = battle.replaceFaintedPlayer(1);
        assertTrue(replacement.forced());
        assertEquals("Cinder", battle.playerActiveCreature().name());
        assertFalse(battle.playerNeedsReplacement());
    }

    @Test
    void opponentAutomaticallySendsNextHealthyCreature() {
        Move finish = new Move("Finish", ElementType.NORMAL, 10);
        Creature player = creature(
                "Sprig", ElementType.GRASS, 30, new Stats(10, 10, 20), finish
        );
        Creature firstOpponent = creature(
                "Drop",
                ElementType.WATER,
                10,
                new Stats(10, 10, 5),
                new Move("Bump", ElementType.NORMAL, 4)
        );
        Creature reserveOpponent = creature(
                "Stone",
                ElementType.NORMAL,
                20,
                new Stats(10, 10, 5),
                new Move("Peck", ElementType.NORMAL, 4)
        );
        Battle battle = new Battle(
                new Trainer("Player", player),
                new Trainer("Opponent", List.of(firstOpponent, reserveOpponent)),
                new Random(1)
        );

        TurnResult turn = battle.takeTurn(finish);

        assertEquals(BattleStatus.IN_PROGRESS, turn.status());
        assertEquals(1, turn.attacks().size());
        assertEquals(1, turn.switches().size());
        assertTrue(turn.switches().getFirst().forced());
        assertEquals("Stone", battle.opponentActiveCreature().name());
    }

    private static Battle battleWith(Move playerMove, int playerHealth, int opponentHealth) {
        Creature playerCreature = creature(
                "Sprig",
                ElementType.GRASS,
                playerHealth,
                new Stats(10, 10, 10),
                playerMove
        );
        Creature opponentCreature = creature(
                "Drop",
                ElementType.WATER,
                opponentHealth,
                new Stats(10, 10, 5),
                new Move("Bump", ElementType.NORMAL, 4)
        );
        return battle(playerCreature, opponentCreature);
    }

    private static Creature creature(
            String name,
            ElementType type,
            int health,
            Stats stats,
            Move... moves
    ) {
        return new Creature(name, type, health, stats, List.of(moves));
    }

    private static Battle battle(Creature playerCreature, Creature opponentCreature) {
        return new Battle(
                new Trainer("Player", playerCreature),
                new Trainer("Opponent", opponentCreature),
                new Random(1)
        );
    }
}
