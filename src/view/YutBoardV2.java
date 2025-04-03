package view;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class YutBoardV2 extends JPanel {

    private int numSides;

    public YutBoardV2(int numSides) {
        this.numSides = numSides;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBoard(g);
    }

    private void drawBoard(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int size = 30; // 동그라미 크기
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int radius = 200;

        Point center = new Point(centerX, centerY);
        drawCircle(g2, center.x, center.y, size); // 중심 원

        // 꼭짓점 계산
        List<Point> vertices = new ArrayList<>();
        for (int i = 0; i < numSides; i++) {
            double angle = 2 * Math.PI * i / numSides - Math.PI / 2; // 위쪽이 0도
            int x = centerX + (int) (radius * Math.cos(angle));
            int y = centerY + (int) (radius * Math.sin(angle));
            vertices.add(new Point(x, y));
        }

        // 꼭짓점 ↔ 중심 사이: 2개 점
        for (Point vertex : vertices) {
            drawBetween(g2, vertex, center, 3, size, false); // 3등분 → 내부 2개만
        }

        // 꼭짓점 ↔ 다음 꼭짓점 사이: 6개 점 (시작/끝 포함)
        for (int i = 0; i < vertices.size(); i++) {
            Point from = vertices.get(i);
            Point to = vertices.get((i + 1) % vertices.size());
            drawBetween(g2, from, to, 5, size, true); // 5등분 → 6개 점
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

    public static void main(String[] args) {
        String input = JOptionPane.showInputDialog("몇 변을 가진 윷놀이 판을 원하시나요? (예: 4, 5, 6)");
        int sides = 4;
        try {
            sides = Integer.parseInt(input);
            if (sides < 3) sides = 4;
        } catch (Exception e) {
            // 기본값 유지
        }

        JFrame frame = new JFrame(sides + "각 윷놀이 판");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(650, 650);
        frame.add(new YutBoardV2(sides));
        frame.setVisible(true);
    }
}