package model;

import java.util.*;

public class Game {
    private final List<Player> players;
    private final Board board;
    private int currentPlayerIndex;
    private List<YutResult> yutResults;

    public Game(int numSides, int playerCount, int pieceCount) {
        this.board = new Board(numSides);
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
    // returnType "YutResult"로 변경 필요!!!
    public String throwYut() {
        Yut yut = new Yut();
        String result = yut.getRandomResult();
        return result;
    }

    /* 말 이동 (선택된 말과 윷 결과 기반으로 이동 처리) */
    public void movePiece() {

    }

    /* 턴 넘기기 */
    public void nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    /* 승리 조건 확인 */
    public boolean checkWin() {
        return false;
    }

    /* 윷 결과 반환 */
    public List<YutResult> getYutResults() { return this.yutResults; }

    public HashMap<Piece, List<int[]>> findCurrentPossiblePos() {
        HashMap<Piece, List<int[]>> currentPossiblePos = new HashMap<>();
        System.out.println("현재 이동 가능한 경로 ---- ");

        List<YutResult> possibleYutResults = new ArrayList<>(yutResults);
        possibleYutResults = new ArrayList<>(new LinkedHashSet<>(yutResults)); // 중복 제거

        Player currentPlayer = getCurrentPlayer(); // 현재 차례인 플레이어
        List<Piece> currentPlayerPieces = currentPlayer.getPieces(); // 현재 차레인 플레이어의 모든 말
        for (Piece piece : currentPlayerPieces) {
            if (!piece.isFinished()) { // 아직 완료되지 않은 말들 -> 이동할 수 있는 말

                List<int[]> possiblePos = new ArrayList<>();
                int[] position = Arrays.copyOf(piece.getPosition(), piece.getPosition().length);
                if (position.length == 0) { // 아직 출발하지 않은 배열이라면
                    position = new int[]{0, 0};
                }

                for (YutResult result: possibleYutResults) {
                    possiblePos.addAll(board.findPossiblePos(piece.getPrePositions(), position[0], position[1], result.getStep()));
                }

                // 출력하는 로그
                if (piece.getPosition().length == 0) System.out.print("출발하지 않은 piece");
                else System.out.print(Arrays.toString(piece.getPosition()));
                System.out.print(" -> ");
                for (int[] pos : possiblePos) System.out.print(Arrays.toString(pos));
                System.out.println();

                currentPossiblePos.put(piece, possiblePos);
            }
        }

        return currentPossiblePos;
    }
}
