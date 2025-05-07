package view;

import model.Piece;

import javax.swing.*;
import java.awt.*;

public class PieceButton extends JButton {
    private final Piece piece;
    private final int playerId;  // 플레이어 식별용

    public PieceButton(Piece piece, int playerId) {
        this.piece = piece;
        this.playerId = playerId;

        setPreferredSize(new Dimension(20, 20));
        setEnabled(false);  // 기본 비활성화
        setFocusable(false);
        setMargin(new Insets(0, 0, 0, 0));
        setContentAreaFilled(false);
        setBorderPainted(false);
        setOpaque(false);
    }

    public Piece getPiece() {
        return piece;
    }
    public int getPlayerId() {
        return playerId;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(getColorByPlayer(playerId));
        g.fillOval(0, 0, getWidth(), getHeight());
    }

    private Color getColorByPlayer(int playerId) {
        return switch (playerId) {
            case 0 -> Color.RED;
            case 1 -> Color.BLUE;
            case 2 -> Color.GREEN;
            case 3 -> Color.YELLOW;
            default -> Color.GRAY;
        };
    }

}
