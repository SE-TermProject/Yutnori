package view.Swing;

import model.Board;
import model.Piece;
import model.YutResult;
import view.CandidatePieceButton;
import view.PieceButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.function.Consumer;

public class YutBoard extends JPanel {

    private final JLabel resultLabel;
    private final JButton throwButton;
    private final JButton throwBackdo, throwDo, throwGae, throwGeol, throwYut, throwMo;
    private final JButton endPiece;
    private JLabel turnLabel;
    private JPanel resultPanel;

//    private final Map<Point, int[]> coordinateToIndexMap = new HashMap<>();
//    private final List<Player> players;
    private final List<PieceButton> pieceButtons = new ArrayList<>();
    private final List<CandidatePieceButton> candidatePieceButtons = new ArrayList<>();
    private int numSides = 4;  // 기본값, 실제 값은 controller에서 설정
    private Board board;

    public YutBoard() {
        setLayout(null);

        throwButton = new JButton("랜덤 윷 던지기");
        throwButton.setBounds(605, 370, 360, 45);
        add(throwButton);

        int y = 420;
        int w = 60;
        int h = 35;

        throwBackdo = new JButton("빽도");
        throwBackdo.setBounds(605, y, w, h);
        add(throwBackdo);

        throwDo = new JButton("도");
        throwDo.setBounds(665, y, w, h);
        add(throwDo);

        throwGae = new JButton("개");
        throwGae.setBounds(725, y, w, h);
        add(throwGae);

        throwGeol = new JButton("걸");
        throwGeol.setBounds(785, y, w, h);
        add(throwGeol);

        throwYut = new JButton("윷");
        throwYut.setBounds(845, y, w, h);
        add(throwYut);

        throwMo = new JButton("모");
        throwMo.setBounds(905, y, w, h);
        add(throwMo);

        endPiece = new JButton("내보내기");
        endPiece.setBounds(480, 550, 90, 40);
        endPiece.setEnabled(false);
        add(endPiece);

        turnLabel = new JLabel("A님의 차례입니다.");
        turnLabel.setBounds(610, 470, 200, 30);
        add(turnLabel);

        resultLabel = new JLabel("윷 결과 🐎");
        resultLabel.setBounds(610, 500, 180, 30);
        add(resultLabel);

        resultPanel = new JPanel();
        resultPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        resultPanel.setBounds(605, 530, 300, 100);
        add(resultPanel);
    }

    public void setupFrame() {
        // Frame 생성 및 view 연결 & 실제 게임 화면으로 이동
        JFrame gameFrame = new JFrame("YutNori");
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setSize(1100, 700);
        gameFrame.setLocationRelativeTo(null);
        gameFrame.add(this);
        gameFrame.setVisible(true);
    }

    // 결과 리스트 업데이트 메서드
    public void updateResultList(List<YutResult> results) {
        resultPanel.removeAll();

        for (YutResult result : results) {
            JLabel label = new JLabel(result.getKoreanName());
            label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            resultPanel.add(label);
        }

        resultPanel.revalidate();
        resultPanel.repaint();
    }

    public JButton getThrowButton() { return throwButton; }
    public JButton getThrowBackdo() { return throwBackdo; }
    public JButton getThrowDo() { return throwDo; }
    public JButton getThrowGae() { return throwGae; }
    public JButton getThrowGeol() { return throwGeol; }
    public JButton getThrowYut() { return throwYut; }
    public JButton getThrowMo() { return throwMo; }
    public JButton getEndPiece() { return endPiece; }

    public void onThrowYutButtonClicked(Runnable callback) {
        throwButton.addActionListener(e -> callback.run());
    }

    public void onManualThrowButtonClicked(YutResult result, Runnable callback) {
        JButton button = switch (result) {
            case BackDo -> throwBackdo;
            case DO -> throwDo;
            case GAE -> throwGae;
            case GUL -> throwGeol;
            case YUT -> throwYut;
            case MO -> throwMo;
        };
        button.addActionListener(e -> callback.run());
    }

    public void addPlayerLabel(int playerId, int x, int y) {
        char playerChar = (char) ('A' + playerId);
        JLabel label = new JLabel(String.valueOf(playerChar));
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setBounds(x, y, 15, 20);
        add(label);
        setComponentZOrder(label, 0);
    }

    public void updateResult(List<YutResult> results) {
        resultPanel.removeAll();

        for (YutResult result : results) {
            JLabel label = new JLabel(result.toString());
            label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            resultPanel.add(label);
        }

        resultPanel.revalidate();
        resultPanel.repaint();
    }

    public void updateTurnLabel(int playerId) {
        turnLabel.setText((char)('A' + playerId) + "님의 차례입니다.");
    }


    public void setNumSides(int numSides) {
        this.numSides = numSides;
    }

    public int getNumSides() { return numSides; }

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

    public void setThrowButtonsEnabled(boolean enabled) {
        throwButton.setEnabled(enabled);
        throwBackdo.setEnabled(enabled);
        throwDo.setEnabled(enabled);
        throwGae.setEnabled(enabled);
        throwGeol.setEnabled(enabled);
        throwYut.setEnabled(enabled);
        throwMo.setEnabled(enabled);
    }

    public void setPossiblePieceButtons(List<CandidatePieceButton> possiblePieceButtons) {
        deletePieceButton(candidatePieceButtons);
        for (CandidatePieceButton pieceButton : possiblePieceButtons) {
            this.add(pieceButton);
            this.setComponentZOrder(pieceButton, 0);  // 항상 최상단
        }
        this.candidatePieceButtons.clear();
        this.candidatePieceButtons.addAll(possiblePieceButtons);

        this.revalidate();
        this.repaint();
    }

    public void deletePieceButton(List<CandidatePieceButton> possiblePieceButtons) {
        for (CandidatePieceButton btn : new ArrayList<>(possiblePieceButtons)) {
            this.remove(btn);                          // 화면에서 제거
            this.candidatePieceButtons.remove(btn);             // 실제 말 리스트에서도 제거 시도
        }

        revalidate();  // 레이아웃 갱신
        repaint();     // 화면 다시 그리기
    }

    public void updatePiecePosition(PieceButton btn) {
        System.out.println("호출");
        int startX, startY;
        if(btn != null){
            startX = btn.getPos()[0];
            startY = btn.getPos()[1];
            btn.setBounds(startX, startY, 20, 20);
            repaint();
        }
    }

    public PieceButton getPieceButton(Piece piece) {
        for (PieceButton button : this.pieceButtons) {
            if (Arrays.equals(button.getPiece().getPosition(), piece.getPosition())) {
                return button;
            }
        }
        return null;
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

        String label = "출발";
        Font font = new Font("SansSerif", Font.BOLD, 16);
        g2.setFont(font);

        FontMetrics fm = g2.getFontMetrics(font);
        int textWidth = fm.stringWidth(label);
        int textHeight = fm.getHeight();

        g2.setColor(Color.BLACK);
        g2.drawString(label, start.x - textWidth / 2, start.y + textHeight / 2 - 6);
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
//       g2.drawString("(" + x + ", " + y + ")", x - size / 2, y - size / 2 - 5);
//    }
    private void drawCircle(Graphics2D g2, int x, int y, int size) {
        if (board == null) return;

        Point point = new Point(x, y);
        int[][] indices = board.getIndicesAt(point);

        boolean isSpecial = isSpecialIndex(indices);

        // 중심점/꼭짓점이면 사이즈 키우기
        int drawSize = size;
        if (isSpecial) {
            drawSize = size + 10; // 강조용 크기 증가
        }

        if (isSpecial) {
            g2.setColor(new Color(230, 200, 250));
            g2.fillOval(x - drawSize / 2, y - drawSize / 2, drawSize, drawSize);
        } else {
            g2.setColor(Color.BLACK);
        }

        g2.drawOval(x - drawSize / 2, y - drawSize / 2, drawSize, drawSize);

//        if (indices != null) {
//            int offsetY = 0;
//            for (int[] idx : indices) {
//                g2.drawString("[" + idx[0] + ", " + idx[1] + "]", x + size / 2, y + size / 2 + offsetY);
//                offsetY += 12; // 여러 개 있을 경우 줄바꿈
//            }
//        }
    }

    private boolean isSpecialIndex(int[][] indices) {
        if (indices == null) return false;
        for (int[] idx : indices) {
            if ((idx[0] == 1 && idx[1] == 8) || (idx[0] == 2 && idx[1] == 13) ||
                    (idx[0] == 3 && idx[1] == 18) || (idx[0] == 0 && idx[1] % 5 == 0)) {
                return true;
            }
        }
        return false;
    }

    /* 말이 한 칸씩 이동 */
    public void animatePieceMovement(PieceButton pieceButton, List<Point> path, Runnable onComplete) {
        new Thread(() -> {
            for (int i = 0; i < path.size(); i++) {
                Point point = path.get(i);
                SwingUtilities.invokeLater(() -> {
                    pieceButton.setPixelPosition(point);
                    repaint();
                });

                try {
                    Thread.sleep(300);  // 이동 간 딜레이
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                if (onComplete != null && i == path.size() - 1) {
                    SwingUtilities.invokeLater(onComplete);  // 애니메이션 끝난 후 실행
                }
            }
        }).start();
    }

    /* 그룹화된 말들 한번에 이동 */
    public void animateGroupedMovement(List<PieceButton> groupButtons, List<Point> path, Runnable onComplete) {
        new Thread(() -> {
            for (int i = 0; i < path.size(); i++) {
                Point point = path.get(i);

                SwingUtilities.invokeLater(() -> {
                    for (PieceButton btn : groupButtons) {
                        btn.setPixelPosition(point);
                    }
                    repaint();
                });

                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                if (onComplete != null && i == path.size() - 1) {
                    SwingUtilities.invokeLater(onComplete);
                }
            }
        }).start();
    }

    public int showGameOverDialog(String winnerName) {
        return JOptionPane.showOptionDialog(
                this,
                winnerName + " 승리!\n게임을 다시 시작하시겠습니까?",
                "게임 종료",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new Object[]{"재시작", "종료"},
                "재시작"
        );
    }

    public void showMessageDialog(String message, String title) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.WARNING_MESSAGE);
    }

    public void showCandidateButtons(List<CandidatePieceButton> buttons, Consumer<CandidatePieceButton> onClick) {
        for (CandidatePieceButton button : buttons) {
            this.add(button);
            this.setComponentZOrder(button, 0);
            button.addActionListener(e -> onClick.accept(button));
        }

        revalidate();
        repaint();
    }

    public void showGetoutButton(YutResult useYut, Runnable onClick) {
        JButton btn = getEndPiece();
        btn.setEnabled(true);
        // 기존 리스너 제거
        for (ActionListener el : btn.getActionListeners()) {
            btn.removeActionListener(el);
        }
        // 새 리스너 등록
        btn.addActionListener(e -> {
            btn.setEnabled(false);
            onClick.run();
        });
    }

    public void showPieceAsFinished(PieceButton btn) {
        int[] pos = btn.getPos();
        btn.setBounds(pos[0], pos[1], 20, 20);
        btn.GetoutColor();
    }
}
