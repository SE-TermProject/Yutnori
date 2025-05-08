package controller;

import model.Game;
import model.Piece;
import model.Player;
import model.YutResult;
import view.CandidatePieceButton;
import view.PieceButton;
import view.YutBoardV2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YutController {
    private final Game game;
    private final YutBoardV2 board;

    public YutController(int sides, int playerCount, int pieceCount, YutBoardV2 board) {
        this.game = new Game(sides, playerCount, pieceCount);
        this.board = board;

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

        // 랜덤 윷 던지기 버튼
        board.getThrowButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                YutResult result = game.throwYut();
                board.updateResult(String.valueOf(result));
            }
        });

        // 지정 윷 던지기 버튼
        board.getThrowBackdo().addActionListener(e -> handleManualThrow(YutResult.BackDo));
        board.getThrowDo().addActionListener(e -> handleManualThrow(YutResult.DO));
        board.getThrowGae().addActionListener(e -> handleManualThrow(YutResult.GAE));
        board.getThrowGeol().addActionListener(e -> handleManualThrow(YutResult.GUL));
        board.getThrowYut().addActionListener(e -> handleManualThrow(YutResult.YUT));
        board.getThrowMo().addActionListener(e -> handleManualThrow(YutResult.MO));

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
                        else if (piece.isFinished(board.getNumSides())) { // 이미 finish된 말이라면
                            System.out.println("이 pieces는 이미 종료되었습니다.");
                        }
                        else {
                            if(game.getCurrentPlayer().getPieces().contains(piece)) { // 현재 차례인 사용자의 말이라면
                                System.out.println("말이 선택되었습니다.");

                                // 이동 가능 위치 버튼 생성 및 표시
                                List<CandidatePieceButton> previewButtons = generatePossiblePieceButtons(piece);
                                board.setPossiblePieceButtons(previewButtons);

                                // 내보내기가 가능할 때, 버튼 생성
                                if(possibleGetout(piece)) {
                                    JButton Getout = board.getEndPiece();
                                    Getout.setEnabled(true);
                                    Getout.addActionListener(new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            handleGetoutButtonClick(btn);
                                        }
                                    });

                                }
                              
                                // 버튼 선택 후 실제 이동
                                movePiece(btn, previewButtons);
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


    // 지정 윷 결과 처리 메서드
    private void handleManualThrow(YutResult result) {
        game.setManualYutResult(result);
        board.updateResult(result.toString());
    }

    /* 해당 말이 이동할 수 있는 모든 위치에 놓일 버튼 */
    private List<CandidatePieceButton> generatePossiblePieceButtons(Piece selectedPiece) {
        List<CandidatePieceButton> possiblePosButtons = new ArrayList<>();
        HashMap<Piece, List<int[]>> currentPossiblePos = game.findCurrentPossiblePos();
        List<int[]> piecePossiblePos = currentPossiblePos.get(selectedPiece); // 선택된 말이 이동할 수 있는 모든 경로의 position

        for (int[] pos : piecePossiblePos) {
            Point point = game.getBoard().indexToPoint(pos);

            CandidatePieceButton btn = new CandidatePieceButton(pos, game.getCurrentPlayerIndex());
            btn.setBounds(point.x, point.y, 20, 20);
            btn.setPixelPosition(point);
            btn.setEnabled(true);
            possiblePosButtons.add(btn);
        }
        return possiblePosButtons;
    }

    private void movePiece(PieceButton selectedPiece, List<CandidatePieceButton> possiblePosButtons) {
        int[] from;
        if (selectedPiece.getPosition().length == 0) {
            // 아직 말이 출발하지 않은 상태일 경우
            from = new int[] {0, 0};
        } else {
            from = selectedPiece.getPosition();  // 출발 지점의 index
        }
        System.out.println("말의 출발 지점: [" + from[0] + ", " + from[1] + "]");

        for (CandidatePieceButton btn : possiblePosButtons) {
            btn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    CandidatePieceButton destinationBtn = (CandidatePieceButton) e.getSource();
                    int[] to = destinationBtn.getPosition();  // 도착 지점의 index

                    /* 말 이동 로직 */
                    List<Point> piecePath = game.getBoard().calculatePath(from, to);
                    board.deletePieceButton(possiblePosButtons);  // 모든 이동 가능한 경로에 있던 버튼 제거
                    board.animatePieceMovement(selectedPiece, piecePath);  // 말 실제 이동
                    selectedPiece.getPiece().setPosition(destinationBtn.getPosition());

                    System.out.println("이동 후 말의 위치: [" + selectedPiece.getPosition()[0] + ", " + selectedPiece.getPosition()[1] + "]");
                    board.deletePieceButton(possiblePosButtons);
                }
            });
        }
    }

    private boolean possibleGetout(Piece selectedPiece) {
        int numSides = game.getBoard().getNumSides();

        HashMap<Piece, List<int[]>> currentPossiblePos = game.findCurrentPossiblePos();

        for(Map.Entry<Piece, List<int[]>> entry : currentPossiblePos.entrySet()) {
            Piece piece = entry.getKey();
            List<int[]> positions = entry.getValue();

            for(int[] pos : positions) {  // 입력된 piece의 예상 위치가 내보내기가 가능한 위치인 경우
                piece.setPosition(pos);
                if(piece.isFinished(numSides)){
                    return true;
                };
            }
        }
        return false;
    }

    private void handleGetoutButtonClick(PieceButton btn) {

    }
}