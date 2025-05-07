package model;

import java.util.ArrayList;
import java.util.Arrays;
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
    public List<int[]> findPossiblePos(int row, int col, int step) {
        // 해당 인덱스의 칸(cells[row][col])에서 윷 결과(step)으로 이동 가능한 위치 계산
        ArrayList<int[]> possiblePos = new ArrayList<>(); // 해당 위치에서 이동 가능한 모든 위치를 담을 배열
        int[] nextPos = new int[2]; // 다음으로 이동 가능한 경로 계산하기 위한 배열

        /* 빽도인 경우 */
        if (step < 0) {
        }

        /* 가장 바깥쪽에 있는 말인 경우 */
        else if (row == 0) {
            nextPos[0] = row; nextPos[1] = col + step; // 바깥쪽으로 이동 가능 경로
            if (nextPos[1] > numSides * 5 || nextPos[1] < 0) { // 시작점을 지나 도착 가능한 경우
                // finish
                return possiblePos; // 비어있는 배열을 반환
            }
            else {
                possiblePos.add(new int[]{nextPos[0], nextPos[1]});
            }

            /* 시작점 제외, 꼭짓점 위치인 경우 */
            if (col > 0 && col % 5 == 0) {
                // 안쪽으로 이동 가능 경로
                if (col / 5 <= numSides - 2) {
                    nextPos[0] = col / 5; nextPos[1] = col + step;
                    if (numSides > 4 && nextPos[1] > 5 * nextPos[0] + 3) { // 중심점을 지나 이동한 경우, 사각형을 제외하고는 다른 규칙을 적용해야 함
                        int e = nextPos[0] - (numSides - 4);
                        nextPos[0] = nextPos[0] - e;
                        nextPos[1] = nextPos[1] - 5 * e;
                    }

                    possiblePos.add(new int[]{nextPos[0], nextPos[1]});
                }
            }
        }

        /* 안쪽에 있는 말인 경우 */
        else {
            nextPos[0] = row; nextPos[1] = col + step;

            int quotient = numSides / 2; // 나눗셈, 나머지 버림
            if(col == 5 * row + 3) { // 중심점인 경우 인덱스 변경
                // 4각형, 5각형 -> (2, 13) / 6각형 -> (3, 18)
                nextPos[0] = quotient; nextPos[1] = (5 * quotient + 3) + step;
            }

            else if (numSides > 4 && nextPos[1] > 5 * nextPos[0] + 3) { // 중심점을 지나 이동한 경우, 사각형을 제외하고는 다른 규칙을 적용해야 함
                int e = nextPos[0] - (numSides - 4);
                nextPos[0] = nextPos[0] - e;
                nextPos[1] = nextPos[1] - 5 * e;
            }

            if (nextPos[0] == quotient - 1 && nextPos[1] > 5 * quotient) { // 시작점 제외, 꼭짓점을 지나 이동하는 경우
                // 인덱스 수
                nextPos[0] = 0;
                if (numSides == 4) nextPos[1] += 4;
                else nextPos[1] += 9;
            }

            if (nextPos[0] == quotient && nextPos[1] > 5 * (quotient + 1) + 1) { // 시작점을 지나 도착 가능한 경우
                // finish
                return possiblePos; // 비어있는 배열을 반환
            }
            possiblePos.add(new int[]{nextPos[0], nextPos[1]});
        }

        // 출력하는 로그
        System.out.print("[" + row + ", " + col + "]에서 step " + step + "으로 이동 가능한 경로: ");
        for (int[] pos : possiblePos) System.out.print(Arrays.toString(pos));
        System.out.println();

        return new ArrayList<int[]>();
    }

    /* 실제 말 이동 처리 */
    public void movePieceTo() {

    }

    /* 상대 말 잡기 처리 */
    public void catchPiece() {

    }
}
