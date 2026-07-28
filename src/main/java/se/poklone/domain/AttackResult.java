package se.poklone.domain;

public record AttackResult(
        String attackerName,
        String defenderName,
        String moveName,
        int damage,
        double effectiveness,
        int defenderHealth,
        boolean defenderFainted
) {
}

