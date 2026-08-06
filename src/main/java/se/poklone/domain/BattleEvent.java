package se.poklone.domain;

public sealed interface BattleEvent permits AttackResult, SwitchResult {
}
