package se.poklone.domain;

import java.util.List;
import java.util.Objects;

public record TurnResult(List<BattleEvent> events, BattleStatus status) {

    public TurnResult {
        if (events == null || events.isEmpty()) {
            throw new IllegalArgumentException("A turn must contain at least one event");
        }
        if (events.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("Turn events must not contain null");
        }
        events = List.copyOf(events);
        Objects.requireNonNull(status, "Battle status must not be null");
    }

    public List<AttackResult> attacks() {
        return events.stream()
                .filter(AttackResult.class::isInstance)
                .map(AttackResult.class::cast)
                .toList();
    }

    public List<SwitchResult> switches() {
        return events.stream()
                .filter(SwitchResult.class::isInstance)
                .map(SwitchResult.class::cast)
                .toList();
    }
}
