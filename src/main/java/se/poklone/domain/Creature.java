package se.poklone.domain;

import java.util.List;
import java.util.Objects;

public final class Creature {

    private final String name;
    private final ElementType type;
    private final int maxHealth;
    private final Stats stats;
    private final List<Move> moves;
    private int currentHealth;

    public Creature(
            String name,
            ElementType type,
            int maxHealth,
            Stats stats,
            List<Move> moves
    ) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Creature name must not be blank");
        }
        this.type = Objects.requireNonNull(type, "Creature type must not be null");
        if (maxHealth <= 0) {
            throw new IllegalArgumentException("Maximum health must be positive");
        }
        if (moves == null || moves.isEmpty()) {
            throw new IllegalArgumentException("A creature must know at least one move");
        }
        if (moves.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("Moves must not contain null");
        }

        this.name = name;
        this.maxHealth = maxHealth;
        this.stats = Objects.requireNonNull(stats, "Creature stats must not be null");
        this.currentHealth = maxHealth;
        this.moves = List.copyOf(moves);
    }

    public String name() {
        return name;
    }

    public ElementType type() {
        return type;
    }

    public int maxHealth() {
        return maxHealth;
    }

    public int currentHealth() {
        return currentHealth;
    }

    public Stats stats() {
        return stats;
    }

    public List<Move> moves() {
        return moves;
    }

    public boolean isFainted() {
        return currentHealth == 0;
    }

    public boolean knows(Move move) {
        return moves.contains(move);
    }

    public void takeDamage(int damage) {
        if (damage < 0) {
            throw new IllegalArgumentException("Damage must not be negative");
        }
        currentHealth = Math.max(0, currentHealth - damage);
    }

    public void restoreHealth() {
        currentHealth = maxHealth;
    }
}

