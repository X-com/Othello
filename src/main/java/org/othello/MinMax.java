package org.othello;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class MinMax {

    private static int dir[] = {-1, -1, 0, 1, 1, 0, -1, 1, -1};
    private int player;
    private int[][] grid;
    private ArrayList<Node> nodes;
    private boolean printLog = false;

    public MinMax(int player, int[][] grid) {
        this.player = player;
        this.grid = new int[8][8];
        this.nodes = new ArrayList<>();
        for (int i = 0; i < grid.length; i++)
            this.grid[i] = grid[i].clone();
    }

    public static Point2D findMove(int ai, int[][] grid) {
        MinMax m = new MinMax(ai, grid);
        return m.getMax();
    }

    private Point2D getMax() {
        printBoard(0);
        findNodes();
        int score = getScore();
        System.out.println(score);

        int scoreMax = Integer.MIN_VALUE;
        ArrayList<Node> valid = new ArrayList<>();
        for (Node n : nodes) {
            if (n.score > scoreMax) {
                scoreMax = n.score;
                valid.clear();
                valid.add(n);
                System.out.println("mining " + scoreMax);
            } else if (n.score == scoreMax) {
                valid.add(n);
            }
        }
        if (valid.isEmpty()) {
            return null;
        }
        Node n = valid.get(0);

        printBoard(player);

        return n.point;
    }

    private int getScore() {
        int score = 0;
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                if (grid[x][y] == 1) {
                    score++;
                } else if (grid[x][y] == 2) {
                    score--;
                }
            }
        }
        return score;
    }

    private void printBoard(int player) {
        if (!printLog) return;
        System.out.println("player: " + (this.player == player ? "AI" : "Player"));
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                System.out.print(grid[x][y] + " ");
            }
            System.out.println();
        }
    }

    private void findNodes() {
        int find = player == 1 ? 2 : 1;
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                if (grid[x][y] != 0) continue;
                ArrayList<Point2D> points = new ArrayList<>();

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

                                points.add(new Point(dx, dy));
                            }
                            break;
                        }
                    }
                }

                if (!points.isEmpty()) {
                    Node n = new Node();
                    n.score = points.size();
                    n.locations = points;
                    n.point = new Point(x, y);
                    nodes.add(n);
                }
            }
        }
    }
}
