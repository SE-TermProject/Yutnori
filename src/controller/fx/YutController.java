package controller.fx;

import javafx.scene.control.*;
import javafx.scene.layout.*;

import app.fx.AppManager;
import model.Game;
import model.Piece;
import model.Player;
import model.YutResult;
import view.fx.YutBoard;
import view.fx.PieceButton;
import view.fx.CandidatePieceButton;

import javafx.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class YutController {
    private final AppManager appManager;
    private final Game game;
    private final YutBoard board;
    private boolean hasNonBonusYut = false;

    public YutController(AppManager appManager, int sides, int playerCount, int pieceCount, YutBoard board) {
        this.appManager = appManager;
        this.game = new Game(sides, playerCount, pieceCount);
        this.board = board;
    }

    public void initializeGameUI() {
        board.setNumSides(game.getBoard().getNumSides());
        board.setBoard(game.getBoard());

        setupThrowButtons();
        setupInitialPieceButtons();
    }

    private void updateResultPanel(List<YutResult> results) {
        List<String> names = results.stream()
                .map(YutResult::getKoreanName)
                .toList();
        board.updateResultList(names);
    }

    private void setupThrowButtons() {
        board.setOnThrowButton(() -> {
            YutResult result = game.throwYut();
            updateResultPanel(game.getYutResults());

            if (!result.isBonusTurn()) hasNonBonusYut = true;

            if (game.getYutResults().get(0) == YutResult.BackDo
                    && game.getCurrentPlayer().getPieces().stream().allMatch(p -> {
                int[] pos = p.getPosition();
                return pos.length == 0 || (pos[0] == 0 && pos[1] == 0);
            })) {
                NackedBackDo();
                return;
            }

            board.getThrowButton().setDisabled(!result.isBonusTurn());
            enableManualThrowButtons(result.isBonusTurn());
        });

        for (YutResult result : YutResult.values()) {
            String name = result.name();
            board.setOnManualThrowButton(name, () -> handleManualThrow(result));
        }
    }

    private void setupInitialPieceButtons() {
        List<PieceButton> pieceButtons = generateInitialPieceButtons();
        board.setPieceButtons(pieceButtons);
    }

    private List<PieceButton> generateInitialPieceButtons() {
        List<PieceButton> pieceButtons = new ArrayList<>();
        int startX = 630, startY = 200;
        int playerGapY = 40, pieceGapX = 30;

        for (Player player : game.getPlayers()) {
            int currentX = startX;
            PieceButton leftmostBtn = null;

            for (Piece piece : player.getPieces()) {
                PieceButton btn = new PieceButton(piece, player.getId());
                pieceToButtonMap.put(piece, btn);
                btn.setBounds(currentX, startY, 20, 20);
                btn.setPos(currentX, startY);
                btn.setDisabled(false);

                if (leftmostBtn == null) {
                    leftmostBtn = btn;
                }

                btn.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        board.getEndPiece().setDisabled(true);
                        /* 말 선택 */
                        System.out.print("Piece clicked - " );
                        if (game.getYutResults().isEmpty()) { // 윷 결과가 없다면
                            System.out.println("윷을 먼저 던져야 합니다.");
                        }
                        else if (piece.isFinished()) { // 이미 finish된 말이라면
                            System.out.println("이 pieces는 이미 종료되었습니다.");
                        }
                        else if (!canMoveNow()) {
                            System.out.println("아직 이동할 수 없습니다. 보너스 턴이 끝날 때까지 기다려야 합니다.");
                        }
                        else {
                            if (game.getYutResults().get(0) == YutResult.BackDo
                                    && game.getCurrentPlayer().getPieces().stream().filter(p -> !p.isFinished()).allMatch(p -> {
                                int[] pos = p.getPosition();
                                return pos.length == 0 || (pos[0] == 0 && pos[1] == 0);
                            })) {
                                NackedBackDo();
                            }
                            if(game.getCurrentPlayer().getPieces().contains(piece)) { // 현재 차례인 사용자의 말이라면
                                System.out.println("말이 선택되었습니다.");

                                board.getThrowButton().setDisabled(true);

                                // 이동 가능 위치 버튼 생성 및 표시
                                List<CandidatePieceButton> previewButtons = generatePossiblePieceButtons(piece);
                                board.showCandidateButtons(previewButtons);

                                // 내보내기가 가능할 때, 버튼 켜기
                                if (possibleGetout(piece)) {
                                    YutResult useYut = getYutResult(piece);

                                    board.showGetoutButton(() -> {
                                        System.out.println(useYut + " 으로 나가기 가능"+ "\n");

                                        handleGetoutButtonClick(btn);
                                        board.deletePieceButton(previewButtons);
                                        btn.getPiece().setFinished(true);
                                        game.getYutResults().remove(useYut);

                                        if (game.checkWin()) {
                                            btn.getPiece().removeGroupedPiece();
                                            System.out.println("현재 플레이어가 모든 말을 도착시켰습니다! 승리!");
                                            String winnerName = "플레이어 " + (char) ('A' + game.getCurrentPlayerIndex());
                                            int choice = board.showGameOverDialog(winnerName);
                                            Utilities.getWindowAncestor(board).dispose(); // 현재 게임 창 닫기

                                            if (choice == OptionPane.YES_OPTION) {
                                                appManager.restartGame();  // 다시 시작
                                            } else {
                                                appManager.exitGame(); // 완전 종료
                                            }
                                        }

                                        if (game.getYutResults().isEmpty()) {
                                            game.nextTurn();
                                            board.updateTurnLabel(game.getCurrentPlayer().getId());
                                            hasNonBonusYut = false;
                                            enableManualThrowButtons(true);
                                            board.getThrowButton().setDisabled(false);
                                            updateResultPanel(game.getYutResults());
                                        } else {
                                            updateResultPanel(game.getYutResults());
                                        }
                                        btn.getPiece().removeGroupedPiece();
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

            if (leftmostBtn != null) {
                board.addPlayerLabel(player.getId(), leftmostBtn.getX() - 20, leftmostBtn.getY());
            }
            startY += playerGapY;
        }
        return pieceButtons;
    }
}
