package view;

import model.FoodModel;
import model.GameState;
import model.ObstacleModel;
import model.SnakeModel;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;

/**
 * VIEW — GamePanel
 * The single JPanel that renders the entire game via paintComponent().
 *
 * Responsibilities:
 * - Draw the HUD (score / level / high score bar at the top)
 * - Draw the grid, snake, food, and obstacles
 * - Show overlay screens: Start, Pause, Game Over
 * - Expose a Restart button (visible only on Game Over)
 *
 * This class has NO knowledge of GameController — it receives model
 * references and a Runnable restart callback from the controller.
 */
public class GamePanel extends JPanel {

    // ---- Grid & panel size constants (used by both View and Controller) ----
    public static final int CELL_SIZE = 20; // pixels per grid cell
    public static final int GRID_WIDTH = 30; // cells horizontally
    public static final int GRID_HEIGHT = 30; // cells vertically
    public static final int HUD_HEIGHT = 30; // pixel height of the top HUD bar
    public static final int PANEL_WIDTH = CELL_SIZE * GRID_WIDTH;
    public static final int PANEL_HEIGHT = CELL_SIZE * GRID_HEIGHT + HUD_HEIGHT;

    // ---- Model references injected by the controller ----
    private SnakeModel snakeModel;
    private FoodModel foodModel;
    private GameState gameState;
    private ObstacleModel obstacleModel;

    /** Called when the Restart button is clicked */
    private Runnable restartAction;

    /** Restart button — only visible when the game is over */
    private final JButton restartButton;

    /**
     * Snake / UI colors per level.
     * The index cycles if the player surpasses the last color index.
     */
    private static final Color[] LEVEL_COLORS = {
            new Color(0, 210, 0), // Level 1 — green
            new Color(30, 144, 255), // Level 2 — dodger-blue
            new Color(255, 165, 0), // Level 3 — orange
            new Color(180, 0, 220), // Level 4 — purple
            new Color(220, 30, 30), // Level 5+ — red
    };

    // ================================================================
    // Construction
    // ================================================================

    public GamePanel() {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        setLayout(null); // absolute layout so the button can float freely

        // Build the restart button
        restartButton = new JButton("  Restart  (R)");
        restartButton.setBounds(PANEL_WIDTH / 2 - 70, PANEL_HEIGHT / 2 + 55, 140, 36);
        restartButton.setFont(new Font("Arial", Font.BOLD, 13));
        restartButton.setBackground(new Color(40, 180, 40));
        restartButton.setForeground(Color.WHITE);
        restartButton.setFocusPainted(false);
        restartButton.setFocusable(false); // keep keyboard focus on the panel
        restartButton.setBorderPainted(false);
        restartButton.setOpaque(true);
        restartButton.setVisible(false);
        add(restartButton);

        restartButton.addActionListener(e -> {
            if (restartAction != null)
                restartAction.run();
            requestFocusInWindow();
        });
    }

    // ================================================================
    // Setters called by the Controller
    // ================================================================

    /**
     * Injects model references into the panel.
     * Must be called before the first repaint.
     */
    public void setModels(SnakeModel snake, FoodModel food,
            GameState state, ObstacleModel obstacles) {
        this.snakeModel = snake;
        this.foodModel = food;
        this.gameState = state;
        this.obstacleModel = obstacles;
    }

    /** The controller passes this::restartGame as the action. */
    public void setRestartAction(Runnable action) {
        this.restartAction = action;
    }

    /** Schedules a repaint (called once per game-loop tick by the controller). */
    public void refresh() {
        repaint();
    }

    // ================================================================
    // paintComponent — master render method
    // ================================================================

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (gameState == null)
            return;

        drawHUD(g);
        drawGrid(g);
        drawObstacles(g);
        drawFood(g);
        drawSnake(g);

        // --- Overlay screens (drawn on top of game elements) ---
        if (!gameState.isRunning() && !gameState.isGameOver()) {
            drawStartScreen(g);
        }
        if (gameState.isPaused()) {
            drawPauseScreen(g);
        }
        if (gameState.isGameOver()) {
            drawGameOverScreen(g);
            restartButton.setVisible(true);
        } else {
            restartButton.setVisible(false);
        }
    }

    // ================================================================
    // Draw helpers
    // ================================================================

    /**
     * Draws the top HUD bar containing score, level, and high-score.
     * A thin colored line at the bottom of the bar reflects the current level
     * color.
     */
    private void drawHUD(Graphics g) {
        // Dark background for the bar
        g.setColor(new Color(18, 18, 18));
        g.fillRect(0, 0, PANEL_WIDTH, HUD_HEIGHT);

        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.setColor(Color.WHITE);
        g.drawString("Score: " + gameState.getScore(), 10, 20);
        g.drawString("Level: " + gameState.getLevel(), PANEL_WIDTH / 2 - 32, 20);
        g.drawString("Best:  " + gameState.getHighScore(), PANEL_WIDTH - 115, 20);

        // Level-colored accent line at the bottom of the HUD
        g.setColor(getLevelColor(gameState.getLevel()));
        g.fillRect(0, HUD_HEIGHT - 3, PANEL_WIDTH, 3);
    }

    /**
     * Draws a subtle grid over the playing area (below the HUD).
     */
    private void drawGrid(Graphics g) {
        g.setColor(new Color(22, 22, 22));
        for (int x = 0; x <= PANEL_WIDTH; x += CELL_SIZE) {
            g.drawLine(x, HUD_HEIGHT, x, PANEL_HEIGHT);
        }
        for (int y = HUD_HEIGHT; y <= PANEL_HEIGHT; y += CELL_SIZE) {
            g.drawLine(0, y, PANEL_WIDTH, y);
        }
    }

    /**
     * Draws the snake body with a gradient from bright head to darker tail.
     * Colors shift according to the current level.
     */
    private void drawSnake(Graphics g) {
        if (snakeModel == null)
            return;
        LinkedList<Point> body = snakeModel.getBody();
        Color base = getLevelColor(gameState.getLevel());

        for (int i = 0; i < body.size(); i++) {
            Point p = body.get(i);
            int px = p.x * CELL_SIZE;
            int py = p.y * CELL_SIZE + HUD_HEIGHT;

            if (i == 0) {
                g.setColor(base.brighter()); // head — brightest
            } else {
                // Gradually darken towards the tail
                float factor = Math.max(0.25f, 1.0f - (float) i / body.size() * 0.75f);
                g.setColor(darken(base, factor));
            }
            g.fillRoundRect(px + 1, py + 1, CELL_SIZE - 2, CELL_SIZE - 2, 6, 6);
        }

        drawSnakeEyes(g, snakeModel.getHead(), snakeModel.getDirection());
    }

    /**
     * Draws two white eye dots on the head facing the current direction.
     */
    private void drawSnakeEyes(Graphics g, Point head, SnakeModel.Direction dir) {
        int hx = head.x * CELL_SIZE;
        int hy = head.y * CELL_SIZE + HUD_HEIGHT;
        g.setColor(Color.WHITE);

        int e1x, e1y, e2x, e2y;
        switch (dir) {
            case RIGHT:
                e1x = hx + 13;
                e1y = hy + 4;
                e2x = hx + 13;
                e2y = hy + 12;
                break;
            case LEFT:
                e1x = hx + 3;
                e1y = hy + 4;
                e2x = hx + 3;
                e2y = hy + 12;
                break;
            case UP:
                e1x = hx + 4;
                e1y = hy + 3;
                e2x = hx + 12;
                e2y = hy + 3;
                break;
            case DOWN:
                e1x = hx + 4;
                e1y = hy + 13;
                e2x = hx + 12;
                e2y = hy + 13;
                break;
            default:
                return;
        }
        g.fillOval(e1x, e1y, 4, 4);
        g.fillOval(e2x, e2y, 4, 4);
    }

    /**
     * Draws the food item as a red circle with a small shine highlight.
     */
    private void drawFood(Graphics g) {
        if (foodModel == null || foodModel.getPosition() == null)
            return;
        Point p = foodModel.getPosition();
        int px = p.x * CELL_SIZE;
        int py = p.y * CELL_SIZE + HUD_HEIGHT;

        g.setColor(Color.RED);
        g.fillOval(px + 2, py + 2, CELL_SIZE - 4, CELL_SIZE - 4);

        // Shine highlight
        g.setColor(new Color(255, 170, 170));
        g.fillOval(px + 5, py + 4, 5, 5);
    }

    /**
     * Draws all obstacles as 3-D stone blocks:
     * - Grey fill
     * - Light top/left edge (highlight)
     * - Dark bottom/right edge (shadow)
     * - Thin crack lines for a rock texture
     */
    private void drawObstacles(Graphics g) {
        if (obstacleModel == null)
            return;
        for (Point p : obstacleModel.getObstacles()) {
            int px = p.x * CELL_SIZE;
            int py = p.y * CELL_SIZE + HUD_HEIGHT;

            // ── Stone fill ──
            g.setColor(new Color(105, 105, 115));
            g.fillRect(px + 1, py + 1, CELL_SIZE - 2, CELL_SIZE - 2);

            // ── 3-D highlight: top and left edges ──
            g.setColor(new Color(180, 180, 195));
            g.drawLine(px + 1, py + 1, px + CELL_SIZE - 2, py + 1); // top
            g.drawLine(px + 1, py + 1, px + 1, py + CELL_SIZE - 2); // left

            // ── 3-D shadow: bottom and right edges ──
            g.setColor(new Color(45, 45, 50));
            g.drawLine(px + 1, py + CELL_SIZE - 2,
                    px + CELL_SIZE - 2, py + CELL_SIZE - 2); // bottom
            g.drawLine(px + CELL_SIZE - 2, py + 1,
                    px + CELL_SIZE - 2, py + CELL_SIZE - 2); // right

            // ── Crack texture ──
            g.setColor(new Color(65, 65, 72));
            g.drawLine(px + 5, py + 3, px + 8, py + 10);
            g.drawLine(px + 11, py + 5, px + 14, py + 14);
            g.drawLine(px + 4, py + 14, px + 9, py + 17);
        }
    }

    // ---- Overlay screens ----

    /** Semi-transparent start-screen overlay shown before first key press. */
    private void drawStartScreen(Graphics g) {
        dimScreen(g, 175);

        g.setColor(new Color(0, 230, 0));
        g.setFont(new Font("Arial", Font.BOLD, 52));
        drawCentered(g, "SNAKE", PANEL_HEIGHT / 2 - 45);

        g.setColor(Color.LIGHT_GRAY);
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        drawCentered(g, "Press any arrow key to start", PANEL_HEIGHT / 2 + 5);
        drawCentered(g, "P = Pause  |  R = Restart", PANEL_HEIGHT / 2 + 30);
    }

    /** Pause overlay. */
    private void drawPauseScreen(Graphics g) {
        dimScreen(g, 140);

        g.setColor(Color.YELLOW);
        g.setFont(new Font("Arial", Font.BOLD, 38));
        drawCentered(g, "PAUSED", PANEL_HEIGHT / 2 - 10);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 15));
        drawCentered(g, "Press P to resume", PANEL_HEIGHT / 2 + 30);
    }

    /** Game-over overlay with score summary. */
    private void drawGameOverScreen(Graphics g) {
        dimScreen(g, 165);

        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 42));
        drawCentered(g, "GAME OVER", PANEL_HEIGHT / 2 - 35);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        drawCentered(g, "Score: " + gameState.getScore(), PANEL_HEIGHT / 2 + 5);

        // Highlight a new high-score record
        if (gameState.getScore() > 0 && gameState.getScore() >= gameState.getHighScore()) {
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Arial", Font.BOLD, 15));
            drawCentered(g, "\u2605  New High Score!  \u2605", PANEL_HEIGHT / 2 + 35);
        }
    }

    // ---- Utility methods ----

    /** Fills the whole panel with a translucent black rectangle. */
    private void dimScreen(Graphics g, int alpha) {
        g.setColor(new Color(0, 0, 0, alpha));
        g.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);
    }

    /** Draws a string horizontally centered in the panel at the given y. */
    private void drawCentered(Graphics g, String text, int y) {
        FontMetrics fm = g.getFontMetrics();
        int x = (PANEL_WIDTH - fm.stringWidth(text)) / 2;
        g.drawString(text, x, y);
    }

    /**
     * Returns the display color for the given level, cycling through the palette.
     */
    private Color getLevelColor(int level) {
        int idx = (level - 1) % LEVEL_COLORS.length;
        return LEVEL_COLORS[idx];
    }

    /**
     * Returns a version of {@code color} darkened by {@code factor} (0.0 = black,
     * 1.0 = original).
     */
    private Color darken(Color color, float factor) {
        int r = Math.max(0, Math.min(255, (int) (color.getRed() * factor)));
        int g = Math.max(0, Math.min(255, (int) (color.getGreen() * factor)));
        int b = Math.max(0, Math.min(255, (int) (color.getBlue() * factor)));
        return new Color(r, g, b);
    }
}
