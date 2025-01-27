package org.othello;

public class Othello implements ClickAction, GameAction {

    Window window = new Window(this, this);

    public static void main(String[] args) {
        new Othello().makeWindow();
    }

    private void makeWindow() {
        window.make();
    }

    @Override
    public void click(int x, int y) {
        window.colorGrid(x, y, 1);
    }

    @Override
    public void gameState(int type) {
        for (int i = 0; i < 64; i++) {
            window.colorGrid(i % 8, i / 8, 0);
        }
        window.colorGrid(3, 3, 1);
        window.colorGrid(4, 4, 1);
        window.colorGrid(4, 3, 2);
        window.colorGrid(3, 4, 2);

        if (type == 1) {
            System.out.println("vs player");
        }
        if (type == 2) {
            System.out.println("vs ai");
        }
    }
}
