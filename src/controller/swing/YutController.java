package controller.swing;

import app.swing.AppManager;
import model.*;
import view.swing.CandidatePieceButton;
import view.swing.PieceButton;
import view.swing.YutBoard;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.*;
import java.util.List;

public class YutController {
    private final AppManager appManager;
    private final Game game;
    private final YutBoard board;
    private boolean hasNonBonusYut = false; // 일반 윷이 한 번이라도 나왔는지 추적
    private final Map<Piece, PieceButton> pieceToButtonMap = new HashMap<>();

    public YutController(AppManager appManager, int sides, int playerCount, int pieceCount, YutBoard board) {
        this.appManager = appManager;
        this.game = new Game(sides, playerCount, pieceCount);
        this.board = board;
    }

    private void initializeGameUI() {
        board.setNumSides(game.getBoard().getNumSides());
        board.setBoard(game.getBoard());

        setupThrowButtons();
        setupInitialPieceButtons();
        board.setupFrame();
    }

    private void setupThrowButtons() {
        board.setOnThrowButton(() -> {
            YutResult result = game.throwYut();
            board.updateResultList(game.getYutResults());

            if (!result.isBonusTurn()) hasNonBonusYut = true;

            if (game.getYutResults().get(0) == YutResult.BackDo
                    && game.getCurrentPlayer().getPieces().stream().allMatch(p -> {
                int[] pos = p.getPosition();
                return pos.length == 0 || (pos[0] == 0 && pos[1] == 0);
            })) {
                NackedBackDo();
                return;
            }

            board.getThrowButton().setEnabled(result.isBonusTurn());
            enableManualThrowButtons(result.isBonusTurn());
        });

        for (YutResult result : YutResult.values()) {
            board.setOnManualThrowButton(result, () -> handleManualThrow(result));
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
                btn.setEnabled(true);

                if (leftmostBtn == null) {
                    leftmostBtn = btn;
                }

                btn.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        board.getEndPiece().setEnabled(false);
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

                                board.getThrowButton().setEnabled(false);

                                // 이동 가능 위치 버튼 생성 및 표시
                                List<CandidatePieceButton> previewButtons = generatePossiblePieceButtons(piece);
                                board.setPossiblePieceButtons(previewButtons);

                                // 내보내기가 가능할 때, 버튼 켜기
                                if (possibleGetout(piece)) {
                                    YutResult useYut = getYutResult(piece);

                                    board.showGetoutButton(useYut, () -> {
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
                                            SwingUtilities.getWindowAncestor(board).dispose(); // 현재 게임 창 닫기

                                            if (choice == JOptionPane.YES_OPTION) {
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
                                            board.getThrowButton().setEnabled(true);
                                            board.updateResultList(game.getYutResults());
                                        } else {
                                            board.updateResultList(game.getYutResults());
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

    // 지정 윷 결과 처리 메서드
    private void handleManualThrow(YutResult result) {
        game.setManualYutResult(result);
        board.updateResultList(game.getYutResults());

        if (!result.isBonusTurn()) {
            hasNonBonusYut = true;
        }

        if (game.getYutResults().get(0) == YutResult.BackDo
                && game.getCurrentPlayer().getPieces().stream().filter(p -> !p.isFinished()).allMatch(p -> {
            int[] pos = p.getPosition();
            return pos.length == 0 || (pos[0] == 0 && pos[1] == 0);
        })) {
            NackedBackDo();
            return;
        }

        // 보너스 턴일 경우 버튼 다시 활성화
        board.setThrowButtonsEnabled(result.isBonusTurn());
    }

    /* 해당 말이 이동할 수 있는 모든 위치에 놓일 버튼 */
    private List<CandidatePieceButton> generatePossiblePieceButtons(Piece selectedPiece) {
        List<CandidatePieceButton> possiblePosButtons = new ArrayList<>();
        HashMap<Piece, HashMap<YutResult, List<int[]>>> currentPossiblePos = game.findCurrentPossiblePos();
        HashMap<YutResult, List<int[]>> piecePossiblePos = currentPossiblePos.get(selectedPiece); // 선택된 말이 이동할 수 있는 모든 경로의 position

        List<YutResult> results = game.getYutResults();
        for (YutResult yutResult : results) {
            for (int[] pos : piecePossiblePos.get(yutResult)) {
                BoardPoint point = game.getBoard().indexToPoint(pos);

                CandidatePieceButton btn = new CandidatePieceButton(pos, game.getCurrentPlayerIndex(), yutResult);
                btn.setPixelPosition(point);
                btn.setEnabled(true);
                possiblePosButtons.add(btn);
            }
        }
        return possiblePosButtons;
    }

    public void movePiece(PieceButton selectedPiece, List<CandidatePieceButton> possiblePosButtons) {
        int[] from;
        if (selectedPiece.getPiece().getPosition().length == 0) {
            // 아직 말이 출발하지 않은 상태일 경우
            from = new int[] {0, 0};
        } else {
            from = selectedPiece.getPiece().getPosition();  // 출발 지점의 index
        }
        System.out.println("말의 출발 지점: [" + from[0] + ", " + from[1] + "]");

        board.showCandidateButtons(possiblePosButtons, destinationBtn -> {
            board.getEndPiece().setEnabled(false);
            if (game.getYutResults().isEmpty()) return;

            board.deletePieceButton(possiblePosButtons);  // 버튼 제거

            int[] to = destinationBtn.getPosition(game.getBoard().getNumSides()); // 도착 지점의 index
            destinationBtn.setPosition(to);
            /* 말 이동 로직 */
            List<BoardPoint> piecePath = game.getBoard().calculatePath(from, to, destinationBtn.getYutResult());

            performMove(selectedPiece, possiblePosButtons, destinationBtn, from, piecePath, destinationBtn);
        });
    }

    private void performMove(PieceButton selectedPiece, List<CandidatePieceButton> possiblePosButtons, CandidatePieceButton destinationBtn, int[] from, List<BoardPoint> piecePath, CandidatePieceButton btn) {
        board.deletePieceButton(possiblePosButtons);  // 모든 이동 가능한 경로에 있던 버튼 제거

        // 이동 후 실행할 공통 로직 정의
        Runnable onComplete = () -> {
            selectedPiece.getPiece().setPosition(destinationBtn.getPosition());
            int[] finalTo = selectedPiece.getPosition();

            // 이동한 말이 그룹이면 전체 위치 기록
            if (selectedPiece.getPiece().isGrouped()) {
                System.out.println("그룹화된 말들 인덱스 변경!");
                List<Piece> group = selectedPiece.getPiece().getPieceGroup();

                System.out.println(group);
                for (Piece piece : group) {
                    piece.setPosition(finalTo);
                    piece.recordPrePositions(
                            game.getBoard().getNumSides(),
                            new int[]{from[0], from[1]},
                            new int[]{finalTo[0], finalTo[1]},
                            destinationBtn.getYutResult()
                    );
                    System.out.println("그룹화된 말 -> " + piece.getPosition()[0] + " " + piece.getPosition()[1]);
                }
            } else {
                System.out.println("말 한개의 인덱스 변경!");
                selectedPiece.getPiece().recordPrePositions(
                        game.getBoard().getNumSides(),
                        new int[]{from[0], from[1]},
                        new int[]{finalTo[0], finalTo[1]},
                        destinationBtn.getYutResult()
                );
            }

            System.out.println(btn.getYutResult() + "으로 이동 후 말의 위치: [" + finalTo[0] + ", " + finalTo[1] + "]");
            handleAfterMove(btn, selectedPiece);
        };

        if (selectedPiece.getPiece().isGrouped()) {
            List<PieceButton> groupButtons = new ArrayList<>();
            groupButtons.add(selectedPiece);

            for (Piece grouped : selectedPiece.getPiece().getPieceGroup()) {
                PieceButton groupedBtn = pieceToButtonMap.get(grouped);
                if (groupedBtn != null) {
                    groupButtons.add(groupedBtn);
                }
            }

            board.animateGroupedMovement(groupButtons, piecePath, onComplete);
        } else {
            board.animatePieceMovement(selectedPiece, piecePath, onComplete);
        }
    }

    private void handleAfterMove(CandidatePieceButton selectedBtn, PieceButton selectedPiece) {
        Player currentPlayer = game.getCurrentPlayer();
        boolean catchPieces = false;

        game.consumeResult(selectedBtn.getYutResult());
        board.updateResultList(game.getYutResults());

        System.out.println("=== 현재 모든 말의 위치와 소유자 ===");
        for (Player player : game.getPlayers()) {
            for (Piece p : player.getPieces()) {
                String posStr = Arrays.toString(p.getPosition());
                String ownerStr = (p.getOwner() != null) ? String.valueOf(p.getOwner().getId()) : "null";
                System.out.println("말 위치: " + posStr + ", 소유자: " + ownerStr);
            }
        }

        List<Piece> groupedPiece = new ArrayList<>();
        groupedPiece.add(selectedPiece.getPiece());

        for (Player player : game.getPlayers()) {
            for (Piece otherPiece : player.getPieces()) {
                if (isGroupedOrCatched(otherPiece, selectedPiece)) {
                    if (otherPiece.getOwner().getId() == currentPlayer.getId()) {
                        System.out.println("자기 팀의 말을 업습니다.");
                        groupedPiece.add(otherPiece);
                        //그룹에 말이 추가된 후, 해당 PieceButton을 다시 그리도록 요청
                        PieceButton pieceButton = board.getPieceButton(selectedPiece.getPiece());
                        if (pieceButton != null) {
                            pieceButton.repaint();  // PieceButton을 다시 그려서 그룹 크기를 업데이트
                        }
                    } else {
                        System.out.println("상대 팀의 말을 잡습니다.");

                        if (otherPiece.isGrouped() && !otherPiece.getPieceGroup().isEmpty()) {
                            List<Piece> group = new ArrayList<>(otherPiece.getPieceGroup());
                            for (Piece grouped : group) {
                                System.out.println("그룹화 풀기");
                                grouped.removeGroupedPiece();
                                grouped.resetPosition();
                                board.updatePiecePosition(pieceToButtonMap.get(grouped));
                            }
                            game.getBoard().catchPiece(group);
                        } else {
                            otherPiece.resetPosition();
                            board.updatePiecePosition(pieceToButtonMap.get(otherPiece));
                            game.getBoard().catchPiece(otherPiece);
                        }
                        board.showMessageDialog("타 플레이어의 말을 잡았네요! 윷을 한 번 더 던지세요!", "타 플레이어의 말 잡기");
                        catchPieces = true;
                    }
                }
            }
            if (groupedPiece.size() >= 2) {  // 업힌 말이 있다면
                for (Piece piece : groupedPiece) {
                    piece.setPieceGroup(groupedPiece);
                    piece.setGrouped(true);
                }
            }
        }
        if (catchPieces) {
            // 윷 한 번 더 던지기
            System.out.println("말을 잡아 윷을 한 번 더 던질 수 있습니다!");
            board.getThrowButton().setEnabled(true);  // 윷 던지기 버튼 활성화
            enableManualThrowButtons(true);   // 수동 윷 버튼들 활성화
            hasNonBonusYut = false;
        } else {
            System.out.println(selectedBtn.getYutResult() + "으로 이동 후 말의 위치: [" + selectedPiece.getPiece().getPosition()[0] + ", " + selectedPiece.getPiece().getPosition()[1] + "]");

            if (!game.hasRemainingMoves()) {
                if (!game.getYutResults().isEmpty() && game.getYutResults().get(game.getYutResults().size() - 1).isBonusTurn()) {
                    board.getThrowButton().setEnabled(true);
                    enableManualThrowButtons(true);
                } else {
                    game.nextTurn();
                    board.updateTurnLabel(game.getCurrentPlayer().getId());
                    board.getThrowButton().setEnabled(true);
                    enableManualThrowButtons(true);
                    hasNonBonusYut = false; // 턴 종료 시 초기화
                }
            } else {
                board.getThrowButton().setEnabled(false);
                enableManualThrowButtons(false);
            }
        }
    }

    private boolean isGroupedOrCatched(Piece otherPiece, PieceButton selectedPiece) {
        // 둘 중 하나 이상이 이미 끝난 말이라면 false
        if (otherPiece.isFinished() || selectedPiece.getPiece().isFinished()) return false;

        int numSides = game.getBoard().getNumSides();
        int[] currentPosition = selectedPiece.getPiece().getPosition();
        int[] otherPosition = otherPiece.getPosition();

        // 비어있는 위치는 비교하지 않음
        if (currentPosition.length < 2 || otherPosition.length < 2) return false;

        BoardPoint current = new BoardPoint(currentPosition[0], currentPosition[1]);
        BoardPoint other = new BoardPoint(otherPosition[0], otherPosition[1]);

        Set<BoardPoint> centerPoint = Set.of(  // 중심점 인덱스
                new BoardPoint(1, 8),
                new BoardPoint(2, 13),
                new BoardPoint(3, 18)
        );
        Set<BoardPoint> destinationPoint = switch (numSides) { // 도착지점 인덱스
            case 4 -> Set.of(new BoardPoint(0, 0), new BoardPoint(0, 20), new BoardPoint(2, 16));
            case 5 -> Set.of(new BoardPoint(0, 0), new BoardPoint(0, 25), new BoardPoint(2, 16));
            case 6 -> Set.of(new BoardPoint(0, 0), new BoardPoint(0, 30), new BoardPoint(3, 21));
            default -> Set.of();
        };
        Set<BoardPoint> finalCornerPoint = Set.of(  // 마지막 점
                new BoardPoint(0, 5 * (numSides - 1)),
                new BoardPoint(numSides / 3, (numSides / 2) * 5 + 1)
        );

        // 1. 두 개가 동일한 piece가 아니어야 함
        if (otherPiece == selectedPiece.getPiece()) return false;

        // 2. 둘의 위치가 동일하다면 true
        if (current.equals(other)) return true;

        // 3. 둘의 위치가 다를 경우
        // 3-1. currentPosition이 중심점이라면
        if (centerPoint.contains(current) && centerPoint.contains(other)) return true;
        // 3-2. currentPosition이 도착지점이라면
        if (destinationPoint.contains(current) && destinationPoint.contains(other)) return true;
        // 3-3. currentPosition이 마지막 코너의 점이라면
        if (finalCornerPoint.contains(current) && finalCornerPoint.contains(other)) return true;

        return false;
    }

    private boolean possibleGetout(Piece selectedPiece) {
        int numSides = game.getBoard().getNumSides();
        boolean possibleOut = false;
        List<YutResult> yutResults = game.getYutResults();

        for (YutResult result: yutResults) {
            possibleOut = selectedPiece.isFinished(numSides, result.getStep());
            // 내보낼 수 있는 경우가 존재하면 내보내기 버튼 생성
            if(possibleOut){
                break;
            }
        }
        return possibleOut;
    }

    private YutResult getYutResult(Piece selectedPiece) {
        game.sortResults();
        YutResult yutResult = null;
        int numSides = game.getBoard().getNumSides();
        int length = game.getYutResults().size();
        for(int i = 0; i < length; i++){

            if(selectedPiece.isFinished(numSides, game.getYutResults().get(i).getStep())){
                yutResult = game.getYutResults().get(i);
                break;
            }
        }
        return yutResult;
    }

    private void handleGetoutButtonClick(PieceButton btn) {
        List<Piece> groupedPieces = btn.getPiece().getPieceGroup();
        if(groupedPieces.size() == 0) {
            btn.getPiece().setFinished(true);
            btn.setBounds(btn.getPos()[0], btn.getPos()[1], 20, 20);
            btn.setOutColor();
            return;
        }
        for (int i = 0; i <  groupedPieces.size(); i++) {
            PieceButton _btn = pieceToButtonMap.get(groupedPieces.get(i));
            _btn.getPiece().setFinished(true);
            _btn.setBounds(_btn.getPos()[0], _btn.getPos()[1], 20, 20);
            _btn.setOutColor();
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
        board.showMessageDialog("모든 말이 판에 올라가지 않았고, 빽도가 나와 낙 처리됩니다.", "낙 발생!!");

        System.out.println("빽도 나옴 -> 낙 처리");
        game.consumeResult(YutResult.BackDo);
        board.updateResultList(game.getYutResults());

        game.nextTurn();
        board.updateTurnLabel(game.getCurrentPlayer().getId());
        board.getThrowButton().setEnabled(true);
        enableManualThrowButtons(true);
    }

    private boolean canMoveNow() {
        List<YutResult> results = game.getYutResults();
        if (results.isEmpty()) return false;

        // 이번 턴에 일반 윷이 하나라도 나왔으면 이동 가능
        return hasNonBonusYut;
    }
}
