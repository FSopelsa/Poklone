package se.poklone.ui.swing;

import se.poklone.application.GameSession;

import javax.swing.JPanel;
import java.awt.CardLayout;
import java.util.Objects;

public final class GamePanel extends JPanel {

    private static final String WORLD_CARD = "world";
    private static final String BATTLE_CARD = "battle";

    private final CardLayout cards = new CardLayout();
    private final GameSession session;
    private final AudioPlayer audio;
    private final WorldPanel worldPanel;
    private BattlePanel battlePanel;

    public GamePanel() {
        this(GameSession.createDefault(), AudioPlayer.systemDefault());
    }

    GamePanel(GameSession session) {
        this(session, AudioPlayer.silent());
    }

    GamePanel(GameSession session, AudioPlayer audio) {
        this.session = Objects.requireNonNull(session, "Game session must not be null");
        this.audio = Objects.requireNonNull(audio, "Audio player must not be null");
        setLayout(cards);
        worldPanel = new WorldPanel(session, this::showBattle, audio);
        add(worldPanel, WORLD_CARD);
        cards.show(this, WORLD_CARD);
        audio.loop(MusicTrack.WORLD);
    }

    private void showBattle() {
        audio.stopMusic();
        if (battlePanel != null) {
            remove(battlePanel);
        }
        battlePanel = new BattlePanel(
                session::battle,
                "Return to room",
                this::returnToWorld,
                audio
        );
        add(battlePanel, BATTLE_CARD);
        cards.show(this, BATTLE_CARD);
        revalidate();
        repaint();
    }

    private void returnToWorld() {
        session.returnToWorld();
        worldPanel.refresh();
        audio.loop(MusicTrack.WORLD);
        cards.show(this, WORLD_CARD);
        revalidate();
        repaint();
    }

    void closeAudio() {
        audio.close();
    }
}
