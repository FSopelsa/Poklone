package se.poklone.application;

import se.poklone.domain.Creature;
import se.poklone.domain.ElementType;
import se.poklone.domain.Move;
import se.poklone.domain.Trainer;

import java.util.List;

public final class GameContent {

    private GameContent() {
    }

    public static Trainer createPlayer() {
        Creature mossling = new Creature(
                "Mossling",
                ElementType.GRASS,
                48,
                List.of(
                        new Move("Nudge", ElementType.NORMAL, 7),
                        new Move("Vine Snap", ElementType.GRASS, 11)
                )
        );
        return new Trainer("You", mossling);
    }

    public static Trainer createOpponent() {
        Creature brookfin = new Creature(
                "Brookfin",
                ElementType.WATER,
                50,
                List.of(
                        new Move("Bump", ElementType.NORMAL, 7),
                        new Move("Bubble Rush", ElementType.WATER, 10)
                )
        );
        return new Trainer("Scout Mira", brookfin);
    }
}

