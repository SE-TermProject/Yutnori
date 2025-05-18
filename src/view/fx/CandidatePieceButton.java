package view.fx;

import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import model.BoardPoint;
import model.YutResult;
import view.CandidatePieceButtonBase;

import java.util.Arrays;

public class CandidatePieceButton extends Button implements CandidatePieceButtonBase {
    private final int[] position;
    private final int playerId;
    private final YutResult yutResult;

    public CandidatePieceButton(int[] position, int playerId, YutResult yutResult) {
        this.position = Arrays.copyOf(position, 2);
        this.playerId = playerId;
        this.yutResult = yutResult;

        setPrefSize(30, 30);
        setStyle("-fx-background-color: transparent;");
        setGraphic(createCircle());
    }

    private Circle createCircle() {
        Circle circle = new Circle(12, getColorByPlayer(playerId));
        return circle;
    }

    private Color getColorByPlayer(int playerId) {
        return switch (playerId) {
            case 0 -> Color.rgb(255, 150, 150);
            case 1 -> Color.rgb(150, 150, 255);
            case 2 -> Color.rgb(180, 255, 200);
            case 3 -> Color.rgb(255, 240, 180);
            default -> Color.LIGHTGRAY;
        };
    }

    @Override
    public int[] getPosition() {
        return position;
    }
    @Override
    public int[] getPosition(int numSides) {
        if (position[0] == 0 && position[1] == 0) { // 시작점으로 도착하면
            return new int[]{0, numSides * 5};
        }
        return position;
    }
    @Override
    public YutResult getYutResult() {
        return yutResult;
    }

    @Override
    public void setPosition(int[] position) {
        this.position[0] = position[0];
        this.position[1] = position[1];
    }

    @Override
    public void setPixelPosition(BoardPoint center) {
        double x = center.getX() - getPrefWidth() / 2;
        double y = center.getY() - getPrefWidth() / 2;
        setLayoutX(x);
        setLayoutY(y);
    }
}
