package controller;

import model.Game;
import model.Piece;
import model.Player;
import model.YutResult;
import view.PieceButton;
import view.YutBoardV2;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class YutController {
    private final Game game;

    public YutController(int sides, int playerCount, int pieceCount, YutBoardV2 board) {
        this.game = new Game(sides, playerCount, pieceCount);

        board.setNumSides(game.getBoard().getNumSides());
        board.setBoard(game.getBoard());

        List<PieceButton> pieceButtons = generateInitialPieceButtons();
        board.setPieceButtons(pieceButtons);

        // Frame 생성 및 view 연결 & 실제 게임 화면으로 이동
        JFrame gameFrame = new JFrame("YutNori");
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setSize(700, 700);
        gameFrame.add(board);
        gameFrame.setVisible(true);

        board.getThrowButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                YutResult result = game.throwYut();
                board.updateResult(result);
            }
        });
    }

    private List<PieceButton> generateInitialPieceButtons() {
        List<PieceButton> pieceButtons = new ArrayList<>();
        int startX = 600, startY = 200;
        int playerGapY = 60, pieceGapX = 30;

        for (Player player : game.getPlayers()) {
            int currentX = startX;
            for (Piece piece : player.getPieces()) {
                PieceButton btn = new PieceButton(piece, player.getId());
                btn.setBounds(currentX, startY, 20, 20);
                btn.setEnabled(true);
                btn.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        /* 말 선택 */
                        System.out.print("Piece clicked - " );
                        if (game.getYutResults().isEmpty()) { // 윷 결과가 없다면
                            System.out.println("윷을 먼저 던져야 합니다.");
                        }
                        else if (piece.isFinished()) { // 이미 finish된 말이라면
                            System.out.println("이 pieces는 이미 종료되었습니다.");
                        }
                        else {
                            if(game.getCurrentPlayer().getPieces().contains(piece)) { // 현재 차례인 사용자의 말이라면
                                System.out.println("말이 선택되었습니다.");

                            }
                            else System.out.println("현재 사용자의 말이 아닙니다.");
                        }
                    }
                });
                pieceButtons.add(btn);
                currentX += pieceGapX;
            }
            startY += playerGapY;
        }
        return pieceButtons;
    }
}