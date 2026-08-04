package se.poklone.domain;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CreatureTest {

    @Test
    void damageCannotReduceHealthBelowZero() {
        Creature creature = new Creature(
                "Testling",
                ElementType.NORMAL,
                20,
                new Stats(10, 10, 10),
                List.of(new Move("Tap", ElementType.NORMAL, 5))
        );

        creature.takeDamage(100);

        assertEquals(0, creature.currentHealth());
        assertTrue(creature.isFainted());
    }

    @Test
    void movesAreAnImmutableSnapshot() {
        List<Move> sourceMoves = new ArrayList<>();
        sourceMoves.add(new Move("Tap", ElementType.NORMAL, 5));
        Creature creature = new Creature(
                "Testling",
                ElementType.NORMAL,
                20,
                new Stats(10, 10, 10),
                sourceMoves
        );

        sourceMoves.clear();

        assertEquals(1, creature.moves().size());
        assertThrows(
                UnsupportedOperationException.class,
                () -> creature.moves().add(new Move("Other", ElementType.FIRE, 10))
        );
    }
}

