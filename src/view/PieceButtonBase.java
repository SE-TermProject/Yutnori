package view;

import model.BoardPoint;
import model.Piece;

public interface PieceButtonBase {
    Piece getPiece();
    int getPlayerId();

    void setPixelPosition(BoardPoint center);
    void setPos(int x, int y);
    int[] getPos();

    void setOutColor();
}
