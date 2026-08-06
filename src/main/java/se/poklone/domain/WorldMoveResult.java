package se.poklone.domain;

import java.util.Objects;

public record WorldMoveResult(
        Position position,
        boolean moved,
        boolean encounterStarted,
        String message
) {

    public WorldMoveResult {
        Objects.requireNonNull(position, "Position must not be null");
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("World message must not be blank");
        }
    }
}
