package view;

import model.Board;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class YutBoardV2 extends JPanel {

    private final JLabel resultLabel;
    private final JButton throwButton;
    private final JButton throwBackdo, throwDo, throwGae, throwGeol, throwYut, throwMo;

//    private final Map<Point, int[]> coordinateToIndexMap = new HashMap<>();
//    private final List<Player> players;
    private final List<PieceButton> pieceButtons = new ArrayList<>();
    private int numSides = 4;  // 기본값, 실제 값은 controller에서 설정
    private Board board;

    public YutBoardV2() {
        setLayout(null);

        resultLabel = new JLabel("윷 결과: ", SwingConstants.CENTER);
        resultLabel.setBounds(220, 20, 200, 30);
        add(resultLabel);

        throwButton = new JButton("랜덤 윷 던지기");
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

    public void setBoard(Board board) {
        this.board = board;
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

        if (board == null) return;

        Point point = new Point(x, y);
        int[][] indices = board.getIndicesAt(point);

        if (indices != null) {
            int offsetY = 0;
            for (int[] idx : indices) {
                g2.drawString("[" + idx[0] + ", " + idx[1] + "]", x + size / 2, y + size / 2 + offsetY);
                offsetY += 12; // 여러 개 있을 경우 줄바꿈
            }
        }
    }

    /* 말이 한 칸씩 이동 */
    public void animatePieceMovement(PieceButton pieceButton, List<Point> path) {
        new Thread(() -> {
            for (Point point : path) {
                SwingUtilities.invokeLater(() -> {
                    pieceButton.setPixelPosition(point);
                    repaint();
                });

                try {
                    Thread.sleep(300);  // 이동 간 딜레이
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }
}
