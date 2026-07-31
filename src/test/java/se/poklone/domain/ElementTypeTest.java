package se.poklone.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ElementTypeTest {

    @Test
    void grassIsStrongAgainstWater() {
        assertEquals(1.5, ElementType.GRASS.effectivenessAgainst(ElementType.WATER));
    }

    @Test
    void grassIsWeakAgainstFire() {
        assertEquals(0.75, ElementType.GRASS.effectivenessAgainst(ElementType.FIRE));
    }

    @Test
    void normalAndMatchingTypesAreNeutral() {
        assertEquals(1.0, ElementType.NORMAL.effectivenessAgainst(ElementType.FIRE));
        assertEquals(1.0, ElementType.WATER.effectivenessAgainst(ElementType.WATER));
    }

    @Test
    void defenderTypeIsRequired() {
        assertThrows(
                NullPointerException.class,
                () -> ElementType.FIRE.effectivenessAgainst(null)
        );
    }
}

