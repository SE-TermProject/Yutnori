package model;

public class Piece {

    private int[] position;
    private boolean isGrouped;
    private boolean isFinished;

    public Piece() {
        this.position = new int[0]; // 초기값
        this.isGrouped = false;
        this.isFinished = false;
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
}
