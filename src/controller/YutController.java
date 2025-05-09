package controller;

import model.*;
import view.CandidatePieceButton;
import view.GameSetupView;
import view.PieceButton;
import view.YutBoardV2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.*;
import java.util.List;

public class YutController {
    private final Game game;
    private final YutBoardV2 board;
    private boolean hasNonBonusYut = false; // 일반 윷이 한 번이라도 나왔는지 추적
    private final Map<Piece, PieceButton> pieceToButtonMap = new HashMap<>();

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

            if (!result.isBonusTurn()) {
                hasNonBonusYut = true;
            }

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
                pieceToButtonMap.put(piece, btn);
                btn.setBounds(currentX, startY, 20, 20);
                btn.setPos(currentX, startY);
                btn.setEnabled(true);
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
                                    && game.getCurrentPlayer().getPieces().stream().allMatch(p -> {
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
                                if(possibleGetout(piece)) {
                                    JButton Getout = board.getEndPiece();
                                    YutResult useYut = getYutResult(piece);
                                    Getout.setEnabled(true);
                                    Getout.addActionListener(new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            Getout.setEnabled(false);
                                            handleGetoutButtonClick(btn);
                                            board.deletePieceButton(previewButtons);
                                            btn.getPiece().setFinished(true);
                                            game.getYutResults().remove(useYut);

                                            if(game.getYutResults().isEmpty()) {

                                                game.nextTurn();
                                            }
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
        board.updateResultList(game.getYutResults());

        if (!result.isBonusTurn()) {
            hasNonBonusYut = true;
        }

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
        HashMap<Piece, HashMap<YutResult, List<int[]>>> currentPossiblePos = game.findCurrentPossiblePos();
        HashMap<YutResult, List<int[]>> piecePossiblePos = currentPossiblePos.get(selectedPiece); // 선택된 말이 이동할 수 있는 모든 경로의 position

        List<YutResult> results = game.getYutResults();
        for (YutResult yutResult : results) {
            for (int[] pos : piecePossiblePos.get(yutResult)) {
                Point point = game.getBoard().indexToPoint(pos);

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

        for (CandidatePieceButton btn : possiblePosButtons) {
            board.add(btn);
            board.setComponentZOrder(btn, 0);

            btn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    board.getEndPiece().setEnabled(false);

                    if (game.getYutResults().isEmpty()) return;

                    for (CandidatePieceButton b : possiblePosButtons) {
                        for (ActionListener al : b.getActionListeners()) {
                            b.removeActionListener(al);
                        }
                    }

                    CandidatePieceButton destinationBtn = (CandidatePieceButton) e.getSource();
                    int[] to = destinationBtn.getPosition();  // 도착 지점의 index

                    /* 말 이동 로직 */
                    List<Point> piecePath = game.getBoard().calculatePath(from, to, btn.getYutResult());

                    performMove(selectedPiece, possiblePosButtons, destinationBtn, from, piecePath, btn);
                    board.deletePieceButton(possiblePosButtons);  // 모든 이동 가능한 경로에 있던 버튼 제거

                }
            });
        }
    }

    private void performMove(PieceButton selectedPiece, List<CandidatePieceButton> possiblePosButtons, CandidatePieceButton destinationBtn, int[] from, List<Point> piecePath, CandidatePieceButton btn) {
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

            if (game.checkWin()) {
                int choice = JOptionPane.showOptionDialog(
                        board,
                        "플레이어 " + (char)('A' + game.getCurrentPlayerIndex()) + " 승리!\n게임을 다시 시작하시겠습니까?",
                        "게임 종료",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.INFORMATION_MESSAGE,
                        null,
                        new Object[] {"재시작", "종료"},
                        "재시작"
                );

                SwingUtilities.getWindowAncestor(board).dispose(); // 현재 게임 창 닫기

                if (choice == JOptionPane.YES_OPTION) {
                    new GameSetupView(); // 재시작
                } else {
                    System.exit(0); // 완전 종료
                }
                return;
            }

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
        int[] currentPosition = selectedPiece.getPiece().getPosition();
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
                if (otherPiece != selectedPiece.getPiece() && Arrays.equals(otherPiece.getPosition(), currentPosition)) {
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
                            PieceButton pieceButton = board.getPieceButton(otherPiece);
                            otherPiece.resetPosition();
                            board.updatePiecePosition(pieceButton);
                            game.getBoard().catchPiece(otherPiece);
                        }
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
            enableManualThrowButtons(true);           // 수동 윷 버튼들 활성화
        } else {
            System.out.println(selectedBtn.getYutResult() + "으로 이동 후 말의 위치: [" + selectedPiece.getPiece().getPosition()[0] + ", " + selectedPiece.getPiece().getPosition()[1] + "]");



            if (game.checkWin()) {
                JOptionPane.showMessageDialog(board, "플레이어 " + (char) ('A' + game.getCurrentPlayerIndex()) + " 승리!");
                System.exit(0);  // 게임 종료
                return;
            }

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
        int numSides = game.getBoard().getNumSides();
        int length = game.getYutResults().size();
        for(int i = 0; i < length; i++){

            if(selectedPiece.isFinished(numSides, game.getYutResults().get(i).getStep())){
                return game.getYutResults().get(i);
            }
        }
    }

    private void handleGetoutButtonClick(PieceButton btn) {
        int startX, startY;

        if(btn != null){
            startX = btn.getPos()[0];
            startY = btn.getPos()[1];
            btn.setBounds(startX, startY, 20, 20);
            btn.GetoutColor();
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

    private boolean canMoveNow() {
        List<YutResult> results = game.getYutResults();
        if (results.isEmpty()) return false;

        // 이번 턴에 일반 윷이 하나라도 나왔으면 이동 가능
        return hasNonBonusYut;
    }
}
