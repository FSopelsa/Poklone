package se.poklone;

import se.poklone.application.ConsoleGame;
import se.poklone.ui.swing.SwingGame;

import java.util.Arrays;

public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        if (Arrays.asList(args).contains("--demo")) {
            ConsoleGame.createDemo().runDemo();
        } else if (Arrays.asList(args).contains("--console")) {
            ConsoleGame.createDefault().runInteractive();
        } else {
            SwingGame.launch();
        }
    }
}
