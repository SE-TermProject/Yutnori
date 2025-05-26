package view.fx;

import controller.fx.BoardLayoutCalculator;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.geometry.*;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.*;
import java.util.function.Consumer;

public class YutBoard extends Pane {
    private Pane boardLayer;
    private VBox sidePanel;
    private HBox resultPanel;
    private Label turnLabel;

    private Button throwButton;
    private Button throwBackdo, throwDo, throwGae, throwGeol, throwYut, throwMo;
    private Button outButton;
    private Button endPiece;

    private final List<PieceButton> pieceButtons = new ArrayList<>();
    private final List<CandidatePieceButton> candidatePieceButtons = new ArrayList<>();
    private Set<Point2D> specialPoints = new HashSet<>();
    private final int numSides;
    private Canvas boardCanvas;

    public YutBoard(int numSides) {
        this.setPrefSize(1100, 700);
        this.numSides = numSides;
        setupBoardLayer();
        setupSidePanel();

        boardLayer.setLayoutX(50);
        boardLayer.setLayoutY(50);

        sidePanel.setLayoutX(650);  // ← 왼쪽으로 더 당기고 싶으면 숫자 줄이기
        sidePanel.setLayoutY(400);  // 이미 아래로 내렸으니 유지

        this.getChildren().addAll(boardLayer, sidePanel);
    }

    private void setupBoardLayer() {
        boardLayer = new Pane();
        boardLayer.setPrefSize(700, 700);

        boardCanvas = new Canvas(700, 700);
        boardLayer.getChildren().add(boardCanvas);

        drawBoard(boardCanvas.getGraphicsContext2D());
    }

    private void setupSidePanel() {
        sidePanel = new VBox(5);                  // 간격 줄이기
        sidePanel.setPrefWidth(280);              // 폭 조정
        sidePanel.setPrefHeight(260);             // 높이 제한
        sidePanel.setPadding(new Insets(10));     // 여백 최소화

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

    public void updateResultList(List<String> resultName) {
        resultPanel.getChildren().clear();

        for (String name : resultName) {
            Label label = new Label(name);
            label.setStyle("-fx-border-color: black; -fx-padding: 4;");
            resultPanel.getChildren().add(label);
        }
    }

    public Button getThrowButton() { return throwButton; }
    public Button getThrowBackdo() { return throwBackdo; }
    public Button getThrowDo() { return throwDo; }
    public Button getThrowGae() { return throwGae; }
    public Button getThrowGeol() { return throwGeol; }
    public Button getThrowYut() { return throwYut; }
    public Button getThrowMo() { return throwMo; }
    public Button getOutButton() { return outButton; }

    /* setter */
    public void setPieceButtons(List<PieceButton> pieceButtons) {
        boardLayer.getChildren().removeAll(this.pieceButtons);
        boardLayer.getChildren().addAll(pieceButtons);
        this.pieceButtons.clear();
        this.pieceButtons.addAll(pieceButtons);
    }

    /* 랜덤 윷 던지기 버튼 클릭 시 실행할 동작 등록 */
    public void setOnThrowButton(Runnable callback) {
        throwButton.setOnAction(e -> callback.run());
    }

    /* 지정 윷 던지기 버튼 클릭 시 실행할 동작 등록 */
    public void setOnManualThrowButton(String resultName, Runnable callback) {
        Button button = switch (resultName) {
            case "BackDo" -> throwBackdo;
            case "DO" -> throwDo;
            case "GAE" -> throwGae;
            case "GUL" -> throwGeol;
            case "YUT" -> throwYut;
            case "MO" -> throwMo;
            default -> throw new IllegalArgumentException("알 수 없는 윷 결과: " + resultName);
        };
        button.setOnAction(e -> callback.run());
    }

    /* 말 옆에 player 라벨 달기 */
    public void addPlayerLabel(int playerId, double x, double y, Pane boardLayer) {
        char playerChar = (char) ('A' + playerId);
        Label label = new Label(String.valueOf(playerChar));

        label.setFont(Font.font("SansSerif", FontWeight.BOLD, 14));
        label.setLayoutX(x);
        label.setLayoutY(y);
        label.setPrefSize(15, 20);

        boardLayer.getChildren().add(label); // z-order 상 가장 위로 올라감
    }

    /* 현재 player에 따른 순서 표시 변경 */
    public void updateTurnLabel(int playerId) {
        turnLabel.setText((char)('A' + playerId) + "님의 차례입니다.");
    }

    /* 윷 던지기 버튼 및 수동 윷 던지기 버튼의 활성화 여부 선택 */
    public void setThrowButtonsEnabled(boolean enabled) {
        throwButton.setDisable(!enabled);
        throwBackdo.setDisable(!enabled);
        throwDo.setDisable(!enabled);
        throwGae.setDisable(!enabled);
        throwGeol.setDisable(!enabled);
        throwYut.setDisable(!enabled);
        throwMo.setDisable(!enabled);
    }

    /* 선택한 말이 이동할 수 있는 후보 칸들을 화면에 표시 */
    public void showCandidateButtons(List<CandidatePieceButton> possiblePieceButtons) {
        deletePieceButton(candidatePieceButtons);
        for (CandidatePieceButton pieceButton : possiblePieceButtons) {
            if (!boardLayer.getChildren().contains(pieceButton)) {
                boardLayer.getChildren().add(pieceButton);
            }
        }
        this.candidatePieceButtons.clear();
        this.candidatePieceButtons.addAll(possiblePieceButtons);
    }

    /* 후보 칸 버튼들을 화면에서 제거하고, 내부 리스트에서도 제거 */
    public void deletePieceButton(List<CandidatePieceButton> possiblePieceButtons) {
        for (CandidatePieceButton btn : new ArrayList<>(possiblePieceButtons)) {
            boardLayer.getChildren().remove(btn);                          // 화면에서 제거
        }
        // JavaFX는 자동으로 레이아웃 및 화면 갱신하므로 repaint() 별도 호출 불필요
        this.candidatePieceButtons.clear();
    }

    /* 이동하는 말(pieceButton)의 위치를 업데이트하며 화면에 반영 */
    public void updatePiecePosition(PieceButton btn) {
        System.out.println("호출");
        int startX, startY;
        if(btn != null){
            startX = btn.getPos()[0];
            startY = btn.getPos()[1];
            btn.setLayoutX(startX);
            btn.setLayoutY(startY);
            btn.setPrefWidth(20);
            btn.setPrefHeight(20);
        }
    }

    /* 선택한 말이 이동할 수 있는 후보 칸 버튼 클릭 시 동작 연결 */
    public void moveActionToCandidates(List<CandidatePieceButton> buttons, Consumer<CandidatePieceButton> onClick) {
        for (CandidatePieceButton button : buttons) {
            if (!boardLayer.getChildren().contains(button)) {
                boardLayer.getChildren().add(button);
            }
            button.toFront();
            button.setOnAction(e -> {
                onClick.accept(button);                       // 말 이동 등 로직 실행
                deletePieceButton(candidatePieceButtons);     // 후보 칸 버튼 제거
            });
        }
    }

    /* 윷놀이 판 그리기 */
    private void drawBoard(GraphicsContext g) {
        // 안티앨리어싱 효과는 JavaFX에서 기본 적용되어 있음 (추가 설정 필요 없음)
        int size = 30;
        Point2D center = new Point2D(350, 350);
        int radius = 200;

        drawCircle(g, center.getX(), center.getY(), size);

        BoardLayoutCalculator layout = new BoardLayoutCalculator(numSides, center, radius);
        List<Point2D> vertices = layout.calculateVertices();

        for (Point2D vertex : vertices) {
            List<Point2D> mids = layout.calculateIntermediatePoints(vertex, center, 3, false);
            for (Point2D p : mids) drawCircle(g, p.getX(), p.getY(), size);
        }

        for (int i = 0; i < vertices.size(); i++) {
            List<Point2D> mids = layout.calculateIntermediatePoints(
                    vertices.get(i), vertices.get((i + 1) % vertices.size()), 5, true);
            for (Point2D p : mids) drawCircle(g, p.getX(), p.getY(), size);
        }


        // 출발 텍스트 표시
        Point2D start = layout.findStartPoint(vertices);
        String label = "출발";

        Font font = new Font("SansSerif", 16);
        g.setFont(font);

        Text text = new Text(label);
        text.setFont(font);
        double textWidth = text.getLayoutBounds().getWidth();
        double baselineOffset = text.getBaselineOffset();

        g.setFill(Color.BLACK);
        g.fillText(label, start.getX() - textWidth / 2, start.getY() + baselineOffset / 2 - 6);
    }

    /* 윷놀이 판의 각 칸 그리기 */
    private void drawCircle(GraphicsContext g2, double x, double y, double size) {
        Point2D point = new Point2D(x, y);
        boolean isSpecial = specialPoints.contains(point);

        // 중심점/꼭짓점이면 사이즈 키우기
        double drawSize = size;
        if (isSpecial) {
            drawSize = size + 10; // 강조용 크기 증가
        }

        double topLeftX = x - drawSize / 2;
        double topLeftY = y - drawSize / 2;

        if (isSpecial) {
            g2.setFill(Color.rgb(230, 200, 250));
            g2.fillOval(topLeftX, topLeftY, drawSize, drawSize);
        }

        g2.setStroke(Color.BLACK);
        g2.strokeOval(x - drawSize / 2, y - drawSize / 2, drawSize, drawSize);
    }

    /* 말&그룹화된 말들 한 칸씩 이동 */
    public void animatePieceMovement(List<PieceButton> groupButtons, List<Point2D> path, Runnable onComplete) {
        Timeline timeline = new Timeline();
        Duration delayPerStep = Duration.seconds(0.3);

        for (int i = 0; i < path.size(); i++) {
            Point2D point = path.get(i);

            KeyFrame keyFrame = new KeyFrame(delayPerStep.multiply(i), e -> {
                for (PieceButton btn : groupButtons) {
                    btn.setPixelPosition(point); // 이 메서드는 JavaFX 좌표 적용 방식이어야 함
                }
            });

            timeline.getKeyFrames().add(keyFrame);
        }

        if (onComplete != null) {
            timeline.setOnFinished(e -> onComplete.run());
        }

        timeline.play();
    }

    /* player 우승 시 게임 재시작 및 종료 선택창 띄우기 */
    public int showGameOverDialog(String winnerName) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("게임 종료");
        alert.setHeaderText(null);
        alert.setContentText(winnerName + " 승리!\n게임을 다시 시작하시겠습니까?");

        ButtonType restartButton = new ButtonType("재시작");
        ButtonType exitButton = new ButtonType("종료");
        alert.getButtonTypes().setAll(restartButton, exitButton);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == restartButton) {
            return 0; // 재시작 선택
        } else {
            return 1; // 종료 선택
        }
    }

    /* 메시지 창 띄우기 */
    public void showMessageDialog(String message, String title) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    /* 말이 도착 지점에 도착할 수 있는 경우 내보내기 버튼 활성화 */
    public void showGetoutButton(Runnable onClick) {
        Button btn = getOutButton(); // JavaFX Button 반환한다고 가정
        btn.setDisable(false); // 활성화

        // 기존 이벤트 핸들러 제거
        btn.setOnAction(null);

        // 새 이벤트 핸들러 등록
        btn.setOnAction(e -> {
            btn.setDisable(true);
            onClick.run();
        });
    }

    public void showPieceAsFinished(PieceButton btn) {
        int[] pos = btn.getPos();  // getPos()가 double[] 반환한다고 가정
        btn.setLayoutX(pos[0]);
        btn.setLayoutY(pos[1]);
        btn.setPrefWidth(20);
        btn.setPrefHeight(20);
        btn.setOutColor();  // 이 메서드는 FX 스타일로 구현되어 있어야 함
    }
}
