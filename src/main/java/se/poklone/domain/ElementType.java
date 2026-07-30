package se.poklone.domain;

import java.util.Objects;

public enum ElementType {
    NORMAL,
    FIRE,
    WATER,
    GRASS;

    public double effectivenessAgainst(ElementType defender) {
        Objects.requireNonNull(defender, "Defender type must not be null");

        if (this == NORMAL || defender == NORMAL || this == defender) {
            return 1.0;
        }

        return switch (this) {
            case FIRE -> defender == GRASS ? 1.5 : 0.75;
            case WATER -> defender == FIRE ? 1.5 : 0.75;
            case GRASS -> defender == WATER ? 1.5 : 0.75;
            case NORMAL -> 1.0;
        };
    }
}

