package view;

import model.YutResult;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class CandidatePieceButton extends JButton {
    private final int[] position;
    private final int playerId;  // 플레이어 식별용
    private final YutResult yutResult;

    public CandidatePieceButton(int[] position, int playerId, YutResult yutResult) {
        this.position = Arrays.copyOf(position, 2);
        this.playerId = playerId;
        this.yutResult = yutResult;

        setPreferredSize(new Dimension(30, 30));
        setEnabled(false);  // 기본 비활성화
        setFocusable(false);
        setMargin(new Insets(0, 0, 0, 0));
        setContentAreaFilled(false);
        setBorderPainted(false);
        setOpaque(false);
    }

    public int[] getPosition() {
        return position;
    }

    public YutResult getYutResult() {
        return yutResult;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(getColorByPlayer(playerId));
        g.fillOval(0, 0, getWidth(), getHeight());
    }

    private Color getColorByPlayer(int playerId) {
        return switch (playerId) {
            case 0 -> new Color(255, 150, 150); // 연한 빨강
            case 1 -> new Color(150, 150, 255); // 연한 파랑
            case 2 -> new Color(180, 255, 200); // 연한 초록
            case 3 -> new Color(255, 240, 180); // 연한 노랑
            default -> new Color(200, 200, 200); // 연한 회색
        };
    }

    public void setPixelPosition(Point center) {
        // 버튼 크기를 고려해서 중심에 배치되도록 보정
        int width = getPreferredSize().width;
        int height = getPreferredSize().height;
        int adjustedX = center.x - width / 2;
        int adjustedY = center.y - height / 2;
        setBounds(adjustedX, adjustedY, width, height);
    }
}
