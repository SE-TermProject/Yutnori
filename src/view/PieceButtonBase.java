package view;

import model.Piece;

public interface PieceButtonBase {
    Piece getPiece();
    int[] getPosition();
    void setPos(int x, int y);
    int[] getPos();
    void setOutColor();
    void initializeView(int currentX, int startY);
}
