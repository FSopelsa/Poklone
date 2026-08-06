package se.poklone.ui.swing;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public final class SwingGame {

    private SwingGame() {
    }

    public static void launch() {
        if (GraphicsEnvironment.isHeadless()) {
            throw new IllegalStateException(
                    "The graphical game needs a desktop display. Run with --demo for a headless smoke test."
            );
        }

        SwingUtilities.invokeLater(SwingGame::showWindow);
    }

    private static void showWindow() {
        useSystemLookAndFeel();

        JFrame frame = new JFrame("Poklone");
        GamePanel gamePanel = new GamePanel();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(gamePanel);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                gamePanel.closeAudio();
            }
        });
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void useSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException
                 | InstantiationException
                 | IllegalAccessException
                 | UnsupportedLookAndFeelException ignored) {
            // Swing's cross-platform look and feel is a safe fallback.
        }
    }
}
