package org.othello;

import java.awt.Point;
import java.util.ArrayList;

public class Othello implements ClickAction, GameAction {
    private static final long TURN_TIME_MILLISECONDS = 250L;

    public static final int BOARD_SIZE = 8;
    public static final int[][] DIRECTIONS = {{1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}, {1, -1}};
    private static final int BLACK = 1, EMPTY = 0, WHITE = 2;

    private final Window window = new Window(this, this);
    private int gameMode = 0; // 0: Game not started,  1: Player vs player, 2: White vs AI, 3: Black vs AI
    private int currentPlayer = 1;  // Current human player 1: Black, 2: White
    private ArrayList<Point> validMoves = new ArrayList<>(); // List of valid moves for currentPlayer
    private final int[][] grid = new int[BOARD_SIZE][BOARD_SIZE];  // Board
    private long startTime = 0;

    public static void main(String[] args) {
        new Othello().makeWindow();
    }

    private void makeWindow() {
        window.make();
    }

    // Window Click Action
    @Override
    public void click(int x, int y) {
        if (gameMode == 1) {
            pvpClick(x, y);
        } else if (gameMode > 1) {
            vsAiClick(x, y);
        }
    }

    // Game Start Action: Initializes board and calculates possible moves for first player
    @Override
    public void gameStart(int type) { // (type: 1: Player vs player, 2: White vs AI, 3: Black vs AI)
        initializeBoard(type);
        if (type == 1) {
            window.setInfo("Black Turn");
            currentPlayer = BLACK;
        }
        if (type == 2) {
            window.setInfo("Playing White Against AI");
            currentPlayer = WHITE;
            aiMakesMoveMinMaxIDDFS(BLACK);
        }
        if (type == 3) {
            window.setInfo("Playing Black Against AI");
            currentPlayer = BLACK;
        }
        if (type == 4) {
            simulationAiMinMaxVsAiRandom();
            return;
        }
        eraseValidMoves(validMoves);
        validMoves = calculateCurrentValidMoves(grid, currentPlayer);
        paintValidMoves(validMoves, currentPlayer);
    }

    private void initializeBoard(int type) {
        for (int i = 0; i < BOARD_SIZE * BOARD_SIZE; i++) {
            window.colorGrid(i % 8, i / 8, EMPTY);
            grid[i % 8][i / 8] = EMPTY;
        }
        setGridToPlayerColor(3, 3, WHITE);
        setGridToPlayerColor(4, 4, WHITE);
        setGridToPlayerColor(4, 3, BLACK);
        setGridToPlayerColor(3, 4, BLACK);

        gameMode = type;
    }

    // Action triggered by a click on pvp mode
    private void pvpClick(int x, int y) {
        if (executePlayerMove(x, y, currentPlayer)) { // If the player move has been properly executed
            eraseValidMoves(validMoves);
            playerPlays();
            paintValidMoves(validMoves, currentPlayer);
        }
    }

    private void playerPlays() {
        int movesLeft = calculateCurrentValidMoves(grid, currentPlayer).size();
        currentPlayer = currentPlayer == WHITE ? BLACK : WHITE;
        window.setInfo((currentPlayer == BLACK ? "Black" : "White") + " Turn");
        validMoves = calculateCurrentValidMoves(grid, currentPlayer);
        if (validMoves.size() == 0 && movesLeft > 0) {
            playerPlays();
        } else if (validMoves.size() == 0) {
            getWinner();
        }
    }

    // Action triggered by a click against AI
    private void vsAiClick(int x, int y) {
        if (executePlayerMove(x, y, currentPlayer)) {
            eraseValidMoves(validMoves);
            aiPlays();
            paintValidMoves(validMoves, currentPlayer);
        }
    }

    private void aiPlays() {
        boolean aiMoved = aiMakesMoveMinMaxIDDFS(currentPlayer == WHITE ? BLACK : WHITE);
        validMoves = calculateCurrentValidMoves(grid, currentPlayer);
        boolean playerCanMove = validMoves.size() != 0;
        if (aiMoved && !playerCanMove) {
            System.out.println("ai moves again");
            aiPlays();
        } else if (!aiMoved && !playerCanMove) {
            getWinner();
        }
    }

    private boolean aiMakesMoveMinMaxIDDFS(int IAPlayer) {
        GameState game = new GameState(grid, IAPlayer, IAPlayer);
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        Point move = MiniMaxIDDFS(game, alpha, beta); // Searching move 
        if (move.x >= 0 && move.y >= 0) {
            executePlayerMove(move.x, move.y, IAPlayer);
            return true;
        } else {
            return false;
        }
    }

    public Point MiniMaxIDDFS(GameState game, int alpha, int beta) {
        startTime = System.currentTimeMillis();
        int depth = 1;
        MiniMaxResult bestResult = new MiniMaxResult(-1, -1, Integer.MIN_VALUE);
        while (true) {
            MiniMaxResult result = MaxValue(game.getCopy(), alpha, beta, depth);
            if (searchTimeExpired()) {
                break;
            }
            if (result.getValue() > bestResult.getValue()) {
                bestResult = result;
            }
            depth++;
        }
        return bestResult.getMove();
    }

    public MiniMaxResult MaxValue(GameState game, int alpha, int beta, int depth) {
        if (depth == 0 || searchTimeExpired()) {
            return new MiniMaxResult(-1, -1, game.getGameScore());
        }
        ArrayList<Point> moves = calculateCurrentValidMoves(game.getGrid(), game.getPlayer());
        if (moves.isEmpty()) {
            return new MiniMaxResult(-1, -1, game.getGameScore());
        }
        Point bestMove = new Point(-1, -1);
        int bestValue = Integer.MIN_VALUE;
        for (Point move : moves) {
            GameState g = game.getCopy();
            g.executeMove(move.x, move.y);
            g.changeTurn();

            MiniMaxResult result = MinValue(g, alpha, beta, depth - 1);

            if (bestValue < result.getValue()) {
                bestValue = result.getValue();
                bestMove = move;
                alpha = Math.max(alpha, result.getValue());
            }
            if (bestValue >= beta) {
                break;
            }
        }
        return new MiniMaxResult(bestMove.x, bestMove.y, bestValue);
    }

    public MiniMaxResult MinValue(GameState game, int alpha, int beta, int depth) {
        if (depth == 0 || searchTimeExpired()) {
            return new MiniMaxResult(-1, -1, game.getGameScore());
        }
        ArrayList<Point> moves = calculateCurrentValidMoves(game.getGrid(), game.getPlayer());
        if (moves.isEmpty()) {
            return new MiniMaxResult(-1, -1, game.getGameScore());
        }
        Point bestMove = new Point(-1, -1);
        int bestValue = Integer.MAX_VALUE;
        for (Point move : moves) {
            GameState g = game.getCopy();
            g.executeMove(move.x, move.y);
            g.changeTurn();
            MiniMaxResult result = MaxValue(g, alpha, beta, depth - 1);

            if (bestValue > result.getValue()) {
                bestValue = result.getValue();
                bestMove = move;
                beta = Math.min(beta, result.getValue());
            }
            if (bestValue <= alpha) {
                break;
            }
        }
        return new MiniMaxResult(bestMove.x, bestMove.y, bestValue);
    }

    private boolean searchTimeExpired() {
        return System.currentTimeMillis() - startTime >= TURN_TIME_MILLISECONDS;
    }

    /*
     * Calculates all possible valid movements for the current player
     */
    public ArrayList<Point> calculateCurrentValidMoves(int[][] grid, int player) {
        ArrayList<Point> validMovesCalculated = new ArrayList<>();
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (validMove(grid, i, j, player)) {
                    validMovesCalculated.add(new Point(i, j));
                }
            }
        }
        return validMovesCalculated;
    }

    // Paints valid moves on board
    public void paintValidMoves(ArrayList<Point> validMoves, int player) {
        for (Point p : validMoves) {
            window.colorGrid((int) p.getX(), (int) p.getY(), player + 2);
        }
    }

    // Erases valid moves on board
    public void eraseValidMoves(ArrayList<Point> validMoves) {
        for (Point p : validMoves) {
            int x = p.x;
            int y = p.y;
            if (grid[x][y] == EMPTY) {
                window.colorGrid(x, y, EMPTY);
            }
        }
        validMoves.clear();
    }

    // Updates grid and colours board on (x,y)
    private void setGridToPlayerColor(int x, int y, int player) {
        grid[x][y] = player;
        window.colorGrid(x, y, player);
    }

    // Tries to execute a player move. If possible paints board and updates grid
    private boolean executePlayerMove(int x, int y, int player) {
        if (!validMove(grid, x, y, player)) {
            return false;
        }
        setGridToPlayerColor(x, y, player);
        updateBoard(x, y, player);
        return true;
    }

    /*
     * Counts disks on board, stops game and announces winner
     */
    private int getWinner() {
        int blackCounter = 0;
        int whiteCounter = 0;
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (grid[i][j] == BLACK) {
                    blackCounter++;
                } else if (grid[i][j] == WHITE) {
                    whiteCounter++;
                }
            }
        }
        gameMode = 0;
        if (blackCounter == whiteCounter) {
            window.setInfo("Draw      Black: " + blackCounter + "   White: " + whiteCounter);
            System.out.println("Draw      Black: " + blackCounter + "   White: " + whiteCounter);
            return EMPTY;
        } else if (blackCounter > whiteCounter) {
            System.out.println("Black wins      Black: " + blackCounter + "   White: " + whiteCounter);
            window.setInfo("Black wins      Black: " + blackCounter + "   White: " + whiteCounter);
            return BLACK;
        } else {
            window.setInfo("White wins      Black: " + blackCounter + "   White: " + whiteCounter);
            System.out.println("White wins      Black: " + blackCounter + "   White: " + whiteCounter);
            return WHITE;
        }
    }

    /*
     * Updates board after a disk has been placed in postion (x,y)
     */
    private void updateBoard(int x, int y, int player) {
        for (int[] dir : DIRECTIONS) {
            recursiveWalk(x + dir[0], y + dir[1], dir, player);
        }
    }

    /*
     * Recusively explores a path to flip the disks according to a player move
     */
    private boolean recursiveWalk(int x, int y, int[] dir, int player) {
        if (!withinBoard(x, y) || grid[x][y] == EMPTY) {
            return false;
        }
        if (grid[x][y] == player) {
            return true;
        }

        if (recursiveWalk(x + dir[0], y + dir[1], dir, player)) {
            setGridToPlayerColor(x, y, player);
            return true;
        }
        return false;
    }

    // Calculates if a cell in the grid is a valid move for a player
    private boolean validMove(int[][] grid, int x, int y, int player) {
        /*
         * A move for player A is valid if:
         * 1. A path can be formed that changes one or more disks of player B
         * 2. The path must contain only the other player B's disks and stops when it reaches the first player A's disk
         */
        if (grid[x][y] != EMPTY) {
            return false;
        }
        int opponent = (player == BLACK) ? WHITE : BLACK;

        for (int[] dir : DIRECTIONS) {
            int dx = x + dir[0], dy = y + dir[1];

            if (!withinBoard(dx, dy) || grid[dx][dy] != opponent) {
                continue;
            }

            while (withinBoard(dx, dy) && grid[dx][dy] == opponent) {
                dx += dir[0];
                dy += dir[1];
            }
            if (withinBoard(dx, dy) && grid[dx][dy] == player) {
                return true;
            }
        }
        return false;
    }

    public boolean withinBoard(int x, int y) {
        return x < BOARD_SIZE && x >= 0 && y < BOARD_SIZE && y >= 0;
    }

    private void simulationAiMinMaxVsAiRandom() {
        boolean a, b;
        boolean passFlagA, passFlagB;
        while (gameMode != 0) {
            a = aiMakesMoveRandom(WHITE);
            if (a == false) passFlagA = true;
            else passFlagA = false;

            b = aiMakesMoveMinMaxIDDFS(BLACK);
            if (b == false) passFlagB = true;
            else passFlagB = false;

            if (passFlagA && passFlagB) { // If no player was able to make a move
                getWinner();
            }
        }
    }

    private boolean aiMakesMoveRandom(int IAPlayer) {
        eraseValidMoves(validMoves);
        validMoves = calculateCurrentValidMoves(grid, IAPlayer);
        if (validMoves.size() == 0) // No legal move is possible
            return false;
        // Choose randomly
        int randomIndex = (int) (Math.random() * validMoves.size());
        Point randomMove = validMoves.get(randomIndex);
        int x = randomMove.x, y = randomMove.y;

        executePlayerMove(x, y, IAPlayer);
        char xChar = (char) (x + 'A');
        y++;
        window.setInfo("Last IA move: " + y + xChar);
        return true;
    }
}
