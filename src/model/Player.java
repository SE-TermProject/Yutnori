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

    /* getter */
    public int getId() {
        return id;
    }

    /* getter */
    public List<Piece> getPieces() {
        return pieces;
    }
}
