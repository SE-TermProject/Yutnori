package view.fx;

import app.fx.AppManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class GameSetupView {
    private final Stage primaryStage;
    private final AppManager appManager;

    public GameSetupView(Stage primaryStage, AppManager appManager) {
        this.primaryStage = primaryStage;
        this.appManager = appManager;
        primaryStage.setTitle("게임 설정");
        initializeUI();
    }

    private void initializeUI() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        HBox shapePanel = createShapeSelector();
        HBox playerPanel = createPlayerSelector();
        HBox piecePanel = createPieceSelector();
        Button startButton = createStartButton();

        root.getChildren().addAll(shapePanel, playerPanel, piecePanel, startButton);

        // Scene 설정
        Scene scene = new Scene(root, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.setTitle("게임 설정");
        primaryStage.show();
    }

    private ComboBox<Integer> shapeBox;
    private ComboBox<Integer> playerBox;
    private ComboBox<Integer> pieceBox;

    // 1. 판 모양 선택 (4~6각형)
    private HBox createShapeSelector() {
        shapeBox = new ComboBox<>();
        shapeBox.getItems().addAll(4, 5, 6);
        shapeBox.getSelectionModel().selectFirst();

        HBox shapePanel = new HBox(10, new Label("판의 변 개수: "), shapeBox);
        shapePanel.setAlignment(Pos.CENTER);
        return shapePanel;
    }

    // 2. 플레이어 수 (2~4명)
    private HBox createPlayerSelector() {
        playerBox = new ComboBox<>();
        playerBox.getItems().addAll(2, 3, 4);
        playerBox.getSelectionModel().selectFirst();

        HBox playerPanel = new HBox(10, new Label("플레이어 수:"), playerBox);
        playerPanel.setAlignment(Pos.CENTER);
        return playerPanel;
    }

    // 3. 말 개수 (2~5개)
    private HBox createPieceSelector() {
        pieceBox = new ComboBox<>();
        pieceBox.getItems().addAll(2, 3, 4, 5);
        pieceBox.getSelectionModel().selectFirst();

        HBox piecePanel = new HBox(10, new Label("플레이어당 말 개수:"), pieceBox);
        piecePanel.setAlignment(Pos.CENTER);
        return piecePanel;
    }

    // 4. 시작 버튼
    private Button createStartButton() {
        Button startButton = new Button("게임 시작!");
        startButton.setPrefSize(200, 40);
        startButton.setOnAction(e -> {
            int sides = shapeBox.getValue();
            int playerCount = playerBox.getValue();
            int pieceCount = pieceBox.getValue();

            System.out.println("Sides: " + sides + ", Players: " + playerCount + ", Pieces: " + pieceCount);
            appManager.startGame(sides, playerCount, pieceCount);
        });
        return startButton;
    }
}
