package se.poklone.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.random.RandomGenerator;

public final class Battle {

    private final Trainer player;
    private final Trainer opponent;
    private final RandomGenerator random;

    public Battle(Trainer player, Trainer opponent, RandomGenerator random) {
        this.player = Objects.requireNonNull(player, "Player must not be null");
        this.opponent = Objects.requireNonNull(opponent, "Opponent must not be null");
        this.random = Objects.requireNonNull(random, "Random generator must not be null");
    }

    public Trainer player() {
        return player;
    }

    public Trainer opponent() {
        return opponent;
    }

    public BattleStatus status() {
        if (opponent.activeCreature().isFainted()) {
            return BattleStatus.PLAYER_WON;
        }
        if (player.activeCreature().isFainted()) {
            return BattleStatus.OPPONENT_WON;
        }
        return BattleStatus.IN_PROGRESS;
    }

    public TurnResult takeTurn(Move playerMove) {
        if (status() != BattleStatus.IN_PROGRESS) {
            throw new IllegalStateException("The battle is already finished");
        }
        if (!player.activeCreature().knows(playerMove)) {
            throw new IllegalArgumentException("The player's creature does not know that move");
        }

        List<AttackResult> attacks = new ArrayList<>();
        attacks.add(resolveAttack(
                player.activeCreature(),
                playerMove,
                opponent.activeCreature()
        ));

        if (status() == BattleStatus.IN_PROGRESS) {
            Creature opponentCreature = opponent.activeCreature();
            Move opponentMove = opponentCreature.moves().get(
                    random.nextInt(opponentCreature.moves().size())
            );
            attacks.add(resolveAttack(
                    opponentCreature,
                    opponentMove,
                    player.activeCreature()
            ));
        }

        return new TurnResult(attacks, status());
    }

    private AttackResult resolveAttack(Creature attacker, Move move, Creature defender) {
        double effectiveness = move.type().effectivenessAgainst(defender.type());
        int damage = Math.max(1, (int) Math.round(move.power() * effectiveness));

        defender.takeDamage(damage);

        return new AttackResult(
                attacker.name(),
                defender.name(),
                move.name(),
                damage,
                effectiveness,
                defender.currentHealth(),
                defender.isFainted()
        );
    }
}

