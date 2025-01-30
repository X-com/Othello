package org.othello;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
        } else if (gameMode > 1) {
            vsAi(x, y);
        }
    }

    @Override
    public void gameStart(int type) {
        for (int i = 0; i < 64; i++) {
            window.colorGrid(i % 8, i / 8, 0);
            grid[i % 8][i / 8] = 0;
        }

//        testLoadFromFile();

        setGridToPlayerColor(3, 3, 2);
        setGridToPlayerColor(4, 4, 2);
        setGridToPlayerColor(4, 3, 1);
        setGridToPlayerColor(3, 4, 1);

        if (type == 1) {
            window.setInfo("Black Turn");
            player = 1;
        }
        if (type == 2) {
            window.setInfo("Playing white");
            player = 2;
            aiSelects();
        }
        if (type == 3) {
            window.setInfo("Playing black");
            player = 1;
        }
        gameMode = type;
        calculateSelection();
    }

    private void testLoadFromFile() {
        try {
            File f = new File("D:\\Programmering\\GitKraken\\Othello\\src\\main\\resources\\test3.txt");
            FileInputStream fs = new FileInputStream(f);
            String result = new java.io.BufferedReader(new java.io.InputStreamReader(fs)).lines().collect(java.util.stream.Collectors.joining("\n"));

            int y = 0;
            for (String line : result.split("\n")) {
                int x = 0;
                for (String s : line.split(" ")) {
                    setGridToPlayerColor(x, y, Integer.parseInt(s));
                    x++;
                }
                y++;
            }
        } catch (FileNotFoundException e) {
        }
    }

    private void pvp(int x, int y) {
        if (playerMove(x, y, player)) {
            player = player == 1 ? 2 : 1;
            calculateSelection();

            if (valid.size() == 0) {
                findWinner();
            } else {
                window.setInfo((player == 1 ? "Black" : "White") + " Turn");
            }
        }
    }

    private void vsAi(int x, int y) {
        if (playerMove(x, y, player)) {
            calculateSelection();

            if (valid.size() == 0) {
                findWinner();
            } else {
                aiSelects();
                calculateSelection();
            }

            if (valid.size() == 0) {
                findWinner();
            }
        }
    }

    private void aiSelects() {
        int ai = player == 1 ? 2 : 1;
        Point2D p = MinMax.findMove(ai, grid);
        if (p == null) {
            gameMode = 0;
            findWinner();
            return;
        }
        int x = (int) p.getX();
        int y = (int) p.getY();
        setGridToPlayerColor(x, y, ai);
        updateBoard(x, y, ai);
    }

    private void calculateSelection() {
        for (Point2D p : valid) {
            int x = (int) p.getX();
            int y = (int) p.getY();
            if (grid[x][y] == 0) {
                window.colorGrid(x, y, 0);
            }
        }
        valid.clear();
        if (gameMode == 0) return;
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

    private boolean playerMove(int x, int y, int player) {
        if (!validSelection(x, y, player)) return false;
        setGridToPlayerColor(x, y, player);
        updateBoard(x, y, player);
        return true;
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
        gameMode = 0;
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
                if (grid[dx][dy] == 0) break;
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
                if (grid[dx][dy] == 0) break;
                if (grid[dx][dy] == player) return true;
            }
        }

        return false;
    }
}
