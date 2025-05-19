package view;

import model.BoardPoint;
import model.YutResult;

public interface CandidatePieceButtonBase {
    int[] getPosition();
    int[] getPosition(int numSides);
    void setPosition(int[] position);
    YutResult getYutResult();
    void setPixelPosition(BoardPoint point);
}
