package model;

import java.util.Arrays;
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

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public Stack<int[]> getPrePositions() { return prePositions; }

    public void recordPrePositions(int numSides, int[] from, int[] to, YutResult yutResult) {
        /* 빽도로 이동하는 경우 */
        if (yutResult.equals(YutResult.BackDo)) {
            if (from[0] != 0) {
                if ((from[0] * 5 + 1 == from[1]) || (from[0] * 5 + 3 == from[1])) {
                    prePositions.pop(); // 스택에서 pop
                }
            }
            else if (from[1] % 5 == 0 && from[1] > 0 && from[1] / 5 <= numSides - 2)
                prePositions.pop(); // 스택에서 pop
        }

        /* 출발 위치가 가장 바깥쪽인 경우 */
        else if (from[0] == 0) {
            if (from[1] % 5 == 0 && from[1] > 0 && from[1] / 5 <= numSides - 2) { // 출발 위치가 꼭짓점인 경우 -> 안으로 이동 가능한
                if (to[0] != 0) { // 안쪽으로 들어가는 경우
                    // 이전 위치 저장
                    prePositions.push(new int[]{from[0], from[1]});

                    if (to[1] >= 5 * to[0] + 3) { // 중심점을 지나는 경우
                        // 이전 위치 저장
                        prePositions.push(new int[]{from[1] / 5, (from[1]/5) * 5 + 2});
                    }
                }
            }
            else if (to[0] == 0 && to[1] / 5 == numSides - 1) { // 시작점과 가장 가까운 꼭짓점을 지나는 경우
                // 이전 위치 저장
                prePositions.push(new int[]{0, (to[1]/5) * 5 - 1});
            }
        }

        /* 출발 위치가 안쪽인 경우 */
        else {
            if (from[1] < 5 * from[0] + 3
                    && (to[1] >= 5 * to[0] + 3 || to[0] == 0)) { // 중심점을 지나는 경우
                // 이전 위치 저장
                prePositions.push(new int[]{from[0], from[0] * 5 + 2});
            }

            if (to[0] == 0) { // 시작점과 가장 가까운 꼭짓점을 지나는 경우
                // 이전 위치 저장
                int quotient = numSides / 2; // 나눗셈, 나머지 버림
                prePositions.push(new int[]{quotient - 1, quotient * 5});
            }
        }

        // 로그
        System.out.print("이전에 이동했던 위치 기록 -> ");
        for (int[] prePosition : prePositions) {
            System.out.print(Arrays.toString(prePosition));
        }
        System.out.println();
    }

    public int[] peekPrePosition() { return prePositions.peek(); }

    public void pushPrePosition(int[] prePosition) { this.prePositions.push(prePosition); }

    public int[] popPrePosition(int prePosition) { return this.prePositions.pop(); }
}
