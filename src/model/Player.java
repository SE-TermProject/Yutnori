package model;

import java.util.List;

public class Player {
    private int id;
    private List<Piece> pieces;

    public Player(int id, List<Piece> pieces) {
        this.id = id;
        this.pieces = pieces;

        for (Piece piece : pieces) { // player가 가진 piece들
            piece.setOwner(this); // player에게 소유시킴
        }
    }

    /* index로 사용자의 piece 선택 */
    public Piece selectPiece(int index) {
        return pieces.get(index);
    }

    /* 아직 도착하지 않은 piece의 개수를 반환 */
    public int checkRemainPieceNum() {
        int count = 0;
        for (Piece piece : pieces) {
            if (!piece.isFinished()) {
                count++;
            }
        }
        return count;
    }

    /* getter */
    public int getId() {
        return id;
    }

    /* getter */
    public List<Piece> getPieces() {
        return pieces;
    }
}
