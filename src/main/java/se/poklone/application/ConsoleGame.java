package se.poklone.application;

import se.poklone.domain.AttackResult;
import se.poklone.domain.Battle;
import se.poklone.domain.BattleEvent;
import se.poklone.domain.BattleStatus;
import se.poklone.domain.Creature;
import se.poklone.domain.Move;
import se.poklone.domain.MoveChoice;
import se.poklone.domain.SwitchChoice;
import se.poklone.domain.SwitchResult;
import se.poklone.domain.TurnChoice;
import se.poklone.domain.TurnResult;

import java.io.PrintStream;
import java.util.Comparator;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;
import java.util.random.RandomGenerator;

public final class ConsoleGame {

    private final Scanner input;
    private final PrintStream output;
    private final RandomGenerator random;

    public ConsoleGame(Scanner input, PrintStream output, RandomGenerator random) {
        this.input = Objects.requireNonNull(input, "Input must not be null");
        this.output = Objects.requireNonNull(output, "Output must not be null");
        this.random = Objects.requireNonNull(random, "Random generator must not be null");
    }

    public static ConsoleGame createDefault() {
        return new ConsoleGame(new Scanner(System.in), System.out, new Random());
    }

    public static ConsoleGame createDemo() {
        return new ConsoleGame(new Scanner(""), System.out, new Random(7));
    }

    public void runInteractive() {
        Battle battle = createBattle();

        printIntroduction(battle);
        while (battle.status() == BattleStatus.IN_PROGRESS) {
            if (battle.playerNeedsReplacement()) {
                Integer replacement = askForPartyMember(battle, true);
                if (replacement == null) {
                    output.println("You leave the practice battle.");
                    return;
                }
                printSwitch(battle.replaceFaintedPlayer(replacement));
                continue;
            }

            printHealth(battle);
            TurnChoice choice = askForTurnChoice(battle);
            if (choice == null) {
                output.println("You leave the practice battle.");
                return;
            }
            printTurn(battle.takeTurn(choice));
        }
        printOutcome(battle.status());
    }

    public BattleStatus runDemo() {
        Battle battle = createBattle();

        output.println("Running automated Poklone battle demo...");
        while (battle.status() == BattleStatus.IN_PROGRESS) {
            if (battle.playerNeedsReplacement()) {
                int replacement = firstAvailableReplacement(battle);
                printSwitch(battle.replaceFaintedPlayer(replacement));
                continue;
            }

            Creature playerCreature = battle.playerActiveCreature();
            Creature opponentCreature = battle.opponentActiveCreature();
            Move selectedMove = playerCreature.moves().stream()
                    .max(Comparator.comparingDouble(move ->
                            move.power() * move.type().effectivenessAgainst(opponentCreature.type())
                    ))
                    .orElseThrow();
            printTurn(battle.takeTurn(selectedMove));
        }
        printOutcome(battle.status());
        return battle.status();
    }

    private Battle createBattle() {
        return GameContent.createBattle(random);
    }

    private void printIntroduction(Battle battle) {
        output.println("=== Poklone: First Practice Battle ===");
        output.printf(
                "%s challenges you with a party of %d!%n",
                battle.opponent().name(),
                battle.opponent().party().size()
        );
        output.printf("Go, %s!%n%n", battle.playerActiveCreature().name());
    }

    private void printHealth(Battle battle) {
        Creature player = battle.playerActiveCreature();
        Creature opponent = battle.opponentActiveCreature();

        output.printf(
                "%s: %d/%d HP | %s: %d/%d HP%n",
                player.name(),
                player.currentHealth(),
                player.maxHealth(),
                opponent.name(),
                opponent.currentHealth(),
                opponent.maxHealth()
        );
    }

    private TurnChoice askForTurnChoice(Battle battle) {
        Creature creature = battle.playerActiveCreature();
        while (true) {
            output.println("Choose an action:");
            for (int index = 0; index < creature.moves().size(); index++) {
                Move move = creature.moves().get(index);
                output.printf(
                        "  %d. %s (%s, power %d)%n",
                        index + 1,
                        move.name(),
                        move.type(),
                        move.power()
                );
            }
            output.println("  s. Switch creature");
            output.print("> ");

            if (!input.hasNextLine()) {
                return null;
            }

            String answer = input.nextLine().trim();
            if (answer.equalsIgnoreCase("q")) {
                return null;
            }
            if (answer.equalsIgnoreCase("s")) {
                Integer partyIndex = askForPartyMember(battle, false);
                return partyIndex == null ? null : new SwitchChoice(partyIndex);
            }

            try {
                int selectedIndex = Integer.parseInt(answer) - 1;
                if (selectedIndex >= 0 && selectedIndex < creature.moves().size()) {
                    return new MoveChoice(creature.moves().get(selectedIndex));
                }
            } catch (NumberFormatException ignored) {
                // The message below covers non-numeric and out-of-range input.
            }

            output.println("Enter a move number, s to switch, or q to quit.");
        }
    }

    private Integer askForPartyMember(Battle battle, boolean forced) {
        while (true) {
            output.println(forced ? "Choose a replacement:" : "Choose a creature:");
            for (int index = 0; index < battle.player().party().size(); index++) {
                Creature creature = battle.player().party().get(index);
                String state = creature.isFainted()
                        ? "fainted"
                        : "%d/%d HP".formatted(creature.currentHealth(), creature.maxHealth());
                if (index == battle.playerActiveIndex()) {
                    state += ", active";
                }
                output.printf("  %d. %s (%s)%n", index + 1, creature.name(), state);
            }
            output.print("> ");

            if (!input.hasNextLine()) {
                return null;
            }
            String answer = input.nextLine().trim();
            if (answer.equalsIgnoreCase("q")) {
                return null;
            }

            try {
                int partyIndex = Integer.parseInt(answer) - 1;
                if (battle.canPlayerSwitchTo(partyIndex)) {
                    return partyIndex;
                }
            } catch (NumberFormatException ignored) {
                // The message below covers invalid input.
            }
            output.println("Choose a healthy reserve creature, or q to quit.");
        }
    }

    private static int firstAvailableReplacement(Battle battle) {
        for (int index = 0; index < battle.player().party().size(); index++) {
            if (battle.canPlayerSwitchTo(index)) {
                return index;
            }
        }
        throw new IllegalStateException("No replacement creature is available");
    }

    private void printTurn(TurnResult turn) {
        for (BattleEvent event : turn.events()) {
            if (event instanceof AttackResult attack) {
                printAttack(attack);
            } else if (event instanceof SwitchResult switchResult) {
                printSwitch(switchResult);
            }
        }
        output.println();
    }

    private void printAttack(AttackResult attack) {
        output.printf(
                "%s used %s and dealt %d damage to %s.%n",
                attack.attackerName(),
                attack.moveName(),
                attack.damage(),
                attack.defenderName()
        );

        if (attack.effectiveness() > 1.0) {
            output.println("It was especially effective!");
        } else if (attack.effectiveness() < 1.0) {
            output.println("It was not very effective.");
        }

        if (attack.defenderFainted()) {
            output.printf("%s fainted!%n", attack.defenderName());
        }
    }

    private void printSwitch(SwitchResult switchResult) {
        String reason = switchResult.forced() ? " sends out " : " switches to ";
        output.printf(
                "%s%s%s.%n",
                switchResult.trainerName(),
                reason,
                switchResult.newCreatureName()
        );
    }

    private void printOutcome(BattleStatus status) {
        if (status == BattleStatus.PLAYER_WON) {
            output.println("You won the practice battle!");
        } else if (status == BattleStatus.OPPONENT_WON) {
            output.println("Your party fainted. Train and try again!");
        }
    }
}
