package app.fx;

import controller.fx.YutController;
import javafx.scene.Scene;
import javafx.stage.Stage;
import view.fx.GameSetupView;
import view.fx.YutBoard;

public class AppManager {
    private Stage primaryStage;

    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        new GameSetupView(primaryStage, this);
    }

    public void startGame(int sides, int playerCount, int pieceCount) {
        YutBoard board = new YutBoard();
        YutController yutController = new YutController(this, sides, playerCount, pieceCount, board);
        yutController.initializeGameUI();

        Scene scene = new Scene(board, 1100, 700);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Yutnori");
        primaryStage.show();
    }

    public void restartGame() {
        start(this.primaryStage);
    }

    public void exitGame() {
        System.exit(0);
    }
}
