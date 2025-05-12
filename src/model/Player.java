package model;

import java.util.List;

public class Player {
    private int id;
    private List<Piece> pieces;

    public Player(int id, List<Piece> pieces) {
        this.id = id;
        this.pieces = pieces;

        for (Piece piece : pieces) {
            piece.setOwner(this);
        }
    }

    public Piece selectPiece(int index) {
        return pieces.get(index);
    }

    public int checkRemainPieceNum() {
        int count = 0;
        for (Piece piece : pieces) {
            if (!piece.isFinished()) {
                count++;
            }
        }
        return count;
    }

    // Getter
    public int getId() {
        return id;
    }

    public List<Piece> getPieces() {
        return pieces;
    }
}
