package se.poklone.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WorldMapTest {

    @Test
    void classifiesWallsFloorAndEncounterTiles() {
        WorldMap world = new WorldMap(List.of("###", "#.E", "###"));

        assertFalse(world.isWalkable(new Position(0, 0)));
        assertTrue(world.isWalkable(new Position(1, 1)));
        assertEquals(WorldTile.ENCOUNTER, world.tileAt(new Position(2, 1)));
        assertFalse(world.isWalkable(new Position(3, 1)));
    }

    @Test
    void rejectsRaggedOrUnknownMapRows() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new WorldMap(List.of("###", "##"))
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> new WorldMap(List.of("#?#"))
        );
    }
}
