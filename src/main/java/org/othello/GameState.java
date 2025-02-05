package org.othello;

public class GameState {
    private int currentPlayer;
    private int[][] grid;
    private boolean isOver;
    private int maximizer;

    public static final int BOARD_SIZE = 8;
    public static final int[][] DIRECTIONS = {{1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}, {1, -1}};
    private static final int BLACK = 1, EMPTY = 0, WHITE = 2;

    public GameState(int[][] grid, int currentPlayer, int maximizer) {
        this.grid = new int[grid.length][grid[0].length];
        for (int i = 0; i < grid.length; i++) {
            this.grid[i] = grid[i].clone();
        }
        this.currentPlayer = currentPlayer;
        this.isOver = false;
        this.maximizer = maximizer;
    }

    public GameState getCopy() {
        GameState g = new GameState(grid, currentPlayer, maximizer);
        return g;
    }

    public int getPlayer() {
        return currentPlayer;
    }

    public int[][] getGrid() {
        int[][] copy = new int[grid.length][grid[0].length];
        for (int i = 0; i < grid.length; i++) {
            copy[i] = grid[i].clone();
        }
        return copy;
    }

    public int getCell(int x, int y) {
        return grid[x][y];
    }

    public void executeMove(int x, int y) {
        grid[x][y] = currentPlayer;
        updateBoard(x, y);
    }

    public void changeTurn() {
        currentPlayer = currentPlayer == WHITE ? BLACK : WHITE;
    }

    public boolean isOver() {
        return isOver;
    }

    public int getMaximizer() {
        return maximizer;
    }

    private void updateBoard(int x, int y) {
        for (int[] dir : DIRECTIONS) {
            recursiveWalk(x + dir[0], y + dir[1], dir);
        }
    }

    private boolean recursiveWalk(int x, int y, int[] dir) {
        if (!withinBoard(x, y) || grid[x][y] == EMPTY) return false;
        if (grid[x][y] == currentPlayer) return true;
        if (recursiveWalk(x + dir[0], y + dir[1], dir)) {
            grid[x][y] = currentPlayer;
            return true;
        }
        return false;
    }

    public boolean withinBoard(int x, int y) {
        return x < BOARD_SIZE && x >= 0 && y < BOARD_SIZE && y >= 0;
    }

    public void printBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                switch (grid[i][j]) {
                    case 2:
                        System.out.print("W ");
                        break;
                    case 1:
                        System.out.print("B ");
                        break;
                    default:
                        System.out.print(grid[i][j] + " ");
                }
            }
            System.out.print("\n");
        }
    }
}
