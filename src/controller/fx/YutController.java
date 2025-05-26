package controller.fx;

import javafx.geometry.Point2D;
import javafx.stage.Stage;

import app.fx.AppManager;
import model.*;
import view.fx.YutBoard;
import view.fx.PieceButton;
import view.fx.CandidatePieceButton;

import java.util.*;

public class YutController {
    private final AppManager appManager;
    private final Game game;
    private final YutBoard board;
    private boolean hasNonBonusYut = false;
    private final Map<Piece, PieceButton> pieceToButtonMap = new HashMap<>();

    public YutController(AppManager appManager, int sides, int playerCount, int pieceCount, YutBoard board) {
        this.appManager = appManager;
        this.game = new Game(sides, playerCount, pieceCount);
        this.board = board;

        initializeGameUI();
    }

    private void initializeGameUI() {
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

            if (game.getYutResults().getFirst() == YutResult.BackDo
                    && game.getCurrentPlayer().getPieces().stream().allMatch(p -> {
                int[] pos = p.getPosition();
                return pos.length == 0 || (pos[0] == 0 && pos[1] == 0);
            })) {
                NackedBackDo();
                return;
            }

            board.getThrowButton().setDisable(!result.isBonusTurn());
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
        double playerGapY = 40, pieceGapX = 30;

        for (Player player : game.getPlayers()) {
            int currentX = startX;
            PieceButton leftmostBtn = null;

            for (Piece piece : player.getPieces()) {
                PieceButton btn = new PieceButton(piece, player.getId());
                pieceToButtonMap.put(piece, btn);
                btn.setLayoutX(currentX);
                btn.setLayoutY(startY);
                btn.setPos(currentX, startY);
                btn.setPrefWidth(20);
                btn.setPrefHeight(20);
                btn.setPos(currentX, startY);
                btn.setDisable(false);

                if (leftmostBtn == null) {
                    leftmostBtn = btn;
                }

                btn.setOnAction(event -> {
                    board.getOutButton().setDisable(true);
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
                        if (game.getYutResults().getFirst() == YutResult.BackDo
                                && game.getCurrentPlayer().getPieces().stream()
                                    .filter(p -> !p.isFinished())
                                    .allMatch(p -> {
                                        int[] pos = p.getPosition();
                                        return pos.length == 0 || (pos[0] == 0 && pos[1] == 0);
                                    })) {
                            NackedBackDo();
                        }
                        if(game.getCurrentPlayer().getPieces().contains(piece)) { // 현재 차례인 사용자의 말이라면
                            System.out.println("말이 선택되었습니다.");

                            board.getThrowButton().setDisable(true);

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
                                        int result = board.showGameOverDialog(winnerName);

                                        Stage stage = (Stage) board.getScene().getWindow();
                                        stage.close();

                                        if (result == 0) {
                                            appManager.restartGame();  // 재시작
                                        } else {
                                            appManager.exitGame();     // 종료
                                        }
                                    }

                                    if (game.getYutResults().isEmpty()) {
                                        game.nextTurn();
                                        board.updateTurnLabel(game.getCurrentPlayer().getId());
                                        hasNonBonusYut = false;
                                        enableManualThrowButtons(true);
                                        board.getThrowButton().setDisable(false);
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
                });
                pieceButtons.add(btn);
                currentX += pieceGapX;
            }

            if (leftmostBtn != null) {
                board.addPlayerLabel(player.getId(), leftmostBtn.getLayoutX() - 20, leftmostBtn.getLayoutY(), board.getBoardLayer());
            }
            startY += playerGapY;
        }
        return pieceButtons;
    }

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
                btn.setDisable(false);
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

        board.moveActionToCandidates(possiblePosButtons, destinationBtn -> {
            board.getOutButton().setDisable(true);
            if (game.getYutResults().isEmpty()) return;

            board.deletePieceButton(possiblePosButtons);  // 버튼 제거

            int[] to = destinationBtn.getPosition(game.getBoard().getNumSides()); // 도착 지점의 index
            destinationBtn.setPosition(to);
            /* 말 이동 로직 */
            List<BoardPoint> piecePath = game.getBoard().calculatePath(from, to, destinationBtn.getYutResult());

            performMove(selectedPiece, possiblePosButtons, destinationBtn, from, piecePath, destinationBtn);
        });
    }

    private List<Point2D> boardPointToPoint(List<BoardPoint> piecePath) {
        List<Point2D> piecePointPath = new ArrayList<>();

        for (BoardPoint piecePoint : piecePath) {
            piecePointPath.add(new Point2D(piecePoint.getX(), piecePoint.getY()));
        }

        return piecePointPath;
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

        List<PieceButton> groupButtons = new ArrayList<>();
        groupButtons.add(selectedPiece);

        for (Piece grouped : selectedPiece.getPiece().getPieceGroup()) {
            PieceButton groupedBtn = pieceToButtonMap.get(grouped);
            if (groupedBtn != null) {
                groupButtons.add(groupedBtn);
            }
        }

        board.animatePieceMovement(groupButtons, boardPointToPoint(piecePath), onComplete);
    }

    private void handleAfterMove(CandidatePieceButton selectedBtn, PieceButton selectedPiece) {
        Player currentPlayer = game.getCurrentPlayer();
        boolean catchPieces = false;

        game.consumeResult(selectedBtn.getYutResult());
        updateResultPanel(game.getYutResults());

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
                        PieceButton pieceButton = pieceToButtonMap.get(selectedPiece.getPiece());
                        if (pieceButton != null) {
                            pieceButton.updateGroupVisual(groupedPiece.size()); // 커스텀 메서드 추천
                        }
                    } else {
                        System.out.println("상대 팀의 말을 잡습니다.");

                        catchPiece(otherPiece);
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
            board.getThrowButton().setDisable(false);  // 윷 던지기 버튼 활성화
            enableManualThrowButtons(true);   // 수동 윷 버튼들 활성화
            hasNonBonusYut = false;
        } else {
            System.out.println(selectedBtn.getYutResult() + "으로 이동 후 말의 위치: [" + selectedPiece.getPiece().getPosition()[0] + ", " + selectedPiece.getPiece().getPosition()[1] + "]");

            if (!game.hasRemainingMoves()) {
                if (!game.getYutResults().isEmpty() && game.getYutResults().get(game.getYutResults().size() - 1).isBonusTurn()) {
                    board.getThrowButton().setDisable(false);
                    enableManualThrowButtons(true);
                } else {
                    game.nextTurn();
                    board.updateTurnLabel(game.getCurrentPlayer().getId());
                    board.getThrowButton().setDisable(false);
                    enableManualThrowButtons(true);
                    hasNonBonusYut = false; // 턴 종료 시 초기화
                }
            } else {
                board.getThrowButton().setDisable(true);
                enableManualThrowButtons(false);
            }
        }
    }

    /* 상대방의 말을 잡기 */
    private void catchPiece(Piece otherPiece) {
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

    private void handleManualThrow(YutResult result) {
        game.setManualYutResult(result);
        updateResultPanel(game.getYutResults());

        if (!result.isBonusTurn()) {
            hasNonBonusYut = true;
        }

        if (game.getYutResults().getFirst() == YutResult.BackDo
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

        if (groupedPieces.isEmpty()) {
            btn.getPiece().setFinished(true);
            board.showPieceAsFinished(btn);
            return;
        }

        for (Piece piece : groupedPieces) {
            piece.setFinished(true);
            PieceButton groupedBtn = pieceToButtonMap.get(piece);
            board.showPieceAsFinished(groupedBtn);
        }
    }

    private void enableManualThrowButtons(boolean enabled) {
        board.getThrowBackdo().setDisable(!enabled);
        board.getThrowDo().setDisable(!enabled);
        board.getThrowGae().setDisable(!enabled);
        board.getThrowGeol().setDisable(!enabled);
        board.getThrowYut().setDisable(!enabled);
        board.getThrowMo().setDisable(!enabled);
    }

    private void NackedBackDo() {
        board.showMessageDialog("모든 말이 판에 올라가지 않았고, 빽도가 나와 낙 처리됩니다.", "낙 발생!!");

        System.out.println("빽도 나옴 -> 낙 처리");
        game.consumeResult(YutResult.BackDo);
        updateResultPanel(game.getYutResults());

        game.nextTurn();
        board.updateTurnLabel(game.getCurrentPlayer().getId());
        board.getThrowButton().setDisable(false);
        enableManualThrowButtons(true);
    }

    private boolean canMoveNow() {
        List<YutResult> results = game.getYutResults();
        if (results.isEmpty()) return false;

        // 이번 턴에 일반 윷이 하나라도 나왔으면 이동 가능
        return hasNonBonusYut;
    }
}
