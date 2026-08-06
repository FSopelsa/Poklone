package se.poklone.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StatsTest {

    @Test
    void exposesCombatValues() {
        Stats stats = new Stats(12, 9, 15);

        assertEquals(12, stats.attack());
        assertEquals(9, stats.defence());
        assertEquals(15, stats.speed());
    }

    @Test
    void valuesMustBePositive() {
        assertAll(
                () -> assertThrows(
                        IllegalArgumentException.class,
                        () -> new Stats(0, 10, 10)
                ),
                () -> assertThrows(
                        IllegalArgumentException.class,
                        () -> new Stats(10, 0, 10)
                ),
                () -> assertThrows(
                        IllegalArgumentException.class,
                        () -> new Stats(10, 10, 0)
                )
        );
    }
}
