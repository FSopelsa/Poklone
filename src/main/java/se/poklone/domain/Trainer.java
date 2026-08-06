package se.poklone.domain;

import java.util.List;
import java.util.Objects;

public record Trainer(String name, List<Creature> party) {

    public Trainer {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Trainer name must not be blank");
        }
        if (party == null || party.isEmpty()) {
            throw new IllegalArgumentException("A trainer must have at least one creature");
        }
        if (party.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("Party must not contain null");
        }
        party = List.copyOf(party);
    }

    public Trainer(String name, Creature creature) {
        this(name, List.of(Objects.requireNonNull(creature, "Creature must not be null")));
    }

    public boolean isDefeated() {
        return party.stream().allMatch(Creature::isFainted);
    }
}
