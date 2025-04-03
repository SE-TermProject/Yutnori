package view;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class YutBoardV2 extends JPanel {

    private final int numSides;
    private final int playerCount;
    private final int pieceCount;

    private final JLabel resultLabel;
    private final JButton throwButton;

    public YutBoardV2(int numSides, int playerCount, int pieceCount) {
        this.numSides = numSides;
        this.playerCount = playerCount;
        this.pieceCount = pieceCount;

        setLayout(null); // 절대 위치 사용

        // 윷 결과 표시 라벨
        resultLabel = new JLabel("윷 결과: ", SwingConstants.CENTER);
        resultLabel.setBounds(220, 20, 200, 30);
        add(resultLabel);

        // 윷 던지기 버튼
        throwButton = new JButton("윷 던지기");
        throwButton.setBounds(240, 60, 160, 40);
        add(throwButton);
    }

    public JButton getThrowButton() {
        return throwButton;
    }

    public void updateResult(String result) {
        resultLabel.setText("윷 결과: " + result);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBoard(g);
    }

    private void drawBoard(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int size = 30;
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int radius = 200;

        Point center = new Point(centerX, centerY);
        drawCircle(g2, center.x, center.y, size);

        List<Point> vertices = new ArrayList<>();
        for (int i = 0; i < numSides; i++) {
            double angle = 2 * Math.PI * i / numSides - Math.PI / 2;
            int x = centerX + (int) (radius * Math.cos(angle));
            int y = centerY + (int) (radius * Math.sin(angle));
            vertices.add(new Point(x, y));
        }

        for (Point vertex : vertices) {
            drawBetween(g2, vertex, center, 3, size, false); // 중심 ↔ 꼭짓점: 점 2개
        }

        for (int i = 0; i < vertices.size(); i++) {
            Point from = vertices.get(i);
            Point to = vertices.get((i + 1) % vertices.size());
            drawBetween(g2, from, to, 5, size, true); // 변 하나당 점 6개
        }

        // 출발 위치: 오른쪽 아래 꼭짓점
        Point start = vertices.get(0);
        for (Point p : vertices) {
            if (p.x >= start.x && p.y >= start.y) {
                start = p;
            }
        }
        g2.drawString("출발", start.x - 15, start.y + 5);
    }

    private void drawBetween(Graphics2D g2, Point from, Point to, int divisions, int size, boolean includeEnds) {
        int start = includeEnds ? 0 : 1;
        int end = includeEnds ? divisions : divisions - 1;

        for (int i = start; i <= end; i++) {
            double t = i / (double) divisions;
            int x = (int) (from.x * (1 - t) + to.x * t);
            int y = (int) (from.y * (1 - t) + to.y * t);
            drawCircle(g2, x, y, size);
        }
    }

    private void drawCircle(Graphics2D g2, int x, int y, int size) {
        g2.drawOval(x - size / 2, y - size / 2, size, size);
    }
}