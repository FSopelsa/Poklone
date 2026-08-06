package se.poklone.domain;

import java.util.List;
import java.util.Objects;

public final class WorldMap {

    private final List<String> rows;
    private final int width;

    public WorldMap(List<String> rows) {
        if (rows == null || rows.isEmpty()) {
            throw new IllegalArgumentException("A world map must contain at least one row");
        }
        if (rows.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("World rows must not contain null");
        }

        width = rows.getFirst().length();
        if (width == 0) {
            throw new IllegalArgumentException("World rows must not be empty");
        }
        if (rows.stream().anyMatch(row -> row.length() != width)) {
            throw new IllegalArgumentException("World rows must have equal width");
        }
        for (String row : rows) {
            for (int column = 0; column < row.length(); column++) {
                tileFor(row.charAt(column));
            }
        }

        this.rows = List.copyOf(rows);
    }

    public int width() {
        return width;
    }

    public int height() {
        return rows.size();
    }

    public boolean contains(Position position) {
        Objects.requireNonNull(position, "Position must not be null");
        return position.x() >= 0
                && position.x() < width
                && position.y() >= 0
                && position.y() < height();
    }

    public boolean isWalkable(Position position) {
        return contains(position) && tileAt(position) != WorldTile.WALL;
    }

    public WorldTile tileAt(Position position) {
        if (!contains(position)) {
            throw new IllegalArgumentException("Position is outside the world map");
        }
        return tileFor(rows.get(position.y()).charAt(position.x()));
    }

    private static WorldTile tileFor(char symbol) {
        return switch (symbol) {
            case '.' -> WorldTile.FLOOR;
            case '#' -> WorldTile.WALL;
            case 'E' -> WorldTile.ENCOUNTER;
            default -> throw new IllegalArgumentException("Unknown world tile: " + symbol);
        };
    }
}
