package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Stack;

public class Piece {
    private int[] position;
    private boolean isGrouped;
    private boolean isFinished;
    private Stack<int[]> prePositions; // 이전에 이동했던 위치 저장

    private List<Piece> pieceGroup = new ArrayList<Piece>();
    private Player owner;

    public Piece() {
        this.position = new int[0]; // 초기값
        this.isGrouped = false;
        this.isFinished = false;
        this.prePositions = new Stack<>();
    }

    /* piece 위치 초기화 */
    public void resetPosition() {
        this.position = new int[0];
        this.isGrouped = false;
        this.isFinished = false;
        this.prePositions = new Stack<>();
        this.pieceGroup.clear();
    }

    /* getter */
    public Player getOwner() { return owner; }

    /* setter */
    public void setOwner(Player owner) { this.owner = owner; }

    /* getter */
    public int[] getPosition() {
        return position;
    }

    /* setter */
    public void setPosition(int[] position) {
        this.position = position;
    }

    /* getter */
    public boolean isGrouped() {
        return isGrouped;
    }

    /* setter */
    public void setGrouped(boolean grouped) {
        this.isGrouped = grouped;
    }

    /* getter */
    public List<Piece> getPieceGroup() {
        return pieceGroup;
    }

    /* setter */
    public void setPieceGroup(List<Piece> pieceGroup) {
        this.pieceGroup = pieceGroup;
    }

    /* 그룹에서 해당 말 제거 */
    public void removeGroupedPiece() {
        pieceGroup.clear();
        setGrouped(false);
    }

    /* 현재 위치 기준 도착 여부 판별 */
    // position[0] = row, [1] = col
    public boolean isFinished(int numSide) {

        if(position.length == 0) return isFinished;

        if(position[0] == 0){
            if(position[1] > 5 * numSide){
                return true;
            }
        }

        else{
            if(position[1] > position[0] * 5 + 6){
                return true;
            }
        }
        return isFinished;
    }

    /* 현재 위치에서 step만큼 이동 시 도착 가능한지 확인 */
    public boolean isFinished(int numSide, int step) {
        int q = numSide / 2;

        if(position.length == 0) { return isFinished; }
        // 빽도일 때
        if(step < 0) {
            return false;
        }
        else{
            // 테두리에 말이 존재할 때
            if(position[0] == 0){
                if(position[1] + step > 5 * numSide){
                    return true;
                }
            }
            // 안쪽, 도착지점과 연결된 경로에 있을 때
            else {
                if((position[1] == 5 * position[0] + 3 || position[0] == q && position[1] > position[0] * 5 + 3) && position[1] + step > 5 * position[0] + 6){
                    return  true;
                }
            }
        }
        return isFinished;
    }

    /* getter */
    public boolean isFinished() { return isFinished; }

    /* setter */
    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    /* 이동 이력을 담은 스택 반환 */
    public Stack<int[]> getPrePositions() { return prePositions; }

    /* 이동 이력을 기록 (중심점, 꼭짓점 등을 지나친 경우 기록) */
    public void recordPrePositions(int numSides, int[] from, int[] to, YutResult yutResult) {
        /* 빽도로 이동하는 경우 */
        if (yutResult.equals(YutResult.BackDo)) {
            if (prePositions.empty()) return;

            if (from[0] != 0) {
                if ((from[0] * 5 + 1 == from[1]) || (from[0] * 5 + 3 == from[1])) {
                    prePositions.pop(); // 스택에서 pop
                }
            }
            else if (from[1] % 5 == 0 && from[1] > 0 && from[1] / 5 == numSides - 1)
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

    /* 가장 최근 위치 반환 -> stack peek */
    public int[] peekPrePosition() { return prePositions.peek(); }

    /* 이동 이력 추가 -> stack push */
    public void pushPrePosition(int[] prePosition) { this.prePositions.push(prePosition); }

    /* 이동 이력 제거 -> stack pop */
    public int[] popPrePosition(int prePosition) { return this.prePositions.pop(); }
}
