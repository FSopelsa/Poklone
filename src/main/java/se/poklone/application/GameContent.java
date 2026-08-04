package se.poklone.application;

import se.poklone.domain.Battle;
import se.poklone.domain.Creature;
import se.poklone.domain.ElementType;
import se.poklone.domain.Move;
import se.poklone.domain.Position;
import se.poklone.domain.Stats;
import se.poklone.domain.Trainer;
import se.poklone.domain.WorldMap;

import java.util.List;
import java.util.Objects;
import java.util.random.RandomGenerator;

public final class GameContent {

    private GameContent() {
    }

    public static Battle createBattle(RandomGenerator random) {
        return createBattle(createPlayer(), random);
    }

    public static Battle createBattle(Trainer player, RandomGenerator random) {
        return new Battle(
                Objects.requireNonNull(player, "Player must not be null"),
                createOpponent(),
                Objects.requireNonNull(random, "Random generator must not be null")
        );
    }

    public static Trainer createPlayer() {
        Creature mossling = new Creature(
                "Mossling",
                ElementType.GRASS,
                48,
                new Stats(12, 10, 14),
                List.of(
                        new Move("Nudge", ElementType.NORMAL, 7),
                        new Move("Vine Snap", ElementType.GRASS, 11),
                        new Move("Leaf Burst", ElementType.GRASS, 15)
                )
        );
        Creature embercub = new Creature(
                "Embercub",
                ElementType.FIRE,
                44,
                new Stats(14, 9, 12),
                List.of(
                        new Move("Nudge", ElementType.NORMAL, 7),
                        new Move("Coal Spark", ElementType.FIRE, 11),
                        new Move("Flame Pounce", ElementType.FIRE, 15)
                )
        );
        return new Trainer("You", List.of(mossling, embercub));
    }

    public static Trainer createOpponent() {
        Creature brookfin = new Creature(
                "Brookfin",
                ElementType.WATER,
                50,
                new Stats(11, 12, 10),
                List.of(
                        new Move("Bump", ElementType.NORMAL, 7),
                        new Move("Bubble Rush", ElementType.WATER, 10),
                        new Move("Water Jet", ElementType.WATER, 14)
                )
        );
        Creature stonebeak = new Creature(
                "Stonebeak",
                ElementType.NORMAL,
                42,
                new Stats(12, 10, 11),
                List.of(
                        new Move("Peck", ElementType.NORMAL, 8),
                        new Move("Wing Rush", ElementType.NORMAL, 12)
                )
        );
        return new Trainer("Scout Mira", List.of(brookfin, stonebeak));
    }

    public static WorldMap createPracticeRoom() {
        return new WorldMap(List.of(
                "#############",
                "#...........#",
                "#.###.###...#",
                "#.....#.....#",
                "#.###.#.###.#",
                "#.....#...E.#",
                "#.#####.....#",
                "#...........#",
                "#############"
        ));
    }

    public static Position practiceRoomStart() {
        return new Position(1, 1);
    }
}
