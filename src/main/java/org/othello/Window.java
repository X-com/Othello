package org.othello;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class Window extends JFrame {

    private ArrayList<Canvas> canvasGrid = new ArrayList<>();
    private int[][] grid = new int[8][8];
    private ClickAction clickAction;
    private GameAction gameAction;
    private JLabel infoLabel;

    public Window(ClickAction click, GameAction game) {
        grid[3][3] = 1;
        grid[4][4] = 1;
        grid[4][3] = 2;
        grid[3][4] = 2;
        clickAction = click;
        gameAction = game;
    }

    public void make() {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Othello");
            frame.setLayout(new BorderLayout());

            JPanel mainPanel = new JPanel(new GridLayout(8, 8));
            mainPanel.setSize(new Dimension(500, 500));

            for (int i = 0; i < 64; i++) {
                Canvas canvas = new Canvas() {
                    @Override
                    public void paint(Graphics g) {
                        render(this, (Graphics2D) g);
                    }
                };
                canvas.setBackground(new Color(0, 128, 0));
                canvas.setSize(50, 50);

                int index = i;
                canvas.addMouseListener(new MouseAdapter() {
                    public void mousePressed(MouseEvent me) {
                        SwingUtilities.invokeLater(() -> {
                            if (me.getButton() == 1) {
//                                int x = (int) ((me.getX() / (float) canvas.getWidth()) * 8);
//                                int y = (int) ((me.getY() / (float) canvas.getHeight()) * 8);
                                mouseClickedAt(index % 8, index / 8);
                            }
                        });
                    }
                });

                canvasGrid.add(canvas);
                JPanel p = new JPanel(new BorderLayout());
                p.setBorder(new CompoundBorder(new EmptyBorder(1, 1, 1, 1), new LineBorder(Color.BLACK)));
                p.add(canvas);
                mainPanel.add(p);
            }

            JMenuBar jMenuBar = new JMenuBar();
            JMenu file = new JMenu("File");
            jMenuBar.add(file);

            JMenuItem player = new JMenuItem("Player vs player");
            player.addActionListener(this::vsPlayer);
            JMenuItem ai = new JMenuItem("Player vs AI");
            ai.addActionListener(this::vsAi);

            file.add(player);
            file.add(ai);

            infoLabel = new JLabel("Select game mode in File menu.");

            frame.add(infoLabel, BorderLayout.SOUTH);

            frame.setJMenuBar(jMenuBar);
            frame.add(mainPanel);
            frame.pack();
            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            frame.setResizable(false);
            frame.setLocation(dim.width / 2 - frame.getSize().width / 2, dim.height / 2 - frame.getSize().height / 2);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }

    private void vsPlayer(ActionEvent actionEvent) {
        gameAction.gameState(1);
    }

    private void vsAi(ActionEvent actionEvent) {
        gameAction.gameState(2);
    }

    private void render(Canvas canvas, Graphics2D g) {
        int index = canvasGrid.indexOf(canvas);
        int x = index % 8;
        int y = index / 8;
        int color = grid[x][y];
        if (color == 0) {
            g.setColor(new Color(0, 128, 0));
        }
        if (color == 1) {
            g.setColor(Color.BLACK);
        }
        if (color == 2) {
            g.setColor(Color.WHITE);
        }
        g.fillOval(2, 2, 46, 46);
        g.dispose();
    }

    private void mouseClickedAt(int x, int y) {
        clickAction.click(x, y);
    }

    public void colorGrid(int x, int y, int player) {
        grid[x][y] = player;
        SwingUtilities.invokeLater(() -> canvasGrid.get(y * 8 + x).repaint());
    }
}
