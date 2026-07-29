package se.poklone.application;

import org.junit.jupiter.api.Test;
import se.poklone.domain.BattleStatus;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConsoleGameTest {

    @Test
    void automatedDemoCompletesWithAVisibleOutcome() {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ConsoleGame game = new ConsoleGame(
                new Scanner(""),
                new PrintStream(bytes, true, StandardCharsets.UTF_8),
                new Random(7)
        );

        BattleStatus status = game.runDemo();
        String output = bytes.toString(StandardCharsets.UTF_8);

        assertEquals(BattleStatus.PLAYER_WON, status);
        assertTrue(output.contains("Running automated Poklone battle demo"));
        assertTrue(output.contains("You won the practice battle!"));
    }
}
