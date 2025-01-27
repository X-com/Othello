package org.othello;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Othello implements ClickAction, GameAction {

    private Window window = new Window(this, this);
    private int gameMode = 0;
    private int player = 1;
    private int[][] grid = new int[8][8];
    private int dir[] = {-1, -1, 0, 1, 1, 0, -1, 1, -1};
    private ArrayList<Point2D> valid = new ArrayList<>();

    public static void main(String[] args) {
        new Othello().makeWindow();
    }

    private void makeWindow() {
        window.make();
    }

    @Override
    public void click(int x, int y) {
        if (gameMode == 1) {
            pvp(x, y);
        }
    }

    @Override
    public void gameStart(int type) {
        for (int i = 0; i < 64; i++) {
            window.colorGrid(i % 8, i / 8, 0);
            grid[i % 8][i / 8] = 0;
        }
        setGridToPlayerColor(3, 3, 2);
        setGridToPlayerColor(4, 4, 2);
        setGridToPlayerColor(4, 3, 1);
        setGridToPlayerColor(3, 4, 1);

        if (type == 1) {
            window.setInfo("Black Turn");
        }
        if (type == 2) {
            window.setInfo("Playing white");
        }
        if (type == 3) {
            window.setInfo("Playing black");
        }
        showSelection();

        gameMode = type;
    }

    private void showSelection() {
        for (Point2D p : valid) {
            int x = (int) p.getX();
            int y = (int) p.getY();
            if (grid[x][y] == 0) {
                window.colorGrid(x, y, 0);
            }
        }
        valid.clear();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (validSelection(i, j, player)) {
                    window.colorGrid(i, j, player + 2);
                    valid.add(new Point(i, j));
                }
            }
        }
    }

    private void setGridToPlayerColor(int x, int y, int player) {
        grid[x][y] = player;
        window.colorGrid(x, y, player);
    }

    private void pvp(int x, int y) {
        if (player == 1) {
            if (validSelection(x, y, player)) {
                setGridToPlayerColor(x, y, player);
                updateBoard(x, y, player);
                player = 2;
            }
        } else if (player == 2) {
            if (validSelection(x, y, player)) {
                setGridToPlayerColor(x, y, player);
                updateBoard(x, y, player);
                player = 1;
            }
        }
        showSelection();

        if (valid.size() == 0) {
            findWinner();
        } else {
            window.setInfo((player == 1 ? "Black" : "White") + " Turn");
        }
    }

    private void findWinner() {
        int black = 0;
        int white = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (grid[i][j] == 1) {
                    black++;
                } else if (grid[i][j] == 2) {
                    white++;
                }
            }
        }
        if (black == white) {
            window.setInfo("Draw      Black: " + black + "   White: " + white);
        } else if (black > white) {
            window.setInfo("Black wins      Black: " + black + "   White: " + white);
        } else {
            window.setInfo("White wins      Black: " + black + "   White: " + white);
        }
    }

    private void updateBoard(int x, int y, int player) {
        int find = player == 1 ? 2 : 1;
        for (int k = 0; k < 8; k++) {
            for (int l = 1; l < 8; l++) {
                int dx = x + dir[k] * l;
                int dy = y + dir[k + 1] * l;
                if (dx < 0 || dx > 7) break;
                if (dy < 0 || dy > 7) break;
                if (l == 1 && grid[dx][dy] != find) break;
                if (grid[dx][dy] == player) {
                    for (int m = l - 1; m >= 1; m--) {
                        dx = x + dir[k] * m;
                        dy = y + dir[k + 1] * m;
                        setGridToPlayerColor(dx, dy, player);
                    }
                    break;
                }
            }
        }
    }

    private boolean validSelection(int x, int y, int player) {
        if (grid[x][y] != 0) return false;
        int find = player == 1 ? 2 : 1;
        for (int k = 0; k < 8; k++) {
            for (int l = 1; l < 8; l++) {
                int dx = x + dir[k] * l;
                int dy = y + dir[k + 1] * l;
                if (dx < 0 || dx > 7) break;
                if (dy < 0 || dy > 7) break;
                if (l == 1 && grid[dx][dy] != find) break;
                if (grid[dx][dy] == player) return true;
            }
        }

        return false;
    }
}
