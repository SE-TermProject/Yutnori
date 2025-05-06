package controller;

import model.Game;
import view.YutBoardV2;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class YutController {
    private final Game game;

    public YutController(int sides, int playerCount, int pieceCount, YutBoardV2 board) {
        this.game = new Game(sides, playerCount, pieceCount);
        board.setGame(game);

        // Frame 생성 및 view 연결 & 실제 게임 화면으로 이동
        JFrame gameFrame = new JFrame("YutNori");
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setSize(700, 700);
        gameFrame.add(board);
        gameFrame.setVisible(true);

        board.getThrowButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String result = game.throwYut();
                board.updateResult(result);
            }
        });
    }
}