package se.poklone.ui.swing;

import org.junit.jupiter.api.Test;
import se.poklone.domain.Battle;
import se.poklone.domain.Creature;
import se.poklone.domain.ElementType;
import se.poklone.domain.Move;
import se.poklone.domain.Stats;
import se.poklone.domain.Trainer;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Container;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BattlePanelTest {

    @Test
    void moveButtonPlaysTurnAndShowsVictory() throws Exception {
        AtomicReference<BattlePanel> panelReference = new AtomicReference<>();
        RecordingAudioPlayer audio = new RecordingAudioPlayer();

        SwingUtilities.invokeAndWait(() -> {
            BattlePanel panel = new BattlePanel(
                    BattlePanelTest::oneHitBattle,
                    "Battle again",
                    null,
                    audio
            );
            panelReference.set(panel);
            namedComponent(panel, "move-0", JButton.class).doClick();
        });

        BattlePanel panel = panelReference.get();
        JLabel opponentHealth = namedComponent(panel, "opponent-health", JLabel.class);
        JLabel playerStats = namedComponent(panel, "player-stats", JLabel.class);
        JLabel status = namedComponent(panel, "battle-status", JLabel.class);
        JTextArea log = namedComponent(panel, "battle-log", JTextArea.class);
        JButton restart = namedComponent(panel, "restart-battle", JButton.class);

        assertEquals("0 / 10 HP", opponentHealth.getText());
        assertEquals("ATK 10   DEF 10   SPD 10", playerStats.getText());
        assertEquals("Victory!", status.getText());
        assertTrue(log.getText().contains("You won the practice battle!"));
        assertTrue(restart.isVisible());
        assertEquals(List.of(SoundEffect.VICTORY), audio.effects());
    }

    @Test
    void partyButtonSwitchesActiveCreatureAndRendersOpponentResponse() throws Exception {
        AtomicReference<Battle> battleReference = new AtomicReference<>();
        AtomicReference<BattlePanel> panelReference = new AtomicReference<>();
        RecordingAudioPlayer audio = new RecordingAudioPlayer();

        SwingUtilities.invokeAndWait(() -> {
            Battle battle = switchingBattle();
            battleReference.set(battle);
            BattlePanel panel = new BattlePanel(() -> battle, "Battle again", null, audio);
            panelReference.set(panel);
            namedComponent(panel, "party-1", JButton.class).doClick();
        });

        Battle battle = battleReference.get();
        JTextArea log = namedComponent(panelReference.get(), "battle-log", JTextArea.class);

        assertEquals(1, battle.playerActiveIndex());
        assertEquals(19, battle.playerActiveCreature().currentHealth());
        assertTrue(log.getText().contains("You switched from Sprig to Cinder."));
        assertEquals(List.of(SoundEffect.SWITCH), audio.effects());
    }

    private static Battle oneHitBattle() {
        Move move = new Move("Finish", ElementType.NORMAL, 10);
        return new Battle(
                new Trainer(
                        "Player",
                        new Creature(
                                "Sprig",
                                ElementType.GRASS,
                                20,
                                new Stats(10, 10, 10),
                                List.of(move)
                        )
                ),
                new Trainer(
                        "Opponent",
                        new Creature(
                                "Drop",
                                ElementType.WATER,
                                10,
                                new Stats(10, 10, 5),
                                List.of(new Move("Bump", ElementType.NORMAL, 1))
                        )
                ),
                new Random(1)
        );
    }

    private static Battle switchingBattle() {
        Move tap = new Move("Tap", ElementType.NORMAL, 1);
        Creature first = new Creature(
                "Sprig",
                ElementType.GRASS,
                20,
                new Stats(10, 10, 10),
                List.of(tap)
        );
        Creature reserve = new Creature(
                "Cinder",
                ElementType.FIRE,
                20,
                new Stats(10, 10, 10),
                List.of(tap)
        );
        Creature opponent = new Creature(
                "Drop",
                ElementType.WATER,
                20,
                new Stats(10, 10, 5),
                List.of(new Move("Bump", ElementType.NORMAL, 1))
        );
        return new Battle(
                new Trainer("Player", List.of(first, reserve)),
                new Trainer("Opponent", opponent),
                new Random(1)
        );
    }

    private static <T extends JComponent> T namedComponent(
            Container root,
            String name,
            Class<T> type
    ) {
        for (Component component : root.getComponents()) {
            if (name.equals(component.getName()) && type.isInstance(component)) {
                return type.cast(component);
            }
            if (component instanceof Container child) {
                try {
                    return namedComponent(child, name, type);
                } catch (AssertionError ignored) {
                    // Keep searching sibling components.
                }
            }
        }
        throw new AssertionError("No component named " + name);
    }
}
