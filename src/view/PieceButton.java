package view;

import model.Piece;

import javax.swing.*;
import java.awt.*;

public class PieceButton extends JButton {
    private final Piece piece;
    private final int playerId;  // 플레이어 식별용
    private Color currentColor;

    public PieceButton(Piece piece, int playerId) {
        this.piece = piece;
        this.playerId = playerId;
        this.currentColor = getColorByPlayer(playerId);

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

    public int[] getPosition() {
        return piece.getPosition();
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

    public void setPixelPosition(Point center) {
        // 버튼 크기를 고려해서 중심에 배치되도록 보정
        int width = getPreferredSize().width;
        int height = getPreferredSize().height;
        int adjustedX = center.x - width / 2;
        int adjustedY = center.y - height / 2;
        setLocation(adjustedX, adjustedY);
    }

}
