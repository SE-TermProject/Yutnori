package view.fx;

import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import model.Piece;
import view.PieceButtonBase;

public class PieceButton extends Button implements PieceButtonBase {
    private final Piece piece;
    private final int playerId;
    private Color currentColor;
    private int[] pos;  // 초기 위치

    public PieceButton(Piece piece, int playerId) {
        this.piece = piece;
        this.playerId = playerId;
        this.currentColor = getColorByPlayer(playerId);

        setShape(new Circle(10));
        setMinSize(20, 20);
        setMaxSize(20, 20);
        setStyle("-fx-background-color: " + toRgbString(currentColor) + ";" +
                "-fx-text-fill: black;" +  // 텍스트 색상
                "-fx-font-weight: bold;" +
                "-fx-alignment: center;" +
                "-fx-padding: 0;");
    }

    @Override
    public Piece getPiece() {
        return piece;
    }
    @Override
    public int getPlayerId() {
        return playerId;
    }
    public int[] getPosition() {
        return piece.getPosition();
    }
    @Override
    public int[] getPos() { return pos; }

    @Override
    public void setPos(int x, int y) {
        this.pos = new int[]{x, y};
    }

    public void setPixelPosition(Point2D center) {
        setLayoutX(center.getX() - 10);
        setLayoutY(center.getY() - 10);
    }

    private Color getColorByPlayer(int playerId) {
        return switch (playerId) {
            case 0 -> Color.RED;
            case 1 -> Color.BLUE;
            case 2 -> Color.GREEN;
            case 3 -> Color.ORANGE;
            default -> Color.GRAY;
        };
    }

    private String toRgbString(Color color) {
        return "rgb(" + (int)(color.getRed() * 255) + "," +
                (int)(color.getGreen() * 255) + "," +
                (int)(color.getBlue() * 255) + ")";
    }

    @Override
    public void setOutColor() {
        this.setStyle("-fx-background-color: gray;");
    }

    public void updateGroupVisual(int groupSize) {
        if (groupSize > 1) {
            this.setText(String.valueOf(groupSize));  // 가운데에 숫자 표시
        } else {
            this.setText("");  // 혼자일 땐 숫자 제거
        }
        applyCss();
        layout();
    }

    public void initializeView(int currentX, int startY) {
        this.setLayoutX(currentX);
        this.setLayoutY(startY);
        this.setPos(currentX, startY);
        this.setDisable(false);
    }
}
