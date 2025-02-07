package org.othello;

import java.awt.Point;

/**
 * Simple method to store state of the MinMax values
 */
public class MiniMaxResult {
    private final int x;
    private final int y;
    private final int value;

    public MiniMaxResult(int x, int y, int value) {
        this.x = x;
        this.y = y;
        this.value = value;
    }

    public Point getMove() {
        return new Point(x, y);
    }

    public int getValue() {
        return value;
    }
}
