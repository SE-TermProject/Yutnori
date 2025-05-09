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

public class YutController {
    private final Game game;
    private final YutBoardV2 board;

    public YutController(int sides, int playerCount, int pieceCount, YutBoardV2 board) {
        this.game = new Game(sides, playerCount, pieceCount);
        this.board = board;
    }

    public void initializeGameUI() {
        board.setNumSides(game.getBoard().getNumSides());
        board.setBoard(game.getBoard());

        setupThrowButtons();
        setupInitialPieceButtons();
        setupFrame();
    }

    private void setupThrowButtons() {
        // 랜덤 윷 던지기
        board.getThrowButton().addActionListener(e -> {
            if (!game.getYutResults().isEmpty()) return;

            YutResult result = game.throwYut();
            board.updateResultList(game.getYutResults());

            if (game.getYutResults().get(0) == YutResult.BackDo
                    && game.getCurrentPlayer().getPieces().stream().allMatch(p -> {
                int[] pos = p.getPosition();
                return pos.length == 0 || (pos[0] == 0 && pos[1] == 0);
            })) {
                NackedBackDo();
                return;
            }

            // 보너스 턴이면 버튼 유지
            if (result.isBonusTurn()) {
                board.getThrowButton().setEnabled(true);
                enableManualThrowButtons(true);
            } else {
                board.getThrowButton().setEnabled(false);
                enableManualThrowButtons(false);
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

    private void setupInitialPieceButtons() {
        List<PieceButton> pieceButtons = generateInitialPieceButtons();
        board.setPieceButtons(pieceButtons);
    }

    private void setupFrame() {
        // Frame 생성 및 view 연결 & 실제 게임 화면으로 이동
        JFrame gameFrame = new JFrame("YutNori");
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setSize(1000, 700);
        gameFrame.add(board);
        gameFrame.setVisible(true);
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
                        else if (isLastResultBonusTurn()) {
                            System.out.println("마지막 결과가 보너스 턴(YUT 또는 MO)이므로, 말을 이동할 수 없습니다.");
                        }
                        else if (piece.isFinished()) { // 이미 finish된 말이라면
                            System.out.println("이 pieces는 이미 종료되었습니다.");
                        }
                        else {
                            if (game.getYutResults().get(0) == YutResult.BackDo
                                    && game.getCurrentPlayer().getPieces().stream().allMatch(p -> {
                                int[] pos = p.getPosition();
                                return pos.length == 0 || (pos[0] == 0 && pos[1] == 0);
                            })) {
                                NackedBackDo();
                            }
                            if(game.getCurrentPlayer().getPieces().contains(piece)) { // 현재 차례인 사용자의 말이라면
                                System.out.println("말이 선택되었습니다.");

                                // 이동 가능 위치 버튼 생성 및 표시
                                List<CandidatePieceButton> previewButtons = generatePossiblePieceButtons(piece);
                                board.setPossiblePieceButtons(previewButtons);

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
        board.updateResultList(game.getYutResults());

        if (game.getYutResults().get(0) == YutResult.BackDo
                && game.getCurrentPlayer().getPieces().stream().allMatch(p -> {
            int[] pos = p.getPosition();
            return pos.length == 0 || (pos[0] == 0 && pos[1] == 0);
        })) {
            NackedBackDo();
            return;
        }

        // 보너스 턴일 경우 버튼 다시 활성화
        if (result.isBonusTurn()) {
            board.getThrowButton().setEnabled(true);
            enableManualThrowButtons(true);
        } else {
            board.getThrowButton().setEnabled(false);
            enableManualThrowButtons(false);
        }
    }

    /* 해당 말이 이동할 수 있는 모든 위치에 놓일 버튼 */
    private List<CandidatePieceButton> generatePossiblePieceButtons(Piece selectedPiece) {
        List<CandidatePieceButton> possiblePosButtons = new ArrayList<>();
        HashMap<Piece, List<int[]>> currentPossiblePos = game.findCurrentPossiblePos();
        List<int[]> piecePossiblePos = currentPossiblePos.get(selectedPiece); // 선택된 말이 이동할 수 있는 모든 경로의 position

        for (int i = 0; i < piecePossiblePos.size(); i++) {
            int[] pos = piecePossiblePos.get(i);
            Point point = game.getBoard().indexToPoint(pos);

            int[] prePos = selectedPiece.getPosition(); int index = i;
            if (prePos.length == 0) prePos = new int[]{0, 0};
            if (prePos[0] == 0 && prePos[1] % 5 == 0 && prePos[1] > 0 && prePos[1] / 5 <= game.getBoard().getNumSides() - 2) { // 가장자리 꼭짓점 위치라면
                // 각 이동 가능한 칸의 수가 두개씩이므로 인덱스 수정
                index = i / 2;
            }
            CandidatePieceButton btn = new CandidatePieceButton(pos, game.getCurrentPlayerIndex(), game.getYutResults().get(index));
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
                    if (game.getYutResults().isEmpty()) return;

                    CandidatePieceButton destinationBtn = (CandidatePieceButton) e.getSource();
                    int[] to = destinationBtn.getPosition();  // 도착 지점의 index

                    /* 말 이동 로직 */
                    List<Point> piecePath = game.getBoard().calculatePath(from, to);
                    board.deletePieceButton(possiblePosButtons);  // 모든 이동 가능한 경로에 있던 버튼 제거
                    board.animatePieceMovement(selectedPiece, piecePath);  // 말 실제 이동
                    selectedPiece.getPiece().setPosition(destinationBtn.getPosition());

                    System.out.println(btn.getYutResult() + "으로 이동 후 말의 위치: [" + selectedPiece.getPosition()[0] + ", " + selectedPiece.getPosition()[1] + "]");
                    game.consumeResult(btn.getYutResult());
                    board.updateResultList(game.getYutResults());

                    if (!game.hasRemainingMoves()) {
                        if (!game.getYutResults().isEmpty() &&
                                game.getYutResults().get(game.getYutResults().size() - 1).isBonusTurn()) {
                            board.getThrowButton().setEnabled(true);
                            enableManualThrowButtons(true);
                        } else {
                            game.nextTurn();
                            board.updateTurnLabel(game.getCurrentPlayer().getId());
                            board.getThrowButton().setEnabled(true);
                            enableManualThrowButtons(true);
                        }
                    } else {
                        board.getThrowButton().setEnabled(false);
                        enableManualThrowButtons(false);
                    }
                }
            });
        }
    }

    private void enableManualThrowButtons(boolean enabled) {
        board.getThrowBackdo().setEnabled(enabled);
        board.getThrowDo().setEnabled(enabled);
        board.getThrowGae().setEnabled(enabled);
        board.getThrowGeol().setEnabled(enabled);
        board.getThrowYut().setEnabled(enabled);
        board.getThrowMo().setEnabled(enabled);
    }

    /* 만약 현재 플레이어의 모든 말이 윷놀이 판으로 나가지 않았고, 그 상황에서 빽도만 나왔다면 예외 처리 */
    private void NackedBackDo() {
        JOptionPane.showMessageDialog(null, "모든 말이 판에 올라가지 않았고, 빽도가 나와 낙 처리됩니다.", "낙 발생", JOptionPane.WARNING_MESSAGE);

        System.out.println("빽도 나옴 -> 낙 처리");
        game.consumeResult(YutResult.BackDo);
        board.updateResultList(game.getYutResults());

        game.nextTurn();
        board.updateTurnLabel(game.getCurrentPlayer().getId());
        board.getThrowButton().setEnabled(true);
        enableManualThrowButtons(true);
    }

    private boolean isLastResultBonusTurn() {
        List<YutResult> results = game.getYutResults();
        if (results.isEmpty()) return false;
        return results.get(results.size() - 1).isBonusTurn();
    }
}