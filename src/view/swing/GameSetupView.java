package view.swing;

import app.swing.AppManager;

import javax.swing.*;
import java.awt.*;

public class GameSetupView extends JFrame {
    private final AppManager appManager;

    private JComboBox<Integer> shapeBox;
    private JComboBox<Integer> playerBox;
    private JComboBox<Integer> pieceBox;

    public GameSetupView(AppManager appManager) {
        this.appManager = appManager;

        setupFrame();
        setupComponents();
        setupStartButton();
        setVisible(true);
    }

    private void setupFrame() {
        setTitle("게임 설정");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    private void setupComponents() {
        // 상단 여백 패널
        JPanel topMargin = new JPanel();
        topMargin.setPreferredSize(new Dimension(0, 10));
        add(topMargin, BorderLayout.NORTH);

        // 중앙 설정 패널
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // 1. 판 모양 선택 (4~6각형)
        shapeBox = new JComboBox<>(new Integer[]{4, 5, 6});
        JPanel shapePanel = new JPanel();
        shapePanel.add(new JLabel("판의 변 개수: "));
        shapePanel.add(shapeBox);
        centerPanel.add(shapePanel);

        // 2. 플레이어 수 (2~4명)
        playerBox = new JComboBox<>(new Integer[]{2, 3, 4});
        JPanel playerPanel = new JPanel();
        playerPanel.add(new JLabel("플레이어 수: "));
        playerPanel.add(playerBox);
        centerPanel.add(playerPanel);

        // 3. 말 개수 (2~5개)
        pieceBox = new JComboBox<>(new Integer[]{2, 3, 4, 5});
        JPanel piecePanel = new JPanel();
        piecePanel.add(new JLabel("플레이어당 말 개수: "));
        piecePanel.add(pieceBox);
        centerPanel.add(piecePanel);

        // 간격 조절용 여백
        centerPanel.add(Box.createVerticalStrut(10));

        // 전체 중앙 패널 추가
        add(centerPanel, BorderLayout.CENTER);
    }

    private void setupStartButton() {
        // 4. 시작 버튼
        JButton startBtn = new JButton("게임 시작!");
        startBtn.setPreferredSize(new Dimension(200, 50));
        JPanel startPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        startPanel.add(startBtn);

        ((JPanel) getContentPane().getComponent(1)).add(startPanel);  // centerPanel에 추가

        startBtn.addActionListener(e -> {
            int sides = (Integer) shapeBox.getSelectedItem();
            int playerCount = (Integer) playerBox.getSelectedItem();
            int pieceCount = (Integer) pieceBox.getSelectedItem();

            // 설정 확인용 로그
            System.out.println("Sides: " + sides + ", Players: " + playerCount + ", Pieces: " + pieceCount);
            appManager.startGame(sides, playerCount, pieceCount);
            this.dispose(); // 설정창 닫기
        });
    }
}