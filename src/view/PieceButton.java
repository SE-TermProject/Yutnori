package view;

import model.Piece;

import javax.swing.*;
import java.awt.*;

public class PieceButton extends JButton {
    private final Piece piece;
    private final int playerId;  // 플레이어 식별용
    private final boolean isReal;

    public PieceButton(Piece piece, int playerId, boolean isReal) {
        this.piece = piece;
        this.playerId = playerId;
        this.isReal = isReal;

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
        g.setColor(getColorByPlayer(playerId, isReal));
        g.fillOval(0, 0, getWidth(), getHeight());

        int groupSize = piece.getPieceGroup().size() + 1;
        if(groupSize > 1) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 10));
            String text = String.valueOf(groupSize);
            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getAscent();

            g.drawString(text, (getWidth() - textWidth) / 2, (getHeight() - textHeight) / 2);
        }
    }

    private Color getColorByPlayer(int playerId, boolean isRealPiece) {
        if (isRealPiece) { // 실제 존재하는 말인 경우
            return switch (playerId) {
                case 0 -> Color.RED;
                case 1 -> Color.BLUE;
                case 2 -> Color.GREEN;
                case 3 -> Color.YELLOW;
                default -> Color.GRAY;
            };
        }
        else { // 이동 경로를 보여주기 위한 예비 말인 경우
            return switch (playerId) {
                case 0 -> new Color(255, 150, 150); // 연한 빨강
                case 1 -> new Color(150, 150, 255); // 연한 파랑
                case 2 -> new Color(150, 255, 150); // 연한 초록
                case 3 -> new Color(255, 255, 180); // 연한 노랑
                default -> new Color(200, 200, 200); // 연한 회색
            };
        }
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
