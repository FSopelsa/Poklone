package se.poklone.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.random.RandomGenerator;

public final class Battle {

    private final Trainer player;
    private final Trainer opponent;
    private final RandomGenerator random;
    private int playerActiveIndex;
    private int opponentActiveIndex;

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

    public int playerActiveIndex() {
        return playerActiveIndex;
    }

    public int opponentActiveIndex() {
        return opponentActiveIndex;
    }

    public Creature playerActiveCreature() {
        return player.party().get(playerActiveIndex);
    }

    public Creature opponentActiveCreature() {
        return opponent.party().get(opponentActiveIndex);
    }

    public BattleStatus status() {
        if (opponent.isDefeated()) {
            return BattleStatus.PLAYER_WON;
        }
        if (player.isDefeated()) {
            return BattleStatus.OPPONENT_WON;
        }
        return BattleStatus.IN_PROGRESS;
    }

    public boolean playerNeedsReplacement() {
        return status() == BattleStatus.IN_PROGRESS && playerActiveCreature().isFainted();
    }

    public boolean canPlayerSwitchTo(int partyIndex) {
        return partyIndex >= 0
                && partyIndex < player.party().size()
                && partyIndex != playerActiveIndex
                && !player.party().get(partyIndex).isFainted();
    }

    public TurnResult takeTurn(Move playerMove) {
        return takeTurn(new MoveChoice(playerMove));
    }

    public TurnResult takeTurn(TurnChoice playerChoice) {
        ensureTurnCanStart();
        Objects.requireNonNull(playerChoice, "Turn choice must not be null");

        return switch (playerChoice) {
            case MoveChoice moveChoice -> takeMoveTurn(moveChoice.move());
            case SwitchChoice switchChoice -> takeSwitchTurn(switchChoice.partyIndex());
        };
    }

    public SwitchResult replaceFaintedPlayer(int partyIndex) {
        if (!playerNeedsReplacement()) {
            throw new IllegalStateException("The active creature does not need replacement");
        }
        validatePlayerSwitch(partyIndex);
        return switchPlayer(partyIndex, true);
    }

    private TurnResult takeMoveTurn(Move playerMove) {
        Creature playerCreature = playerActiveCreature();
        if (!playerCreature.knows(playerMove)) {
            throw new IllegalArgumentException("The player's creature does not know that move");
        }

        Creature opponentCreature = opponentActiveCreature();
        List<BattleEvent> events = new ArrayList<>();

        if (playerCreature.stats().speed() >= opponentCreature.stats().speed()) {
            AttackResult playerAttack = resolveAttack(
                    playerCreature,
                    playerMove,
                    opponentCreature
            );
            events.add(playerAttack);

            if (playerAttack.defenderFainted()) {
                replaceFaintedOpponent(events);
            } else {
                events.add(resolveAttack(
                        opponentCreature,
                        chooseOpponentMove(opponentCreature),
                        playerCreature
                ));
            }
        } else {
            AttackResult opponentAttack = resolveAttack(
                    opponentCreature,
                    chooseOpponentMove(opponentCreature),
                    playerCreature
            );
            events.add(opponentAttack);

            if (!opponentAttack.defenderFainted()) {
                AttackResult playerAttack = resolveAttack(
                        playerCreature,
                        playerMove,
                        opponentCreature
                );
                events.add(playerAttack);
                if (playerAttack.defenderFainted()) {
                    replaceFaintedOpponent(events);
                }
            }
        }

        return new TurnResult(events, status());
    }

    private TurnResult takeSwitchTurn(int partyIndex) {
        validatePlayerSwitch(partyIndex);

        List<BattleEvent> events = new ArrayList<>();
        events.add(switchPlayer(partyIndex, false));

        Creature opponentCreature = opponentActiveCreature();
        events.add(resolveAttack(
                opponentCreature,
                chooseOpponentMove(opponentCreature),
                playerActiveCreature()
        ));

        return new TurnResult(events, status());
    }

    private void ensureTurnCanStart() {
        if (status() != BattleStatus.IN_PROGRESS) {
            throw new IllegalStateException("The battle is already finished");
        }
        if (playerNeedsReplacement()) {
            throw new IllegalStateException("The player must replace the fainted creature");
        }
    }

    private void validatePlayerSwitch(int partyIndex) {
        if (partyIndex < 0 || partyIndex >= player.party().size()) {
            throw new IllegalArgumentException("Party index is out of range");
        }
        if (partyIndex == playerActiveIndex) {
            throw new IllegalArgumentException("That creature is already active");
        }
        if (player.party().get(partyIndex).isFainted()) {
            throw new IllegalArgumentException("A fainted creature cannot enter battle");
        }
    }

    private SwitchResult switchPlayer(int partyIndex, boolean forced) {
        Creature previous = playerActiveCreature();
        playerActiveIndex = partyIndex;
        return new SwitchResult(
                player.name(),
                previous.name(),
                playerActiveCreature().name(),
                forced
        );
    }

    private void replaceFaintedOpponent(List<BattleEvent> events) {
        if (opponent.isDefeated()) {
            return;
        }

        Creature previous = opponentActiveCreature();
        opponentActiveIndex = firstHealthyIndex(opponent.party());
        events.add(new SwitchResult(
                opponent.name(),
                previous.name(),
                opponentActiveCreature().name(),
                true
        ));
    }

    private static int firstHealthyIndex(List<Creature> party) {
        for (int index = 0; index < party.size(); index++) {
            if (!party.get(index).isFainted()) {
                return index;
            }
        }
        throw new IllegalStateException("No healthy creature is available");
    }

    private Move chooseOpponentMove(Creature opponentCreature) {
        return opponentCreature.moves().get(random.nextInt(opponentCreature.moves().size()));
    }

    private AttackResult resolveAttack(Creature attacker, Move move, Creature defender) {
        double effectiveness = move.type().effectivenessAgainst(defender.type());
        double statMultiplier = (double) attacker.stats().attack() / defender.stats().defence();
        int damage = Math.max(
                1,
                (int) Math.round(move.power() * statMultiplier * effectiveness)
        );

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
