package se.poklone.ui.swing;

import org.junit.jupiter.api.Test;
import se.poklone.application.GamePhase;
import se.poklone.application.GameSession;
import se.poklone.domain.Creature;
import se.poklone.domain.ElementType;
import se.poklone.domain.Move;
import se.poklone.domain.Position;
import se.poklone.domain.Stats;
import se.poklone.domain.Trainer;
import se.poklone.domain.WorldMap;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Container;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GamePanelTest {

    @Test
    void movingOntoEncounterChangesFromWorldToBattle() throws Exception {
        GameSession session = session();
        RecordingAudioPlayer audio = new RecordingAudioPlayer();
        AtomicReference<GamePanel> panelReference = new AtomicReference<>();

        SwingUtilities.invokeAndWait(() -> {
            GamePanel panel = new GamePanel(session, audio);
            panelReference.set(panel);
            namedComponent(panel, "move-up", JButton.class).doClick();
            namedComponent(panel, "move-right", JButton.class).doClick();
        });

        GamePanel panel = panelReference.get();
        assertEquals(GamePhase.BATTLING, session.phase());
        assertTrue(List.of(panel.getComponents()).stream()
                .anyMatch(component -> component instanceof BattlePanel && component.isVisible()));
        assertFalse(List.of(panel.getComponents()).stream()
                .anyMatch(component -> component instanceof WorldPanel && component.isVisible()));
        assertEquals(List.of(MusicTrack.WORLD), audio.tracks());
        assertEquals(List.of(SoundEffect.BLOCKED, SoundEffect.ENCOUNTER), audio.effects());
        assertEquals(1, audio.musicStops());
    }

    @Test
    void soundButtonMutesSharedAudio() throws Exception {
        GameSession session = session();
        RecordingAudioPlayer audio = new RecordingAudioPlayer();
        AtomicReference<JButton> buttonReference = new AtomicReference<>();

        SwingUtilities.invokeAndWait(() -> {
            GamePanel panel = new GamePanel(session, audio);
            JButton button = namedComponent(panel, "toggle-sound", JButton.class);
            button.doClick();
            buttonReference.set(button);
        });

        assertTrue(audio.muted());
        assertEquals("Sound: off", buttonReference.get().getText());
    }

    private static GameSession session() {
        Creature creature = new Creature(
                "Sprig",
                ElementType.GRASS,
                30,
                new Stats(10, 10, 10),
                List.of(new Move("Tap", ElementType.NORMAL, 5))
        );
        return new GameSession(
                new Trainer("Player", creature),
                new WorldMap(List.of("#####", "#.E.#", "#####")),
                new Position(1, 1),
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
