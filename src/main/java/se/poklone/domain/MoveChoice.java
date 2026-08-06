package se.poklone.domain;

import java.util.Objects;

public record MoveChoice(Move move) implements TurnChoice {

    public MoveChoice {
        Objects.requireNonNull(move, "Move must not be null");
    }
}
