package org.othello;

import java.awt.Point;

public class MiniMaxResult {
    private int x;
    private int y;
    private int value;
    
    public MiniMaxResult(int x, int y, int value){
        this.x = x;
        this.y = y;
        this.value = value;
    }

    public Point getMove(){
        Point p = new Point(x, y);
        return p;
    }

    public int getValue(){
        return value;
    }
    public MiniMaxResult getCopy(){
        return new MiniMaxResult(this.x, this.y, this.value);
    }
}
