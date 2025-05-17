package view.swing;

import model.Piece;
import model.BoardPoint;
import view.PieceButtonBase;

import javax.swing.*;
import java.awt.*;

public class PieceButton extends JButton implements PieceButtonBase {
    private final Piece piece;
    private final int playerId;  // 플레이어 식별용
    private Color currentColor;
    private int[] Pos; // 초기 위치

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
    public int[] getPos() {
        return Pos;
    }

    @Override
    public void setPos(int x, int y) {
        this.Pos = new int[]{x, y};
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(currentColor);
        g.fillOval(0, 0, getWidth(), getHeight());

        int groupSize = piece.getPieceGroup().size();
        if(groupSize > 1) {
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 14));
            String text = String.valueOf(groupSize);
            FontMetrics fm = g.getFontMetrics();

            // 텍스트의 크기 계산
            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getAscent();

            // 텍스트가 버튼의 중앙에 오도록 위치 계산
            int x = (getWidth() - textWidth) / 2;
            int y = (getHeight() + textHeight) / 2;  // y는 ascent를 고려해 세팅

            g.drawString(text, x, y);
        }
    }

    private Color getColorByPlayer(int playerId) {
        return switch (playerId) {
            case 0 -> Color.RED;
            case 1 -> Color.BLUE;
            case 2 -> new Color(0, 128, 0);
            case 3 -> new Color(255, 165, 0);
            default -> Color.GRAY;
        };
    }

    @Override
    public void setPixelPosition(BoardPoint center) {
        // 버튼 크기를 고려해서 중심에 배치되도록 보정
        int width = getPreferredSize().width;
        int height = getPreferredSize().height;
        int adjustedX = center.x - width / 2;
        int adjustedY = center.y - height / 2;
        setLocation(adjustedX, adjustedY);
        repaint();
    }

    @Override
    public void setOutColor() {
        this.currentColor = Color.GRAY;
        repaint();
    }
}
