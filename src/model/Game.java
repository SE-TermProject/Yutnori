package model;

import java.util.*;

public class Game {
    private final List<Player> players;
    private final Board board;
    private int currentPlayerIndex;
    private final List<YutResult> yutResults;
    private final Yut yut = new Yut();

    public Game(int numSides, int playerCount, int pieceCount) {
        this.board = new Board(numSides, new ArrayList<>());
        this.players = new ArrayList<>();
        this.yutResults = new ArrayList<>();
        this.currentPlayerIndex = 0;

        for (int i = 0; i < playerCount; i++) {
            List<Piece> pieces = new ArrayList<>();
            for (int j = 0; j < pieceCount; j++) {
                pieces.add(new Piece());
            }
            players.add(new Player(i, pieces));
        }
    }

    public Board getBoard() {
        return board;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    /* 게임 시작 */
    public void startGame() {
        System.out.println("게임 시작합니다.");
    }

    /* 현재 플레이어 반환 */
    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    /* 윷 던지기 */
    public YutResult throwYut() {
        YutResult result = YutResult.valueOf(yut.getRandomResult());
        yutResults.add(result);  // 누적 리스트에 저장
//        System.out.println("던진 결과: " + result);
        return result;
    }

    public void setManualYutResult(YutResult result) {
        yut.setManualResult(result);
        yutResults.add(result);
    }

    /* 윷 결과 하나 소비 */
    public void consumeResult(YutResult usedResult) {
        yutResults.remove(usedResult);

        System.out.println("현재 플레이어: " + currentPlayerIndex);
        System.out.println("남은 윷 결과: ");
        for (YutResult yutResult : yutResults) {
            System.out.println(yutResult);
        }
    }

    public boolean hasRemainingMoves() {
        return !yutResults.isEmpty();
    }

    /* 턴 넘기기 */
    public void nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    /* 승리 조건 확인 */
    public boolean checkWin() {
        return getCurrentPlayer().getPieces().stream().allMatch(Piece::isFinished);
    }

    /* 윷 결과 반환 */
    public List<YutResult> getYutResults() { return this.yutResults; }

    public HashMap<Piece, HashMap<YutResult, List<int[]>>> findCurrentPossiblePos() {
        HashMap<Piece, HashMap<YutResult, List<int[]>>> currentPossiblePos = new HashMap<>();
        System.out.println("현재 이동 가능한 경로 ---- ");

        List<YutResult> possibleYutResults = new ArrayList<>(new LinkedHashSet<>(yutResults)); // 중복 제거

        Player currentPlayer = getCurrentPlayer(); // 현재 차례인 플레이어
        List<Piece> currentPlayerPieces = currentPlayer.getPieces(); // 현재 차레인 플레이어의 모든 말
        for (Piece piece : currentPlayerPieces) {
            if (!piece.isFinished(board.getNumSides())) { // 아직 완료되지 않은 말들 -> 이동할 수 있는 말
                HashMap<YutResult, List<int[]>> yutResultPossiblePos = new HashMap<>();

                int[] position = Arrays.copyOf(piece.getPosition(), piece.getPosition().length);
                if (position.length == 0) { // 아직 출발하지 않은 배열이라면
                    position = new int[]{0, 0};
                }

                for (YutResult yutResult: possibleYutResults) {
                    List<int[]> possiblePos = new ArrayList<>();
                    possiblePos.addAll(board.findPossiblePos(piece.getPrePositions(), position[0], position[1], yutResult.getStep()));
                    yutResultPossiblePos.put(yutResult, possiblePos);
                }

                // 출력하는 로그
                System.out.print("From: ");
                if (piece.getPosition().length == 0) System.out.println("출발하지 않은 piece");
                else System.out.println(Arrays.toString(piece.getPosition()));
                for (YutResult yutResult: yutResultPossiblePos.keySet()) {
                    System.out.print(yutResult + " -> ");
                    for (int[] pos : yutResultPossiblePos.get(yutResult)) System.out.print(Arrays.toString(pos));
                    System.out.println();
                }

                currentPossiblePos.put(piece, yutResultPossiblePos);
            }
        }

        return currentPossiblePos;
    }
}