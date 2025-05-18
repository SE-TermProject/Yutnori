package view.fx;

import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import model.Piece;

import java.awt.*;

public class PieceButton extends Button {
    private final Piece piece;
    private final int playerId;
    private Color currentColor;

    public PieceButton(Piece piece, int playerId) {
        this.piece = piece;
        this.playerId = playerId;
        this.currentColor = getColorByPlayer(playerId);

        setShape(new Circle(10));
        setMinSize(20, 20);
        setMaxSize(20, 20);
        setStyle("-fx-background-color: " + toRgbString(currentColor));
    }

    public Piece getPiece() {
        return piece;
    }
    public int getPlayerId() {
        return playerId;
    }
    public void setPixelPosition(Point center) {
        setLayoutX(center.x - 10);
        setLayoutY(center.y - 10);
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
}
