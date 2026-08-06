package se.poklone.domain;

public record SwitchResult(
        String trainerName,
        String previousCreatureName,
        String newCreatureName,
        boolean forced
) implements BattleEvent {

    public SwitchResult {
        requireText(trainerName, "Trainer name");
        requireText(previousCreatureName, "Previous creature name");
        requireText(newCreatureName, "New creature name");
    }

    private static void requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
    }
}
