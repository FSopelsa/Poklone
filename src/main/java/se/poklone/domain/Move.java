package se.poklone.domain;

import java.util.Objects;

public record Move(String name, ElementType type, int power) {

    public Move {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Move name must not be blank");
        }
        Objects.requireNonNull(type, "Move type must not be null");
        if (power <= 0) {
            throw new IllegalArgumentException("Move power must be positive");
        }
    }
}

