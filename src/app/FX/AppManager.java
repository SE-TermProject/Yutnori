package app.FX;

import controller.FX.YutController;
import view.Swing.GameSetupView;
import view.Swing.YutBoard;

public class AppManager {
    public void start() {
        new GameSetupView(this);
    }

    public void startGame(int sides, int playerCount, int pieceCount) {
        YutBoard board = new YutBoard();
        YutController yutController = new YutController(this, sides, playerCount, pieceCount, board);
        yutController.initializeGameUI();
    }

    public void restartGame() {
        start();
    }

    public void exitGame() {
        System.exit(0);
    }
}
