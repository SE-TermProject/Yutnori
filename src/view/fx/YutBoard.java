package view.fx;

import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.control.*;
import javafx.scene.text.Font;
import model.Board;

public class YutBoard extends BorderPane {
    private Pane boardLayer;
    private VBox sidePanel;
    private HBox resultPanel;
    private Label turnLabel;

    private Button throwButton;
    private Button throwBackdo, throwDo, throwGae, throwGeol, throwYut, throwMo;
    private Button outButton;

    private int numSides;
    private Board board;

    public YutBoard() {
        this.setPrefSize(1100, 700);

        setupBoardLayer();
        setupSidePanel();
        this.setCenter(boardLayer);
        this.setRight(sidePanel);
    }

    private void setupBoardLayer() {
        boardLayer = new Pane();
        boardLayer.setPrefSize(700, 700);
    }

    private void setupSidePanel() {
        sidePanel = new VBox(10);
        sidePanel.setPadding(new Insets(20));
        sidePanel.setPrefWidth(400);

        // 윷 던지기 버튼
        throwButton = createButton("랜덤 윷 던지기", 300);
        // 수동 윷 버튼
        HBox selectedThrowButtons = new HBox(5);
        throwBackdo = new Button("빽도");
        throwDo = new Button("도");
        throwGae = new Button("개");
        throwGeol = new Button("걸");
        throwYut = new Button("윷");
        throwMo = new Button("모");
        selectedThrowButtons.getChildren().addAll(
                throwBackdo, throwDo, throwGae, throwGeol, throwYut, throwMo
        );

        // 내보내기 버튼
        outButton = new Button("내보내기");
        outButton.setDisable(true);

        // 차례 표시
        turnLabel = new Label("A님의 차례입니다.");
        turnLabel.setFont(new Font(16));

        // 결과 패널
        resultPanel = new HBox(5);

        // 패널 구성
        sidePanel.getChildren().addAll(
                throwButton, selectedThrowButtons, outButton, turnLabel, new Label("윷 결과 🐎"), resultPanel
        );
    }

    private Button createButton(String text, double width) {
        Button btn = new Button(text);
        btn.setPrefWidth(width);
        return btn;
    }

    public Pane getBoardLayer() {
        return boardLayer;
    }

    public Button getThrowButton() { return throwButton; }
    public Button getThrowBackdo() { return throwBackdo; }
    public Button getThrowDo() { return throwDo; }
    public Button getThrowGae() { return throwGae; }
    public Button getThrowGeol() { return throwGeol; }
    public Button getThrowYut() { return throwYut; }
    public Button getThrowMo() { return throwMo; }
    public Button getOutButton() { return outButton; }

    public void setNumSides(int numSides) {
        this.numSides = numSides;
    }

    public void setBoard(Board board) {
        this.board = board;
    }
}
