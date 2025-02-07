package org.othello;

/**
 * Class storing the game state when preforming min-max simulations.
 */
public class GameState {
    private int currentPlayer;
    private final int[][] grid;
    private final int maximizer;

    public static final int BOARD_SIZE = 8;
    public static final int[][] DIRECTIONS = {{1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}, {1, -1}};
    private static final int BLACK = 1, EMPTY = 0, WHITE = 2;

    public GameState(int[][] grid, int currentPlayer, int maximizer) {
        this.grid = new int[grid.length][grid[0].length];
        for (int i = 0; i < grid.length; i++) {
            this.grid[i] = grid[i].clone();
        }
        this.currentPlayer = currentPlayer;
        this.maximizer = maximizer;
    }

    /**
     * Copy of the current game state.
     */
    public GameState getCopy() {
        return new GameState(grid, currentPlayer, maximizer);
    }

    /**
     * Which player is playing in the current game state.
     */
    public int getPlayer() {
        return currentPlayer;
    }

    /**
     * Game board of the current game state
     */
    public int[][] getGrid() {
        int[][] copy = new int[grid.length][grid[0].length];
        for (int i = 0; i < grid.length; i++) {
            copy[i] = grid[i].clone();
        }
        return copy;
    }

    /**
     * Value of specified sell in this game state
     */
    public int getCell(int x, int y) {
        return grid[x][y];
    }

    /**
     * Changes the current game states player turn.
     */
    public void changeTurn() {
        currentPlayer = currentPlayer == WHITE ? BLACK : WHITE;
    }

    /**
     * Used to keep track if this game state is used to maximize or minimize the score when
     * preforming min-max search.
     */
    public int getMaximizer() {
        return maximizer;
    }

    /**
     * Simulates playing a move in the specified cell and updates this instanced game state.
     */
    public void executeMove(int x, int y) {
        grid[x][y] = currentPlayer;
        updateBoard(x, y);
    }

    /**
     * Changes the board based on simulated move on the current instanced game state in
     * all 8 directions around the specified cell.
     */
    private void updateBoard(int x, int y) {
        for (int[] dir : DIRECTIONS) {
            recursiveWalk(x + dir[0], y + dir[1], dir);
        }
    }

    /**
     * Recursively updates a line on the board changing the color of markers
     * until it can not find more tiles that needs their color changed.
     */
    private boolean recursiveWalk(int x, int y, int[] dir) {
        if (!withinBoard(x, y) || grid[x][y] == EMPTY) return false;
        if (grid[x][y] == currentPlayer) return true;
        if (recursiveWalk(x + dir[0], y + dir[1], dir)) {
            grid[x][y] = currentPlayer;
            return true;
        }
        return false;
    }

    /**
     * Checks if the position is within the board.
     */
    public boolean withinBoard(int x, int y) {
        return x < BOARD_SIZE && x >= 0 && y < BOARD_SIZE && y >= 0;
    }

    /**
     * Gets the game score of the currently instanced player relative to its opponent.
     */
    public int getGameScore() {
        int blackCounter = 0, whiteCounter = 0;
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (getCell(i, j) == BLACK) {
                    blackCounter++;
                } else if (getCell(i, j) == WHITE) {
                    whiteCounter++;
                }
            }
        }
        return getMaximizer() == WHITE ? whiteCounter - blackCounter : blackCounter - whiteCounter;
    }
}
