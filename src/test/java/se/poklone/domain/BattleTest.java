package se.poklone.domain;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}

