package model;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private int numSides;
    private int[][] cells;

    public Board(int numSides) {
        this.numSides = numSides;
        this.cells = new int[5][35];
    }

    /* 위치가 비어있는지 확인 */
    public boolean isOccupied() {
        return false;
    }

    /* 이동 가능한 위치 계산 */
    public List<int[]> findPossiblePos() {
        return new ArrayList<int[]>();
    }

    /* 실제 말 이동 처리 */
    public void movePieceTo() {

    }

    /* 상대 말 잡기 처리 */
    public void catchPiece() {

    }
}
