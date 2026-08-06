package se.poklone.domain;

import java.util.Objects;

public record Position(int x, int y) {

    public Position move(Direction direction) {
        Objects.requireNonNull(direction, "Direction must not be null");
        return new Position(x + direction.deltaX(), y + direction.deltaY());
    }
}
