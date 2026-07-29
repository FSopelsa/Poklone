package se.poklone.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CreatureTest {

    @Test
    void damageCannotReduceHealthBelowZero() {
        Creature creature = new Creature(
                "Testling",
                ElementType.NORMAL,
                20,
                List.of(new Move("Tap", ElementType.NORMAL, 5))
        );

        creature.takeDamage(100);

        assertEquals(0, creature.currentHealth());
        assertTrue(creature.isFainted());
    }
}

