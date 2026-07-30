package se.poklone.ui.swing;

import se.poklone.application.GameContent;
import se.poklone.domain.AttackResult;
import se.poklone.domain.Battle;
import se.poklone.domain.BattleStatus;
import se.poklone.domain.Creature;
import se.poklone.domain.ElementType;
import se.poklone.domain.Move;
import se.poklone.domain.TurnResult;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Supplier;

public final class BattlePanel extends JPanel {

    private static final Color BACKGROUND = new Color(17, 25, 39);
    private static final Color SURFACE = new Color(31, 42, 55);
    private static final Color SURFACE_LIGHT = new Color(46, 59, 76);
    private static final Color TEXT = new Color(238, 242, 247);
    private static final Color MUTED_TEXT = new Color(171, 184, 201);
    private static final Color ACCENT = new Color(71, 207, 173);
    private static final Color DANGER = new Color(235, 87, 87);
    private static final Color WARNING = new Color(245, 181, 62);

    private final Supplier<Battle> battleFactory;
    private final CreatureCard playerCard = new CreatureCard("YOUR CREATURE");
    private final CreatureCard opponentCard = new CreatureCard("OPPONENT");
    private final JPanel movePanel = new JPanel(new GridLayout(0, 2, 10, 10));
    private final JTextArea battleLog = new JTextArea();
    private final JLabel statusLabel = new JLabel("", SwingConstants.CENTER);
    private final JButton restartButton = new JButton("Battle again");
    private final List<JButton> moveButtons = new ArrayList<>();

    private Battle battle;
    private int turnNumber;

    public BattlePanel() {
        this(() -> GameContent.createBattle(new Random()));
    }

    BattlePanel(Supplier<Battle> battleFactory) {
        this.battleFactory = Objects.requireNonNull(battleFactory, "Battle factory must not be null");

        setLayout(new BorderLayout(18, 18));
        setBackground(BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(22, 24, 22, 24));
        setPreferredSize(new Dimension(920, 680));

        add(createHeader(), BorderLayout.NORTH);
        add(createBattlefield(), BorderLayout.CENTER);
        add(createCommandArea(), BorderLayout.SOUTH);

        startNewBattle();
    }

    private JComponent createHeader() {
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("POKLONE");
        title.setForeground(TEXT);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 28f));
        title.setAlignmentX(LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("First Practice Battle");
        subtitle.setForeground(ACCENT);
        subtitle.setFont(subtitle.getFont().deriveFont(Font.BOLD, 13f));
        subtitle.setAlignmentX(LEFT_ALIGNMENT);

        header.add(title);
        header.add(Box.createVerticalStrut(3));
        header.add(subtitle);
        return header;
    }

    private JComponent createBattlefield() {
        JPanel battlefield = new JPanel(new GridLayout(1, 2, 18, 0));
        battlefield.setOpaque(false);
        battlefield.add(opponentCard);
        battlefield.add(playerCard);
        return battlefield;
    }

    private JComponent createCommandArea() {
        JPanel commandArea = new JPanel(new BorderLayout(14, 0));
        commandArea.setOpaque(false);
        commandArea.setPreferredSize(new Dimension(880, 255));

        battleLog.setName("battle-log");
        battleLog.setEditable(false);
        battleLog.setLineWrap(true);
        battleLog.setWrapStyleWord(true);
        battleLog.setBackground(new Color(12, 19, 30));
        battleLog.setForeground(TEXT);
        battleLog.setCaretColor(TEXT);
        battleLog.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        battleLog.setMargin(new Insets(12, 12, 12, 12));

        JScrollPane logScroll = new JScrollPane(battleLog);
        logScroll.setBorder(BorderFactory.createLineBorder(SURFACE_LIGHT));
        logScroll.setPreferredSize(new Dimension(500, 230));
        commandArea.add(logScroll, BorderLayout.CENTER);

        JPanel actions = new JPanel(new BorderLayout(0, 10));
        actions.setOpaque(false);
        actions.setPreferredSize(new Dimension(340, 230));

        statusLabel.setName("battle-status");
        statusLabel.setForeground(MUTED_TEXT);
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.BOLD, 14f));
        actions.add(statusLabel, BorderLayout.NORTH);

        movePanel.setOpaque(false);
        actions.add(movePanel, BorderLayout.CENTER);

        styleRestartButton();
        restartButton.addActionListener(event -> startNewBattle());
        restartButton.setVisible(false);
        actions.add(restartButton, BorderLayout.SOUTH);

        commandArea.add(actions, BorderLayout.EAST);
        return commandArea;
    }

    private void styleRestartButton() {
        restartButton.setName("restart-battle");
        restartButton.setUI(new BasicButtonUI());
        restartButton.setBackground(ACCENT);
        restartButton.setForeground(BACKGROUND);
        restartButton.setOpaque(true);
        restartButton.setContentAreaFilled(true);
        restartButton.setFont(restartButton.getFont().deriveFont(Font.BOLD, 14f));
        restartButton.setFocusPainted(false);
        restartButton.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
    }

    private void startNewBattle() {
        battle = Objects.requireNonNull(battleFactory.get(), "Battle factory must return a battle");
        turnNumber = 0;
        battleLog.setText("");
        restartButton.setVisible(false);

        Creature player = battle.player().activeCreature();
        Creature opponent = battle.opponent().activeCreature();
        playerCard.showCreature(player);
        opponentCard.showCreature(opponent);
        rebuildMoveButtons(player.moves());

        statusLabel.setForeground(MUTED_TEXT);
        statusLabel.setText("Choose your move");
        appendLine("%s sent out %s.", battle.opponent().name(), opponent.name());
        appendLine("Go, %s!", player.name());
    }

    private void rebuildMoveButtons(List<Move> moves) {
        movePanel.removeAll();
        moveButtons.clear();

        for (int index = 0; index < moves.size(); index++) {
            Move move = moves.get(index);
            JButton button = createMoveButton(move, index);
            moveButtons.add(button);
            movePanel.add(button);
        }

        movePanel.revalidate();
        movePanel.repaint();
    }

    private JButton createMoveButton(Move move, int index) {
        JButton button = new JButton(
                "<html><center><b>" + move.name() + "</b><br>"
                        + move.type() + " · power " + move.power() + "</center></html>"
        );
        button.setName("move-" + index);
        button.setUI(new BasicButtonUI());
        button.setBackground(typeColor(move.type()));
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(typeColor(move.type()).brighter()),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        button.addActionListener(event -> playMove(move));
        return button;
    }

    private void playMove(Move move) {
        if (battle.status() != BattleStatus.IN_PROGRESS) {
            return;
        }

        turnNumber++;
        appendLine("");
        appendLine("TURN %d", turnNumber);

        TurnResult turn = battle.takeTurn(move);
        for (AttackResult attack : turn.attacks()) {
            appendAttack(attack);
        }

        playerCard.showCreature(battle.player().activeCreature());
        opponentCard.showCreature(battle.opponent().activeCreature());

        if (turn.status() == BattleStatus.IN_PROGRESS) {
            statusLabel.setText("Choose your move");
            return;
        }

        finishBattle(turn.status());
    }

    private void appendAttack(AttackResult attack) {
        appendLine(
                "%s used %s — %d damage.",
                attack.attackerName(),
                attack.moveName(),
                attack.damage()
        );
        if (attack.effectiveness() > 1.0) {
            appendLine("It was especially effective!");
        } else if (attack.effectiveness() < 1.0) {
            appendLine("It was not very effective.");
        }
        if (attack.defenderFainted()) {
            appendLine("%s fainted!", attack.defenderName());
        }
    }

    private void finishBattle(BattleStatus status) {
        boolean playerWon = status == BattleStatus.PLAYER_WON;
        String message = playerWon ? "Victory!" : "Defeated";

        statusLabel.setText(message);
        statusLabel.setForeground(playerWon ? ACCENT : DANGER);
        appendLine("");
        appendLine(playerWon
                ? "You won the practice battle!"
                : "Your creature fainted. Train and try again!");

        moveButtons.forEach(button -> button.setEnabled(false));
        restartButton.setVisible(true);
    }

    private void appendLine(String format, Object... arguments) {
        battleLog.append(format.formatted(arguments));
        battleLog.append(System.lineSeparator());
        battleLog.setCaretPosition(battleLog.getDocument().getLength());
    }

    private static Color typeColor(ElementType type) {
        return switch (type) {
            case NORMAL -> new Color(91, 103, 120);
            case FIRE -> new Color(202, 83, 60);
            case WATER -> new Color(47, 116, 190);
            case GRASS -> new Color(55, 139, 91);
        };
    }

    private static final class CreatureCard extends JPanel {

        private final JLabel roleLabel = new JLabel();
        private final CreatureAvatar avatar = new CreatureAvatar();
        private final JLabel nameLabel = new JLabel("", SwingConstants.CENTER);
        private final JLabel typeLabel = new JLabel("", SwingConstants.CENTER);
        private final JLabel healthLabel = new JLabel("", SwingConstants.CENTER);
        private final JProgressBar healthBar = new JProgressBar();

        private CreatureCard(String role) {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBackground(SURFACE);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(SURFACE_LIGHT),
                    BorderFactory.createEmptyBorder(16, 18, 16, 18)
            ));

            roleLabel.setText(role);
            roleLabel.setForeground(MUTED_TEXT);
            roleLabel.setFont(roleLabel.getFont().deriveFont(Font.BOLD, 11f));
            roleLabel.setAlignmentX(CENTER_ALIGNMENT);

            avatar.setAlignmentX(CENTER_ALIGNMENT);

            nameLabel.setForeground(TEXT);
            nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD, 22f));
            nameLabel.setAlignmentX(CENTER_ALIGNMENT);

            typeLabel.setForeground(MUTED_TEXT);
            typeLabel.setFont(typeLabel.getFont().deriveFont(Font.BOLD, 12f));
            typeLabel.setAlignmentX(CENTER_ALIGNMENT);

            healthLabel.setForeground(TEXT);
            healthLabel.setFont(healthLabel.getFont().deriveFont(Font.BOLD, 12f));
            healthLabel.setAlignmentX(CENTER_ALIGNMENT);

            healthBar.setMinimum(0);
            healthBar.setStringPainted(false);
            healthBar.setBorderPainted(false);
            healthBar.setBackground(new Color(12, 19, 30));
            healthBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 14));

            add(roleLabel);
            add(Box.createVerticalGlue());
            add(avatar);
            add(Box.createVerticalStrut(8));
            add(nameLabel);
            add(Box.createVerticalStrut(3));
            add(typeLabel);
            add(Box.createVerticalGlue());
            add(healthLabel);
            add(Box.createVerticalStrut(6));
            add(healthBar);
        }

        private void showCreature(Creature creature) {
            nameLabel.setText(creature.name());
            typeLabel.setText(creature.type().name());
            healthLabel.setText("%d / %d HP".formatted(
                    creature.currentHealth(),
                    creature.maxHealth()
            ));
            healthLabel.setName(roleLabel.getText().equals("OPPONENT")
                    ? "opponent-health"
                    : "player-health");
            healthBar.setMaximum(creature.maxHealth());
            healthBar.setValue(creature.currentHealth());
            healthBar.setForeground(healthColor(creature));
            avatar.showCreature(creature);
        }

        private static Color healthColor(Creature creature) {
            double remaining = (double) creature.currentHealth() / creature.maxHealth();
            if (remaining > 0.5) {
                return ACCENT;
            }
            if (remaining > 0.25) {
                return WARNING;
            }
            return DANGER;
        }
    }

    private static final class CreatureAvatar extends JComponent {

        private String initial = "?";
        private Color color = SURFACE_LIGHT;

        private CreatureAvatar() {
            setPreferredSize(new Dimension(150, 150));
            setMinimumSize(new Dimension(150, 150));
            setMaximumSize(new Dimension(150, 150));
        }

        private void showCreature(Creature creature) {
            initial = creature.name().substring(0, 1).toUpperCase();
            color = typeColor(creature.type());
            repaint();
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);

            Graphics2D canvas = (Graphics2D) graphics.create();
            try {
                canvas.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                int diameter = Math.min(getWidth(), getHeight()) - 20;
                int x = (getWidth() - diameter) / 2;
                int y = (getHeight() - diameter) / 2;

                canvas.setColor(color.darker());
                canvas.fillOval(x + 5, y + 8, diameter, diameter);
                canvas.setColor(color);
                canvas.fillOval(x, y, diameter, diameter);
                canvas.setColor(color.brighter());
                canvas.setStroke(new BasicStroke(3f));
                canvas.drawOval(x, y, diameter, diameter);

                canvas.setColor(new Color(255, 255, 255, 45));
                canvas.fillOval(x + 22, y + 18, diameter / 3, diameter / 4);

                canvas.setColor(Color.WHITE);
                canvas.setFont(getFont().deriveFont(Font.BOLD, 54f));
                int textWidth = canvas.getFontMetrics().stringWidth(initial);
                int textX = (getWidth() - textWidth) / 2;
                int textY = (getHeight() + canvas.getFontMetrics().getAscent()) / 2 - 8;
                canvas.drawString(initial, textX, textY);
            } finally {
                canvas.dispose();
            }
        }
    }
}
