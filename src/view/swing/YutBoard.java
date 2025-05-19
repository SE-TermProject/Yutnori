package view.swing;

import controller.swing.BoardLayoutCalculator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.function.Consumer;

public class YutBoard extends JPanel {

    private JLabel resultLabel;
    private JButton throwButton;
    private JButton throwBackdo, throwDo, throwGae, throwGeol, throwYut, throwMo;
    private JButton endPiece;
    private JLabel turnLabel;
    private JPanel resultPanel;

//    private final Map<Point, int[]> coordinateToIndexMap = new HashMap<>();
//    private final List<Player> players;
    private final List<PieceButton> pieceButtons = new ArrayList<>();
    private final List<CandidatePieceButton> candidatePieceButtons = new ArrayList<>();
    private int numSides = 4;  // 기본값, 실제 값은 controller에서 설정
    private Set<Point> specialPoints = new HashSet<>();

    public YutBoard() {
        setLayout(null);
        initializeUI();
    }

    private void initializeUI() {
        createThrowButtons();
        createManualThrowButtons();
        createEndPieceButton();
        createTurnInfoPanel();
        createResultPanel();
    }

    private void createThrowButtons() {
        throwButton = new JButton("랜덤 윷 던지기");
        throwButton.setBounds(605, 370, 360, 45);
        add(throwButton);
    }

    private void createManualThrowButtons() {
        int y = 420;
        int w = 60;
        int h = 35;

        throwBackdo = createManualThrowButton("빽도", 605, y, w, h);
        throwDo = createManualThrowButton("도", 665, y, w, h);
        throwGae = createManualThrowButton("개", 725, y, w, h);
        throwGeol = createManualThrowButton("걸", 785, y, w, h);
        throwYut = createManualThrowButton("윷", 845, y, w, h);
        throwMo = createManualThrowButton("모", 905, y, w, h);
    }

    private JButton createManualThrowButton(String text, int x, int y, int w, int h) {
        JButton button = new JButton(text);
        button.setBounds(x, y, w, h);
        add(button);
        return button;
    }

    private void createEndPieceButton() {
        endPiece = new JButton("내보내기");
        endPiece.setBounds(480, 550, 90, 40);
        endPiece.setEnabled(false);
        add(endPiece);
    }

    private void createTurnInfoPanel() {
        turnLabel = new JLabel("A님의 차례입니다.");
        turnLabel.setBounds(610, 470, 200, 30);
        add(turnLabel);

        resultLabel = new JLabel("윷 결과 🐎");
        resultLabel.setBounds(610, 500, 180, 30);
        add(resultLabel);
    }

    private void createResultPanel() {
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

    public void setSpecialPoints(Set<Point> specialUIPoints) {
        this.specialPoints = specialUIPoints;
    }

    // 결과 리스트 업데이트 메서드
    public void updateResultList(List<String> resultName) {
        resultPanel.removeAll();

        for (String name : resultName) {
            JLabel label = new JLabel(name);
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

    public void setOnThrowButton(Runnable callback) {
        throwButton.addActionListener(e -> callback.run());
    }

    public void setOnManualThrowButton(String resultName, Runnable callback) {
        JButton button = switch (resultName) {
            case "BackDo" -> throwBackdo;
            case "DO" -> throwDo;
            case "GAE" -> throwGae;
            case "GUL" -> throwGeol;
            case "YUT" -> throwYut;
            case "MO" -> throwMo;
            default -> throw new IllegalArgumentException("알 수 없는 윷 결과: " + resultName);
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

    public void updateTurnLabel(int playerId) {
        turnLabel.setText((char)('A' + playerId) + "님의 차례입니다.");
    }


    public void setNumSides(int numSides) {
        this.numSides = numSides;
    }

    public int getNumSides() { return numSides; }

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

//    public PieceButton getPieceButton(Piece piece) {
//        for (PieceButton button : this.pieceButtons) {
//            if (Arrays.equals(button.getPiece().getPosition(), piece.getPosition())) {
//                return button;
//            }
//        }
//        return null;
//    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawBoard(g);
    }

    private void drawBoard(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int size = 30;
        Point center = new Point(350, 350);
        int radius = 200;

        drawCircle(g2, center.x, center.y, size);

        BoardLayoutCalculator layout = new BoardLayoutCalculator(numSides, center, radius);
        List<Point> vertices = layout.calculateVertices();

        for (Point vertex : vertices) {
            List<Point> mids = layout.calculateIntermediatePoints(vertex, center, 3, false);
            for (Point p : mids) drawCircle(g2, p.x, p.y, size);
        }

        for (int i = 0; i < vertices.size(); i++) {
            List<Point> mids = layout.calculateIntermediatePoints(
                    vertices.get(i), vertices.get((i + 1) % vertices.size()), 5, true);
            for (Point p : mids) drawCircle(g2, p.x, p.y, size);
        }

        // 출발 텍스트 표시
        Point start = layout.findStartPoint(vertices);
        String label = "출발";
        Font font = new Font("SansSerif", Font.BOLD, 16);
        g2.setFont(font);
        FontMetrics fm = g2.getFontMetrics(font);
        int textWidth = fm.stringWidth(label);
        int textHeight = fm.getHeight();
        g2.setColor(Color.BLACK);
        g2.drawString(label, start.x - textWidth / 2, start.y + textHeight / 2 - 6);
    }

//    private void drawCircle(Graphics2D g2, int x, int y, int size) {
//        g2.drawOval(x - size / 2, y - size / 2, size, size);
//        int[] index = coordinateToIndexMap.getOrDefault(new Point(x, y), new int[]{-1, -1});
//        g2.drawString("[" + index[0] + ", " + index[1] + "]", x + size / 2, y + size / 2);
//       g2.drawString("(" + x + ", " + y + ")", x - size / 2, y - size / 2 - 5);
//    }
    private void drawCircle(Graphics2D g2, int x, int y, int size) {
        Point point = new Point(x, y);
        boolean isSpecial = specialPoints.contains(point);

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

    /* 말&그룹화된 말들 한 칸씩 이동 */
    public void animatePieceMovement(List<PieceButton> groupButtons, List<Point> path, Runnable onComplete) {
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

    public void showGetoutButton(Runnable onClick) {
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
        btn.setOutColor();
    }
}
