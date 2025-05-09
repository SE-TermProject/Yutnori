package app;

import controller.YutController;
import view.GameSetupView;
import view.YutBoardV2;

public class AppManager {
    public void start() {
        new GameSetupView(this);
    }

    public void startGame(int sides, int playerCount, int pieceCount) {
        YutBoardV2 board = new YutBoardV2();
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
