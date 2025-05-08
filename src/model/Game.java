package model;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private final List<Player> players;
    private final Board board;
    private int currentPlayerIndex;
    private List<Yut> yutResults;

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
}
