package app.fx;

import controller.fx.YutController;
import javafx.stage.Stage;
import view.fx.GameSetupView;
import view.fx.YutBoard;

public class AppManager {
    private Stage primaryStage;

    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        new GameSetupView(primaryStage, this);
    }

    private void startGame(int sides, int playerCount, int pieceCount) {
        YutBoard board = new YutBoard();
        YutController yutController = new YutController(this, sides, playerCount, pieceCount, board);
        yutController.initializeGameUI();
    }

    public void restartGame() {
        start(this.primaryStage);
    }

    public void exitGame() {
        System.exit(0);
    }
}
