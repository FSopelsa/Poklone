package se.poklone.application;

import org.junit.jupiter.api.Test;
import se.poklone.domain.Battle;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GameContentTest {

    @Test
    void eachBattleGetsIndependentCreatureState() {
        Battle first = GameContent.createBattle(new Random(1));
        Battle second = GameContent.createBattle(new Random(1));

        first.opponent().activeCreature().takeDamage(10);

        assertEquals(40, first.opponent().activeCreature().currentHealth());
        assertEquals(50, second.opponent().activeCreature().currentHealth());
    }
}
