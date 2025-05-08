package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Piece {

    private int[] position;
    private boolean isGrouped;
    private boolean isFinished;
    private Stack<int[]> prePositions; // 이전에 이동했던 위치 저장

    private List<Piece> pieceGroup = new ArrayList<Piece>();

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

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public Stack<int[]> getPrePositions() { return prePositions; }

    public int[] peekPrePosition() { return prePositions.peek(); }

    public void pushPrePosition(int[] prePosition) { this.prePositions.push(prePosition); }

    public int[] popPrePosition(int prePosition) { return this.prePositions.pop(); }
}
