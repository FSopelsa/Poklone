package se.poklone.application;

import se.poklone.domain.Battle;
import se.poklone.domain.Creature;
import se.poklone.domain.ElementType;
import se.poklone.domain.Move;
import se.poklone.domain.Trainer;

import java.util.List;
import java.util.Objects;
import java.util.random.RandomGenerator;

public final class GameContent {

    private GameContent() {
    }

    public static Battle createBattle(RandomGenerator random) {
        return new Battle(
                createPlayer(),
                createOpponent(),
                Objects.requireNonNull(random, "Random generator must not be null")
        );
    }

    public static Trainer createPlayer() {
        Creature mossling = new Creature(
                "Mossling",
                ElementType.GRASS,
                48,
                List.of(
                        new Move("Nudge", ElementType.NORMAL, 7),
                        new Move("Vine Snap", ElementType.GRASS, 11),
                        new Move("Leaf Burst", ElementType.GRASS, 15)
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
                        new Move("Bubble Rush", ElementType.WATER, 10),
                        new Move("Water Jet", ElementType.WATER, 14)
                )
        );
        return new Trainer("Scout Mira", brookfin);
    }
}

