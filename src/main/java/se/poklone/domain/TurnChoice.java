package se.poklone.domain;

public sealed interface TurnChoice permits MoveChoice, SwitchChoice {
}
