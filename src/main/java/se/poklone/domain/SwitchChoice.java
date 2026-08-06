package se.poklone.domain;

public record SwitchChoice(int partyIndex) implements TurnChoice {

    public SwitchChoice {
        if (partyIndex < 0) {
            throw new IllegalArgumentException("Party index must not be negative");
        }
    }
}
