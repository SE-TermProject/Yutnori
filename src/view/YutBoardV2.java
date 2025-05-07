package view;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class YutBoardV2 extends JPanel {

    private final JLabel resultLabel;
    private final JButton throwButton;
    private final JButton throwBackdo, throwDo, throwGae, throwGeol, throwYut, throwMo;

//    private final Map<Point, int[]> coordinateToIndexMap = new HashMap<>();
    private final Map<Point, List<int[]>> coordinateToIndexMap = new HashMap<>();
//    private final List<Player> players;
    private final List<PieceButton> pieceButtons = new ArrayList<>();
    private int numSides = 4;  // 기본값, 실제 값은 controller에서 설정

    public YutBoardV2() {
        setLayout(null);

        resultLabel = new JLabel("윷 결과: ", SwingConstants.CENTER);
        resultLabel.setBounds(220, 20, 200, 30);
        add(resultLabel);

        throwButton = new JButton("윷 던지기");
        throwButton.setBounds(240, 60, 160, 40);
        add(throwButton);

        throwBackdo = new JButton("빽도");
        throwBackdo.setBounds(70, 120, 60, 30);
        add(throwBackdo);

        throwDo = new JButton("도");
        throwDo.setBounds(140, 120, 60, 30);
        add(throwDo);

        throwGae = new JButton("개");
        throwGae.setBounds(210, 120, 60, 30);
        add(throwGae);

        throwGeol = new JButton("걸");
        throwGeol.setBounds(280, 120, 60, 30);
        add(throwGeol);

        throwYut = new JButton("윷");
        throwYut.setBounds(350, 120, 60, 30);
        add(throwYut);

        throwMo = new JButton("모");
        throwMo.setBounds(420, 120, 60, 30);
        add(throwMo);
    }

    public JButton getThrowButton() { return throwButton; }
    public JButton getThrowBackdo() { return throwBackdo; }
    public JButton getThrowDo() { return throwDo; }
    public JButton getThrowGae() { return throwGae; }
    public JButton getThrowGeol() { return throwGeol; }
    public JButton getThrowYut() { return throwYut; }
    public JButton getThrowMo() { return throwMo; }

    public void updateResult(String result) {
        resultLabel.setText("윷 결과: " + result);
    }

    public void setNumSides(int numSides) {
        this.numSides = numSides;
    }

    public void setPieceButtons(List<PieceButton> pieceButtons) {
        for (PieceButton pieceButton : pieceButtons) {
            this.add(pieceButton);
        }
        this.pieceButtons.clear();
        this.pieceButtons.addAll(pieceButtons);
        repaint();

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
        int centerX = 350, centerY = 350, radius = 200;

        Point center = new Point(centerX, centerY);
        drawCircle(g2, center.x, center.y, size);

        double startAngle;
        switch (numSides) {
            case 4: startAngle = Math.PI / 4; break;
            case 5: startAngle = Math.PI / 2 + Math.PI / 5; break;
            case 6: startAngle = 0; break;
            default: startAngle = -Math.PI / 2;
        }

        List<Point> vertices = new ArrayList<Point>();
        for (int i = 0; i < numSides; i++) {
            double angle = 2 * Math.PI * i / numSides + startAngle;
            int x = centerX + (int) (radius * Math.cos(angle));
            int y = centerY + (int) (radius * Math.sin(angle));
            vertices.add(new Point(x, y));
        }

        for (Point vertex : vertices) {
            drawBetween(g2, vertex, center, 3, size, false);
        }

        for (int i = 0; i < vertices.size(); i++) {
            drawBetween(g2, vertices.get(i), vertices.get((i + 1) % vertices.size()), 5, size, true);
        }

        Point start = vertices.get(0);
        for (Point p : vertices) {
            if ((numSides == 6 && p.y > start.y) ||
                    (numSides == 5 && p.y >= start.y && p.x >= start.x) ||
                    (numSides != 5 && numSides != 6 && p.y >= start.y && p.x >= start.x)) {
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

//    private void drawCircle(Graphics2D g2, int x, int y, int size) {
//        g2.drawOval(x - size / 2, y - size / 2, size, size);
//        int[] index = coordinateToIndexMap.getOrDefault(new Point(x, y), new int[]{-1, -1});
//        g2.drawString("[" + index[0] + ", " + index[1] + "]", x + size / 2, y + size / 2);
////        g2.drawString("(" + x + ", " + y + ")", x - size / 2, y - size / 2 - 5);
//    }
private void drawCircle(Graphics2D g2, int x, int y, int size) {
    g2.drawOval(x - size / 2, y - size / 2, size, size);

    Point point = new Point(x, y);
    List<int[]> indices = coordinateToIndexMap.get(point);

    if (indices != null) {
        int offsetY = 0;
        for (int[] idx : indices) {
            g2.drawString("[" + idx[0] + ", " + idx[1] + "]", x + size / 2, y + size / 2 + offsetY);
            offsetY += 12; // 여러 개 있을 경우 줄바꿈
        }
    }
}

    public void populateBoardIndexMap() {
        int[][] data;
        if (numSides == 4) {
            data = new int[][] {
                    // 중심점
                    {350, 350, 1, 18}, {350, 350, 2, 13},

                    // 대각선
                    {256, 256, 2, 11}, {303, 303, 2, 12},
                    {444, 256, 1, 6}, {397, 303, 1, 7},
                    {303, 397, 1, 9}, {256, 444, 1, 10},
                    {397, 397, 2, 14}, {444, 444, 2, 15},

                    // 외곽 선상 점들 (왼쪽 -> 위 -> 오른쪽 -> 아래 방향)
                    {491, 491, 0, 0}, {491, 491, 2, 16}, {491, 434, 0, 1}, {491, 378, 0, 2}, {491, 321, 0, 3}, {491, 265, 0, 4}, {491, 209, 0, 5},
                    {434, 209, 0, 6}, {378, 209, 0, 7}, {321, 209, 0, 8}, {265, 209, 0, 9}, {209, 209, 0, 10},
                    {209, 265, 0, 11}, {209, 321, 0, 12}, {209, 378, 0, 13}, {209, 434, 0, 14}, {209, 491, 0, 15}, {209, 491, 1, 11},
                    {265, 491, 0, 16}, {321, 491, 0, 17}, {378, 491, 0, 18}, {434, 491, 0, 19}
            };
        } else if (numSides == 5) {
            data = new int[][] {
                    {350, 216, 2, 11}, {350, 283, 2, 12}, {223, 309, 3, 16}, {286, 329, 3, 17},
                    {350, 350, 1, 8}, {350, 350, 2, 13}, {350, 350, 3, 18}, {413, 329, 1, 7}, {311, 403, 1, 9}, {389, 403, 2, 14},
                    {272, 457, 1, 10}, {428, 457, 2, 15}, {350, 150, 0, 10}, {388, 177, 0, 9},
                    {426, 205, 0, 8}, {464, 233, 0, 7}, {502, 261, 0, 6}, {540, 289, 0, 5},
                    {476, 309, 1, 6}, {525, 333, 0, 4}, {510, 377, 0, 3}, {496, 422, 0, 2},
                    {481, 466, 0, 1}, {467, 511, 0, 0}, {467, 511, 2, 16}, {420, 511, 0, 24}, {373, 511, 0, 23},
                    {326, 511, 0, 22}, {279, 511, 0, 21}, {233, 511, 0, 20}, {233, 511, 1, 11}, {218, 466, 0, 19},
                    {203, 422, 0, 18}, {189, 377, 0, 17}, {174, 333, 0, 16}, {160, 289, 0, 15},
                    {198, 261, 0, 14}, {236, 233, 0, 13}, {274, 205, 0, 12}, {312, 177, 0, 11}
            };
        } else if (numSides == 6) {
            data = new int[][] {
                    {283, 234, 3, 16}, {416, 234, 2, 11}, {316, 292, 3, 17}, {383, 292, 2, 12},
                    {216, 350, 4, 21}, {283, 350, 4, 22}, {350, 350, 1, 8}, {350, 350, 2, 13}, {350, 350, 3, 18}, {350, 350, 4, 23},
                    {416, 350, 1, 7}, {483, 350, 1, 6}, {317, 407, 2, 14}, {383, 407, 3, 19}, {284, 465, 2, 15},
                    {416, 465, 3, 20}, {250, 177, 0, 15}, {290, 177, 0, 14}, {330, 177, 0, 13},
                    {370, 177, 0, 12}, {410, 177, 0, 11}, {450, 177, 0, 10}, {470, 211, 0, 9},
                    {490, 246, 0, 8}, {510, 280, 0, 7}, {530, 315, 0, 6}, {550, 350, 0, 5},
                    {530, 384, 0, 4}, {510, 419, 0, 3}, {490, 453, 0, 2}, {470, 488, 0, 1},
                    {450, 523, 0, 0}, {450, 523, 3, 21}, {410, 523, 0, 29}, {370, 523, 0, 28}, {330, 523, 0, 27},
                    {290, 523, 0, 26}, {251, 523, 0, 25}, {230, 488, 0, 24}, {210, 453, 0, 23},
                    {190, 419, 0, 22}, {170, 384, 0, 21}, {150, 350, 0, 20}, {170, 315, 0, 19},
                    {190, 280, 0, 18}, {210, 246, 0, 17}, {230, 211, 0, 16}
            };
        } else {
            data = new int[0][];
        }

        for (int[] entry : data) {
            Point point = new Point(entry[0], entry[1]);
            coordinateToIndexMap
                    .computeIfAbsent(point, k -> new ArrayList<>())
                    .add(new int[]{entry[2], entry[3]});
        }
    }
}
