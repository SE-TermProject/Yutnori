package model;

import java.util.ArrayList;
import java.util.List;

public class Piece {

    private int[][] position;
    private boolean isGrouped;
    private boolean isFinished;
    private List<int[]> prePositions; // 이전에 이동했던 위치 저장

    public Piece() {
        this.position = new int[0][0]; // 초기값
        this.isGrouped = false;
        this.isFinished = false;
        this.prePositions = new ArrayList<>();
    }

    // Getters and Setters
    public int[][] getPosition() {
        return position;
    }

    public void setPosition(int[][] position) {
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

    public List<int[]> getPrePositions() { return prePositions; }

    public void addPrePosition(int[] prePosition) { this.prePositions.add(prePosition); }

    public void deletePrePosition(int prePosition) { this.prePositions.remove(prePosition); }
}
