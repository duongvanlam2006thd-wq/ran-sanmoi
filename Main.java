import controller.GameController;
import view.GamePanel;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * ENTRY POINT — Main
 *
 * Bootstraps the Snake game:
 * 1. Creates the GamePanel (View)
 * 2. Creates the GameController (wires up models, timer, and key listener)
 * 3. Wraps the panel in a JFrame and shows the window
 *
 * All Swing operations run on the Event Dispatch Thread (EDT) as required.
 */
public class Main {

    public static void main(String[] args) {
        // Swing must be constructed and accessed on the EDT
        SwingUtilities.invokeLater(() -> {
            // 1. Create view
            GamePanel gamePanel = new GamePanel();

            // 2. Create controller — internally creates models and hooks them to the panel
            new GameController(gamePanel);

            // 3. Build the window
            JFrame frame = new JFrame("Snake \u2014 R\u1eafn S\u0103n M\u1ed3i");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.add(gamePanel);
            frame.pack(); // size frame to panel's preferred size
            frame.setLocationRelativeTo(null); // center on screen
            frame.setVisible(true);

            // Give the panel keyboard focus so key events are received immediately
            gamePanel.requestFocusInWindow();
        });
    }
}
