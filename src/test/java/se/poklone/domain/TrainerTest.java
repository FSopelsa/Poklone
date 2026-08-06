package se.poklone.domain;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TrainerTest {

    @Test
    void partyIsAnImmutableSnapshot() {
        Creature creature = creature("Sprig");
        List<Creature> source = new ArrayList<>(List.of(creature));

        Trainer trainer = new Trainer("Player", source);
        source.add(creature("Cinder"));

        assertEquals(1, trainer.party().size());
        assertThrows(UnsupportedOperationException.class, () -> trainer.party().clear());
    }

    private static Creature creature(String name) {
        return new Creature(
                name,
                ElementType.NORMAL,
                20,
                new Stats(10, 10, 10),
                List.of(new Move("Tap", ElementType.NORMAL, 1))
        );
    }
}
