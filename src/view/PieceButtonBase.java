package view;

import model.Piece;

public interface PieceButtonBase {
    Piece getPiece();
    int getPlayerId();

    void setPos(int x, int y);
    int[] getPos();

    void setOutColor();
}
