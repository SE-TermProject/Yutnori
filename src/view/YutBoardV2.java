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
        int centerX = 350;
        int centerY = 350;
        int radius = 200;

        Point center = new Point(centerX, centerY);
        drawCircle(g2, center.x, center.y, size);

        // 시작 각도 조정
        double startAngle;
        switch (numSides) {
            case 4:
                startAngle = Math.PI / 4; // 사각형
                break;
            case 5:
                startAngle = Math.PI / 2 + Math.PI / 5; // 오각형
                break;
            case 6:
                startAngle = 0; // 밑변이 수평한 육각형
                break;
            default:
                startAngle = -Math.PI / 2; // 디폴트
                break;
        }

        List<Point> vertices = new ArrayList<>();
        for (int i = 0; i < numSides; i++) {
            double angle = 2 * Math.PI * i / numSides + startAngle;
            int x = centerX + (int) (radius * Math.cos(angle));
            int y = centerY + (int) (radius * Math.sin(angle));
            vertices.add(new Point(x, y));
        }

        for (Point vertex : vertices) {
            drawBetween(g2, vertex, center, 3, size, false); // 중심 ↔ 꼭짓점
        }

        for (int i = 0; i < vertices.size(); i++) {
            Point from = vertices.get(i);
            Point to = vertices.get((i + 1) % vertices.size());
            drawBetween(g2, from, to, 5, size, true); // 변 위 점들
        }

        // 출발 위치 표시
        Point start = vertices.get(0);

        if (numSides == 6) {
            // 육각형 → y값이 가장 큰 꼭짓점 (아래쪽)
            for (Point p : vertices) {
                if (p.y > start.y) {
                    start = p;
                }
            }
        } else if (numSides == 5) {
            // 오각형 → x값이 가장 큰 꼭짓점 (오른쪽)
            for (Point p : vertices) {
                if (p.y >= start.y && p.x >= start.x) {
                    start = p;
                }
            }
        } else {
            // 기본: 아래 + 오른쪽 (기존 방식)
            for (Point p : vertices) {
                if (p.y >= start.y && p.x >= start.x) {
                    start = p;
                }
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

    private int[] convertToBoardIndex(int x, int y) {
        int boardX = 0, boardY = 0;

        if(numSides == 4){

            if (x == 491) {
                boardY = (491 - y) / 56;
                return new int[]{0, boardY};
            } else if (y == 491) {
                boardY = 20 - (491 - x) / 56;
                return new int[]{0, boardY};
            } else if (x == 209) {
                boardY = 10 - (209 - y) / 56;
                return new int[]{0, boardY};
            } else if (y == 209) {
                boardY = 5 + (491 - x) / 56;
                return new int[]{0, boardY};
            } else {
                if (x == 256 && y == 256) return new int[]{2, 11};
                if (x == 444 && y == 256) return new int[]{1, 6};
                if (x == 303 && y == 303) return new int[]{2, 12};
                if (x == 397 && y == 303) return new int[]{1, 7};
                if (x == 350 && y == 350) return new int[]{1, 1};
                if (x == 303 && y == 397) return new int[]{1, 9};
                if (x == 397 && y == 397) return new int[]{2, 14};
                if (x == 256 && y == 444) return new int[]{1, 10};
                if (x == 444 && y == 444) return new int[]{2, 15};
            }
        }

        if (numSides == 5){
                if (x == 350 && y == 216) return new int[]{2, 11};
                if (x == 350 && y == 283) return new int[]{2, 12};
                if (x == 223 && y == 309) return new int[]{3, 16};
                if (x == 286 && y == 329) return new int[]{3, 17};
                if (x == 350 && y == 350) return new int[]{1, 8};  // 중심점
                if (x == 413 && y == 329) return new int[]{1, 7};
                if (x == 311 && y == 403) return new int[]{1, 9};
                if (x == 389 && y == 403) return new int[]{2, 14};
                if (x == 272 && y == 457) return new int[]{1, 10};
                if (x == 428 && y == 457) return new int[]{2, 15};

                // 가장자리 좌표
                if (x == 350 && y == 150) return new int[]{0, 10};
                if (x == 388 && y == 177) return new int[]{0, 9};
                if (x == 426 && y == 205) return new int[]{0, 8};
                if (x == 464 && y == 233) return new int[]{0, 7};
                if (x == 502 && y == 261) return new int[]{0, 6};
                if (x == 540 && y == 289) return new int[]{0, 5};
                if (x == 476 && y == 309) return new int[]{1, 6};
                if (x == 525 && y == 333) return new int[]{0, 4};
                if (x == 510 && y == 377) return new int[]{0, 3};
                if (x == 496 && y == 422) return new int[]{0, 2};
                if (x == 481 && y == 466) return new int[]{0, 1};
                if (x == 467 && y == 511) return new int[]{0, 0};
                if (x == 420 && y == 514) return new int[]{0, 24};
                if (x == 373 && y == 514) return new int[]{0, 23};
                if (x == 326 && y == 513) return new int[]{0, 22};
                if (x == 279 && y == 513) return new int[]{0, 21};
                if (x == 233 && y == 513) return new int[]{0, 20};
                if (x == 218 && y == 466) return new int[]{0, 19};
                if (x == 203 && y == 422) return new int[]{0, 18};
                if (x == 189 && y == 377) return new int[]{0, 17};
                if (x == 174 && y == 333) return new int[]{0, 16};
                if (x == 160 && y == 289) return new int[]{0, 15};
                if (x == 198 && y == 261) return new int[]{0, 14};
                if (x == 236 && y == 233) return new int[]{0, 13};
                if (x == 274 && y == 205) return new int[]{0, 12};
                if (x == 312 && y == 177) return new int[]{0, 11};
            }


        return new int[]{boardX, boardY};
    }

    private void drawCircle(Graphics2D g2, int x, int y, int size) {
        g2.drawOval(x - size / 2, y - size / 2, size, size);
        int[] boardIndex = convertToBoardIndex(x, y);
//        g2.drawString("(" + x + ", " + y + ")", x - size / 2, y - size / 2 - 5);
        g2.drawString("[" + boardIndex[0] + ", " + boardIndex[1] + "]", x + size / 2, y + size / 2);
    }

}
