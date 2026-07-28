package se.poklone.domain;

import java.util.List;
import java.util.Objects;

public record TurnResult(List<AttackResult> attacks, BattleStatus status) {

    public TurnResult {
        if (attacks == null || attacks.isEmpty()) {
            throw new IllegalArgumentException("A turn must contain at least one attack");
        }
        attacks = List.copyOf(attacks);
        Objects.requireNonNull(status, "Battle status must not be null");
    }
}

