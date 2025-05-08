package model;

import java.util.Stack;

public class Piece {

    private int[] position;
    private boolean isGrouped;
    private boolean isFinished;
    private Stack<int[]> prePositions; // 이전에 이동했던 위치 저장

    public Piece() {
        this.position = new int[0]; // 초기값
        this.isGrouped = false;
        this.isFinished = false;
        this.prePositions = new Stack<>();
    }

    // Getters and Setters
    public int[] getPosition() {
        return position;
    }

    public void setPosition(int[] position) {
        this.position = position;
    }

    public boolean isGrouped() {
        return isGrouped;
    }

    public void setGrouped(boolean grouped) {
        isGrouped = grouped;
    }

    //position[0] = row, [1] = col
    public boolean isFinished(int numSide) {

        if(position.length == 0) return isFinished;

        int now_row = position[0];
        int now_col = position[1];

        // 4각형
        if(numSide == 4){
            if(now_row == 0 && now_col > 20) { isFinished = true; }
            if(now_row != 0 && now_col > 16) { isFinished = true; }
            return isFinished;
        }
        // 5각형
        else if(numSide ==5) {
            if(now_row == 0 && now_col > 25) { isFinished = true; }
            if(now_row != 0 && now_col > 16) { isFinished = true; }
            return isFinished;
        }

        // 6각형
        else  {
            if(now_row == 0 && now_col > 30) { isFinished = true; }
            if(now_row != 0 && now_col > 21) { isFinished = true; }
            return isFinished;
        }
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public Stack<int[]> getPrePositions() { return prePositions; }

    public int[] peekPrePosition() { return prePositions.peek(); }

    public void pushPrePosition(int[] prePosition) { this.prePositions.push(prePosition); }

    public int[] popPrePosition(int prePosition) { return this.prePositions.pop(); }
}
