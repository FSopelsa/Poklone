package se.poklone.domain;

import java.util.Objects;

public record Trainer(String name, Creature activeCreature) {

    public Trainer {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Trainer name must not be blank");
        }
        Objects.requireNonNull(activeCreature, "Active creature must not be null");
    }
}

