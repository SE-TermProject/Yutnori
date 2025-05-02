package view;

import controller.YutController;
import model.YutModel;
import view.YutBoardV2;

import javax.swing.*;
import java.awt.*;

public class GameSetupView extends JFrame {

    public GameSetupView() {
        setTitle("게임 설정");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(5, 1, 10, 10));

        // 1. 판 모양 선택 (4~6각형)
        Integer[] shapes = {4, 5, 6};
        JComboBox<Integer> shapeBox = new JComboBox<>(shapes);
        JPanel shapePanel = new JPanel();
        shapePanel.add(new JLabel("판의 변 개수: "));
        shapePanel.add(shapeBox);
        add(shapePanel);

        // 2. 플레이어 수 (2~4명)
        Integer[] players = {2, 3, 4};
        JComboBox<Integer> playerBox = new JComboBox<>(players);
        JPanel playerPanel = new JPanel();
        playerPanel.add(new JLabel("플레이어 수: "));
        playerPanel.add(playerBox);
        add(playerPanel);

        // 3. 말 개수 (2~5개)
        Integer[] pieces = {2, 3, 4, 5};
        JComboBox<Integer> pieceBox = new JComboBox<>(pieces);
        JPanel piecePanel = new JPanel();
        piecePanel.add(new JLabel("플레이어당 말 개수: "));
        piecePanel.add(pieceBox);
        add(piecePanel);

        // 4. 시작 버튼
        JButton startBtn = new JButton("게임 시작!");
        add(startBtn);

        startBtn.addActionListener(e -> {
            int sides = (Integer) shapeBox.getSelectedItem();
            int playerCount = (Integer) playerBox.getSelectedItem();
            int pieceCount = (Integer) pieceBox.getSelectedItem();

            // 설정 확인용 로그
            System.out.println("Sides: " + sides + ", Players: " + playerCount + ", Pieces: " + pieceCount);

            // 실제 게임 화면으로 이동
            JFrame gameFrame = new JFrame("YutNori");
            gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            gameFrame.setSize(700, 700);
            YutBoardV2 board = new YutBoardV2(sides, playerCount, pieceCount);
            gameFrame.add(board);

            // 모델 + 컨트롤러 연결
            YutModel model = new YutModel();
            new YutController(model, board);

            gameFrame.setVisible(true);
            this.dispose(); // 설정창 닫기
        });

        setVisible(true);
    }
}