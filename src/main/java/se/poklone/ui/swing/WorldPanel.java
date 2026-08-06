package se.poklone.ui.swing;

import se.poklone.application.GamePhase;
import se.poklone.application.GameSession;
import se.poklone.domain.Direction;
import se.poklone.domain.Position;
import se.poklone.domain.WorldMap;
import se.poklone.domain.WorldMoveResult;
import se.poklone.domain.WorldTile;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.util.Objects;

final class WorldPanel extends JPanel {

    private static final Color BACKGROUND = new Color(17, 25, 39);
    private static final Color FLOOR = new Color(48, 75, 68);
    private static final Color FLOOR_ALT = new Color(56, 87, 78);
    private static final Color WALL = new Color(61, 70, 85);
    private static final Color WALL_EDGE = new Color(98, 112, 132);
    private static final Color TEXT = new Color(238, 242, 247);
    private static final Color MUTED_TEXT = new Color(171, 184, 201);
    private static final Color ACCENT = new Color(71, 207, 173);
    private static final Color ENCOUNTER = new Color(245, 181, 62);

    private final GameSession session;
    private final Runnable battleStartedAction;
    private final AudioPlayer audio;
    private final WorldCanvas canvas = new WorldCanvas();
    private final JLabel statusLabel = new JLabel("", SwingConstants.CENTER);
    private final JButton soundButton = new JButton();

    WorldPanel(GameSession session, Runnable battleStartedAction) {
        this(session, battleStartedAction, AudioPlayer.silent());
    }

    WorldPanel(GameSession session, Runnable battleStartedAction, AudioPlayer audio) {
        this.session = Objects.requireNonNull(session, "Game session must not be null");
        this.battleStartedAction = Objects.requireNonNull(
                battleStartedAction,
                "Battle action must not be null"
        );
        this.audio = Objects.requireNonNull(audio, "Audio player must not be null");

        setLayout(new BorderLayout(18, 18));
        setBackground(BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(22, 24, 22, 24));
        setPreferredSize(new Dimension(920, 740));

        add(createHeader(), BorderLayout.NORTH);
        add(canvas, BorderLayout.CENTER);
        add(createControls(), BorderLayout.SOUTH);
        installMovementBindings();
        refresh();
    }

    private JComponent createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("PRACTICE ROOM");
        title.setForeground(TEXT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 27f));

        JLabel hint = new JLabel("Move with WASD, arrow keys, or the buttons");
        hint.setForeground(MUTED_TEXT);
        hint.setFont(hint.getFont().deriveFont(Font.PLAIN, 13f));
        JPanel right = new JPanel(new BorderLayout(12, 0));
        right.setOpaque(false);
        right.add(hint, BorderLayout.CENTER);
        right.add(createSoundButton(), BorderLayout.EAST);

        header.add(title, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    private JButton createSoundButton() {
        soundButton.setName("toggle-sound");
        soundButton.setBackground(new Color(46, 59, 76));
        soundButton.setForeground(TEXT);
        soundButton.setFocusPainted(false);
        soundButton.addActionListener(event -> {
            audio.setMuted(!audio.muted());
            if (!audio.muted()) {
                audio.loop(MusicTrack.WORLD);
            }
            updateSoundButton();
        });
        updateSoundButton();
        return soundButton;
    }

    private JComponent createControls() {
        JPanel area = new JPanel(new BorderLayout(18, 0));
        area.setOpaque(false);

        statusLabel.setName("world-status");
        statusLabel.setForeground(TEXT);
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.BOLD, 14f));
        statusLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(46, 59, 76)),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
        area.add(statusLabel, BorderLayout.CENTER);

        JPanel directions = new JPanel(new GridLayout(2, 3, 6, 6));
        directions.setOpaque(false);
        directions.add(new JLabel());
        directions.add(moveButton("Up", "move-up", Direction.UP));
        directions.add(new JLabel());
        directions.add(moveButton("Left", "move-left", Direction.LEFT));
        directions.add(moveButton("Down", "move-down", Direction.DOWN));
        directions.add(moveButton("Right", "move-right", Direction.RIGHT));
        area.add(directions, BorderLayout.EAST);
        return area;
    }

    private JButton moveButton(String text, String name, Direction direction) {
        JButton button = new JButton(text);
        button.setName(name);
        button.setBackground(new Color(46, 59, 76));
        button.setForeground(TEXT);
        button.setFocusPainted(false);
        button.addActionListener(event -> move(direction));
        return button;
    }

    private void installMovementBindings() {
        bind("pressed W", Direction.UP);
        bind("pressed UP", Direction.UP);
        bind("pressed S", Direction.DOWN);
        bind("pressed DOWN", Direction.DOWN);
        bind("pressed A", Direction.LEFT);
        bind("pressed LEFT", Direction.LEFT);
        bind("pressed D", Direction.RIGHT);
        bind("pressed RIGHT", Direction.RIGHT);
    }

    private void bind(String keyStroke, Direction direction) {
        String actionName = "move-" + direction + "-" + keyStroke;
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(keyStroke), actionName);
        getActionMap().put(actionName, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent event) {
                move(direction);
            }
        });
    }

    private void move(Direction direction) {
        if (session.phase() != GamePhase.EXPLORING) {
            return;
        }
        WorldMoveResult result = session.move(direction);
        if (result.encounterStarted()) {
            audio.play(SoundEffect.ENCOUNTER);
        } else if (result.moved()) {
            audio.play(SoundEffect.STEP);
        } else {
            audio.play(SoundEffect.BLOCKED);
        }
        refresh();
        if (result.encounterStarted()) {
            battleStartedAction.run();
        }
    }

    void refresh() {
        statusLabel.setText(session.worldMessage());
        updateSoundButton();
        canvas.repaint();
        requestFocusInWindow();
    }

    private void updateSoundButton() {
        soundButton.setText(audio.muted() ? "Sound: off" : "Sound: on");
    }

    private final class WorldCanvas extends JComponent {

        private WorldCanvas() {
            setName("world-canvas");
            setOpaque(true);
            setBackground(new Color(12, 19, 30));
            setBorder(BorderFactory.createLineBorder(new Color(46, 59, 76), 2));
            setPreferredSize(new Dimension(850, 540));
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D drawing = (Graphics2D) graphics.create();
            try {
                drawing.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );
                drawWorld(drawing);
            } finally {
                drawing.dispose();
            }
        }

        private void drawWorld(Graphics2D drawing) {
            WorldMap world = session.world();
            int tileSize = Math.max(
                    1,
                    Math.min((getWidth() - 24) / world.width(), (getHeight() - 24) / world.height())
            );
            int mapWidth = tileSize * world.width();
            int mapHeight = tileSize * world.height();
            int startX = (getWidth() - mapWidth) / 2;
            int startY = (getHeight() - mapHeight) / 2;

            for (int y = 0; y < world.height(); y++) {
                for (int x = 0; x < world.width(); x++) {
                    drawTile(drawing, world, new Position(x, y), startX, startY, tileSize);
                }
            }
            drawPlayer(drawing, startX, startY, tileSize);
        }

        private void drawTile(
                Graphics2D drawing,
                WorldMap world,
                Position position,
                int startX,
                int startY,
                int tileSize
        ) {
            int x = startX + position.x() * tileSize;
            int y = startY + position.y() * tileSize;
            WorldTile tile = world.tileAt(position);

            drawing.setColor(tile == WorldTile.WALL
                    ? WALL
                    : ((position.x() + position.y()) % 2 == 0 ? FLOOR : FLOOR_ALT));
            drawing.fillRect(x, y, tileSize, tileSize);
            drawing.setColor(tile == WorldTile.WALL ? WALL_EDGE : new Color(255, 255, 255, 18));
            drawing.drawRect(x, y, tileSize, tileSize);

            if (tile == WorldTile.ENCOUNTER && !session.encounterCleared()) {
                int inset = Math.max(5, tileSize / 5);
                drawing.setColor(ENCOUNTER);
                drawing.fillOval(x + inset, y + inset, tileSize - inset * 2, tileSize - inset * 2);
                drawing.setColor(BACKGROUND);
                drawing.setFont(getFont().deriveFont(Font.BOLD, Math.max(12f, tileSize * 0.32f)));
                String marker = "M";
                int markerX = x + (tileSize - drawing.getFontMetrics().stringWidth(marker)) / 2;
                int markerY = y + (tileSize + drawing.getFontMetrics().getAscent()) / 2 - 3;
                drawing.drawString(marker, markerX, markerY);
            }
        }

        private void drawPlayer(Graphics2D drawing, int startX, int startY, int tileSize) {
            Position player = session.playerPosition();
            int x = startX + player.x() * tileSize;
            int y = startY + player.y() * tileSize;
            int inset = Math.max(4, tileSize / 7);

            drawing.setColor(new Color(12, 19, 30, 120));
            drawing.fillOval(x + inset + 3, y + inset + 5, tileSize - inset * 2, tileSize - inset * 2);
            drawing.setColor(ACCENT);
            drawing.fillOval(x + inset, y + inset, tileSize - inset * 2, tileSize - inset * 2);
            drawing.setColor(Color.WHITE);
            drawing.setStroke(new BasicStroke(2f));
            drawing.drawOval(x + inset, y + inset, tileSize - inset * 2, tileSize - inset * 2);
        }
    }
}
