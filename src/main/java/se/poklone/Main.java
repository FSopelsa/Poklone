package se.poklone;

import se.poklone.application.ConsoleGame;

import java.util.Arrays;

public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        if (Arrays.asList(args).contains("--demo")) {
            ConsoleGame.createDemo().runDemo();
        } else {
            ConsoleGame.createDefault().runInteractive();
        }
    }
}
