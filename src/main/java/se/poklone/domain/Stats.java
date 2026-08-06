package se.poklone.domain;

public record Stats(int attack, int defence, int speed) {

    public Stats {
        if (attack <= 0) {
            throw new IllegalArgumentException("Attack must be positive");
        }
        if (defence <= 0) {
            throw new IllegalArgumentException("Defence must be positive");
        }
        if (speed <= 0) {
            throw new IllegalArgumentException("Speed must be positive");
        }
    }
}
