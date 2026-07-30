package se.poklone.application;

import se.poklone.domain.AttackResult;
import se.poklone.domain.Battle;
import se.poklone.domain.BattleStatus;
import se.poklone.domain.Creature;
import se.poklone.domain.Move;
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
            printHealth(battle);
            Move selectedMove = askForMove(battle.player().activeCreature());
            if (selectedMove == null) {
                output.println("You leave the practice battle.");
                return;
            }
            printTurn(battle.takeTurn(selectedMove));
        }
        printOutcome(battle.status());
    }

    public BattleStatus runDemo() {
        Battle battle = createBattle();

        output.println("Running automated Poklone battle demo...");
        while (battle.status() == BattleStatus.IN_PROGRESS) {
            Creature playerCreature = battle.player().activeCreature();
            Move selectedMove = playerCreature.moves().stream()
                    .max(Comparator.comparingInt(Move::power))
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
                "%s challenges you with %s!%n",
                battle.opponent().name(),
                battle.opponent().activeCreature().name()
        );
        output.printf("Go, %s!%n%n", battle.player().activeCreature().name());
    }

    private void printHealth(Battle battle) {
        Creature player = battle.player().activeCreature();
        Creature opponent = battle.opponent().activeCreature();

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

    private Move askForMove(Creature creature) {
        while (true) {
            output.println("Choose a move:");
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
            output.print("> ");

            if (!input.hasNextLine()) {
                return null;
            }

            String answer = input.nextLine().trim();
            if (answer.equalsIgnoreCase("q")) {
                return null;
            }

            try {
                int selectedIndex = Integer.parseInt(answer) - 1;
                if (selectedIndex >= 0 && selectedIndex < creature.moves().size()) {
                    return creature.moves().get(selectedIndex);
                }
            } catch (NumberFormatException ignored) {
                // The message below covers non-numeric and out-of-range input.
            }

            output.println("Enter a move number, or q to quit.");
        }
    }

    private void printTurn(TurnResult turn) {
        for (AttackResult attack : turn.attacks()) {
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
        output.println();
    }

    private void printOutcome(BattleStatus status) {
        if (status == BattleStatus.PLAYER_WON) {
            output.println("You won the practice battle!");
        } else if (status == BattleStatus.OPPONENT_WON) {
            output.println("Your creature fainted. Train and try again!");
        }
    }
}
