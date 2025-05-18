package model;

import java.util.*;
import java.util.List;

public class Board {
    private final int numSides;
    private final Map<BoardPoint, int[][]> coordinateToIndexMap = new HashMap<>();
    private final List<Player> players;

    public Board(int numSides, List<Player> players) {
        this.numSides = numSides;
        this.players = new ArrayList<>(players);
        initializeCoordinateMap();
    }

    /* getter */
    public int getPlayerCount() {
        return players.size();
    }

    /* getter */
    public List<Player> getPlayers() {
        return players;
    }

    /* getter */
    public int getNumSides() {
        return numSides;
    }

    /* getter */
    public int[][] getIndicesAt(BoardPoint point) {
        return coordinateToIndexMap.get(point);
    }

    /* 각 칸 좌표 초기 설정 */
    public void initializeCoordinateMap() {
        int[][] data;
        if (numSides == 4) {
            data = new int[][] {
                    // 중심점
                    {350, 350, 1, 8}, {350, 350, 2, 13},

                    // 대각선
                    {256, 256, 2, 11}, {303, 303, 2, 12},
                    {444, 256, 1, 6}, {397, 303, 1, 7},
                    {303, 397, 1, 9}, {256, 444, 1, 10},
                    {397, 397, 2, 14}, {444, 444, 2, 15},

                    // 외곽 선상 점들 (왼쪽 -> 위 -> 오른쪽 -> 아래 방향)
                    {491, 491, 0, 0}, {491, 491, 2, 16}, {491, 434, 0, 1}, {491, 378, 0, 2}, {491, 321, 0, 3}, {491, 265, 0, 4}, {491, 209, 0, 5},
                    {434, 209, 0, 6}, {378, 209, 0, 7}, {321, 209, 0, 8}, {265, 209, 0, 9}, {209, 209, 0, 10},
                    {209, 265, 0, 11}, {209, 321, 0, 12}, {209, 378, 0, 13}, {209, 434, 0, 14}, {209, 491, 0, 15}, {209, 491, 1, 11},
                    {265, 491, 0, 16}, {321, 491, 0, 17}, {378, 491, 0, 18}, {434, 491, 0, 19}, {491, 491, 0, 20}
            };
        } else if (numSides == 5) {
            data = new int[][] {
                    {350, 216, 2, 11}, {350, 283, 2, 12}, {223, 309, 3, 16}, {286, 329, 3, 17},
                    {350, 350, 1, 8}, {350, 350, 2, 13}, {350, 350, 3, 18}, {413, 329, 1, 7}, {311, 403, 1, 9}, {389, 403, 2, 14},
                    {272, 457, 1, 10}, {428, 457, 2, 15}, {350, 150, 0, 10}, {388, 177, 0, 9},
                    {426, 205, 0, 8}, {464, 233, 0, 7}, {502, 261, 0, 6}, {540, 289, 0, 5},
                    {476, 309, 1, 6}, {525, 333, 0, 4}, {510, 377, 0, 3}, {496, 422, 0, 2},
                    {481, 466, 0, 1}, {467, 511, 0, 0}, {467, 511, 2, 16}, {420, 511, 0, 24}, {373, 511, 0, 23},
                    {326, 511, 0, 22}, {279, 511, 0, 21}, {233, 511, 0, 20}, {233, 511, 1, 11}, {218, 466, 0, 19},
                    {203, 422, 0, 18}, {189, 377, 0, 17}, {174, 333, 0, 16}, {160, 289, 0, 15},
                    {198, 261, 0, 14}, {236, 233, 0, 13}, {274, 205, 0, 12}, {312, 177, 0, 11}, {467, 511, 0, 25}
            };
        } else if (numSides == 6) {
            data = new int[][] {
                    {283, 234, 3, 16}, {416, 234, 2, 11}, {316, 292, 3, 17}, {383, 292, 2, 12},
                    {216, 350, 4, 21}, {283, 350, 4, 22}, {350, 350, 1, 8}, {350, 350, 2, 13}, {350, 350, 3, 18}, {350, 350, 4, 23},
                    {416, 350, 1, 7}, {483, 350, 1, 6}, {317, 407, 2, 14}, {383, 407, 3, 19}, {284, 465, 2, 15},
                    {416, 465, 3, 20}, {250, 177, 0, 15}, {290, 177, 0, 14}, {330, 177, 0, 13},
                    {370, 177, 0, 12}, {410, 177, 0, 11}, {450, 177, 0, 10}, {470, 211, 0, 9},
                    {490, 246, 0, 8}, {510, 280, 0, 7}, {530, 315, 0, 6}, {550, 350, 0, 5},
                    {530, 384, 0, 4}, {510, 419, 0, 3}, {490, 453, 0, 2}, {470, 488, 0, 1},
                    {450, 523, 0, 0}, {450, 523, 3, 21}, {410, 523, 0, 29}, {370, 523, 0, 28}, {330, 523, 0, 27},
                    {290, 523, 0, 26}, {251, 523, 0, 25}, {230, 488, 0, 24}, {210, 453, 0, 23},
                    {190, 419, 0, 22}, {170, 384, 0, 21}, {150, 350, 0, 20}, {170, 315, 0, 19},
                    {190, 280, 0, 18}, {210, 246, 0, 17}, {230, 211, 0, 16}, {450, 523, 0, 30}
            };
        } else {
            data = new int[0][];
        }

        Map<BoardPoint, List<int[]>> tempMap = new HashMap<>();

        for (int[] entry : data) {
            BoardPoint point = new BoardPoint(entry[0], entry[1]);
            tempMap
                    .computeIfAbsent(point, k -> new ArrayList<>())
                    .add(new int[]{entry[2], entry[3]});
        }

        for (Map.Entry<BoardPoint, List<int[]>> e : tempMap.entrySet()) {
            List<int[]> list = e.getValue();
            int[][] array = list.toArray(new int[0][]);
            coordinateToIndexMap.put(e.getKey(), array);
        }
    }

    /* index -> point 변환 */
    public BoardPoint indexToPoint(int[] index) {
        for (Map.Entry<BoardPoint, int[][]> entry : coordinateToIndexMap.entrySet()) {
            for (int[] idx : entry.getValue()) {
                if (idx[0] == index[0] && idx[1] == index[1]) {
                    return entry.getKey();  // 일치하는 좌표 반환
                }
            }
        }
        return null;
    }

    /* 이동 가능한 위치 계산 */
    public List<int[]> findPossiblePos(Stack<int[]> prePositions, int row, int col, int step) {

        // 해당 인덱스의 칸(cells[row][col])에서 윷 결과(step)으로 이동 가능한 위치 계산
        ArrayList<int[]> possiblePos = new ArrayList<>(); // 해당 위치에서 이동 가능한 모든 위치를 담을 배열
        int[] nextPos = new int[2]; // 다음으로 이동 가능한 경로 계산하기 위한 배열

        /* 빽도인 경우 */
        if (step < 0) {
            if (row == 0) { // 가장 바깥쪽에 있는 말인 경우
                if (col == 0) { // 출발점인 경우
                    // finish
                    System.out.println("-> 도착 가능합니다.");
                    return new ArrayList<>(); // 비어있는 배열을 반환
                }
                else if (col == (numSides - 1) * 5 && !prePositions.empty()) {
                    int[] recentPos = prePositions.peek();
                    possiblePos.add(new int[]{recentPos[0], recentPos[1]});
                }
                else possiblePos.add(new int[]{row, col + step});
            }
            else { // 안쪽에 있는 말인 경우
                if (col == 5 * row + 3) { // 중심점인 경우
                    int[] recentPos = prePositions.peek();
                    possiblePos.add(new int[]{recentPos[0], recentPos[1]});
                }
                else {
                    nextPos[0] = row; nextPos[1] = col + step;
                    if (nextPos[1] == nextPos[0] * 5) { // 꼭짓점으로 이동하는 경우
                        nextPos[0] = 0;
                    }
                    possiblePos.add(new int[]{nextPos[0], nextPos[1]});
                }
            }
        }

        /* 가장 바깥쪽에 있는 말인 경우 */
        else if (row == 0) {
            nextPos[0] = row; nextPos[1] = col + step; // 바깥쪽으로 이동 가능 경로
            if (nextPos[1] > numSides * 5 || nextPos[1] < 0) { // 시작점을 지나 도착 가능한 경우
                // finish
                System.out.println("-> 도착 가능합니다.");
                return new ArrayList<>(); // 비어있는 배열을 반환
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

            else if (numSides > 4 && col < 5 * row + 3 && nextPos[1] > 5 * nextPos[0] + 3) { // 중심점을 지나 이동한 경우, 사각형을 제외하고는 다른 규칙을 적용해야 함
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
                System.out.println("-> 도착 가능합니다.");
                return new ArrayList<>(); // 비어있는 배열을 반환
            }
            possiblePos.add(new int[]{nextPos[0], nextPos[1]});
        }

        // 출력하는 로그
        System.out.print("[" + row + ", " + col + "]에서 step " + step + "으로 이동 가능한 경로: ");
        for (int[] pos : possiblePos) System.out.print(Arrays.toString(pos));
        System.out.println();

        return possiblePos;
    }

    /* 한 칸씩 이동하기 위한 이동 경로 계산 */
    public List<BoardPoint> calculatePath(int[] from, int[] to, YutResult yutResult) {
        List<int[]> path = new ArrayList<>();

        // 1. 빽도인 경우
        if (yutResult == YutResult.BackDo) {
            path.add(from);
            path.add(to);
            return pathIndexToPoint(path);
        }

        // 2. 빽도가 아닌 도,개,걸,윷,모의 경우
        if (from[0] == to[0]) {
            for (int i = from[1]; i <= to[1]; i++) {
                path.add(new int[]{from[0], i});
            }
        } else if (from[0] < to[0]) {
            path.add(from);  // 현재 위치 추가

            int curX = from[1] / 5;
            int curY = from[1] + 1;
            // 1. 만약 출발 지점이 분기점이라면
            if (from[0] == 0 && from[1] % 5 == 0) {
                // 1-1. 만약 도착 지점이 중심점 & 중심점 이전에 있다면 해당 칸까지의 이동거리 추가
                if (to[1] - from[1] <= 3 && to[1] - from[1] > 0) {
                    for (int i = curY; i <= to[1]; i++) {
                        path.add(new int[]{curX, i});
                    }
                } else {
                    if (numSides == 4) {   // 1-2. 만약 도착 지점이 중심점을 넘어선다면
                        for (int i = curY; i <= to[1]; i++) {
                            path.add(new int[]{curX, i});
                        }
                    } else {
                        boolean passedCenter = false;
                        while (true) {
                            path.add(new int[]{curX, curY});

                            // 현재 지점이 도착 지점이라면 while 문 종료
                            if (curX == to[0] && curY == to[1]) {
                                break;
                            }

                            // 현재 지점이 중심점이라면 -> 중심점을 지나가므로 경로를 변경시켜줌
                            // numSides가 5라면 (1,8)로, 6이라면 (2,13)으로 변경
                            if (isCenterPoint(curX, curY) && !passedCenter) {
                                curX = numSides / 3;
                                curY = 5 * (numSides / 2) - 2;
                                passedCenter = true;
                            }

                            curY++;
                        }
                    }
                }
            } else if (isCenterPoint(from[0], from[1])) {
                // 2. 출발 지점이 중심점이라면
                curX = numSides / 2;
                curY = curX * 5 + 3;

                for (int i = curY; i <= to[1]; i++) {
                    path.add(new int[]{curX, i});
                }
            } else {
                // 3. 출발 지점이 분기점과 중심점이 아니라면
                boolean passedCenter = false;
                while (true) {
                    path.add(new int[]{curX, curY});

                    // 현재 지점이 도착 지점이라면 while 문 종료
                    if (curX == to[0] && curY == to[1]) {
                        break;
                    }

                    // 현재 지점이 중심점이라면 -> 중심점을 지나가므로 경로를 변경시켜줌
                    // numSides가 5라면 (1,8)로, 6이라면 (2,13)으로 변경
                    if (isCenterPoint(curX, curY) && !passedCenter) {
                        curX = numSides / 3;
                        curY = 5 * (numSides / 2) - 2;
                        passedCenter = true;
                    }
                    curY++;
                }
            }
        } else { // from[0] > to[0]
            path.add(new int[]{from[0], from[1]});

            int pointX = from[0];
            int pointY = from[1] + 1;
            boolean passedCenter = false;
            while (true) {
                // 현재 위치가 중간칸들을 빠져나가는 곳이라면
                // numSides가 4라면 (1, 11) -> (0, 15)
                // numSides가 5라면 (1, 11) -> (0, 20)
                // numSides가 6이라면 (2, 16) -> (0, 25)
                if (pointX == numSides / 3 && pointY == (numSides / 2) * 5 + 1) {
                    pointX = 0;
                    pointY = 5 * (numSides - 1);
                }

                path.add(new int[]{pointX, pointY});

                // 현재 지점이 도착 지점이라면 while문 종료
                if (pointX == to[0] && pointY == to[1]) {
                    break;
                }

                // 현재 지점이 중심점이라면 -> 중심점을 지나가므로 경로를 변경시켜줌
                // numSides가 5라면 (1, 8)로, 6이라면 (2, 13)으로 변경
                if (isCenterPoint(pointX, pointY) && !passedCenter) {
                    pointX = numSides / 3;
                    pointY = 5 * (numSides / 2) - 2;
                    passedCenter = true;
                }

                pointY++;
            }
        }

        return pathIndexToPoint(path);
    }

    /* 실제 말 이동 처리 */
    public void movePieceTo() {

    }

    /* 중심점인지 확인 */
    private boolean isCenterPoint(int x, int y) {
        List<int[]> centerPoints = new ArrayList<>(); // 중심점 인덱스
        for(int i = 1; i <= numSides - 2; i++) {
            centerPoints.add(new int[]{i, i * 5 + 3});
        }

        for (int[] cp : centerPoints) { // 해당 위치(x, y)가 중심점인지 확인
            if (cp[0] == x && cp[1] == y) {
                return true;
            }
        }
        return false;
    }

    /* path index list -> point list 변환 */
    private List<BoardPoint> pathIndexToPoint(List<int[]> pathIdx) {
        List<BoardPoint> result = new ArrayList<>();

        for (int[] targetIdx : pathIdx) {
            BoardPoint point = indexToPoint(targetIdx);
            if (point != null) {
                result.add(point);
            }
        }

        System.out.println("말의 이동 경로: ");
        for (int i = 0; i < result.size(); i++) {
            BoardPoint p = result.get(i);
            int[] idx = pathIdx.get(i);
            System.out.printf("%d번째: (%d, %d) || (%d, %d)%n", i, idx[0], idx[1], p.x, p.y);
        }

        return result;
    }

    /* 잡힌 말(단일 piece)을 시작 위치로 되돌림 */
    public void catchPiece(Piece piece) {
        // 해당 말의 위치를 시작 위치로 되돌리기
        piece.resetPosition();
        System.out.println("catch: 윷을 한 번 더 던지세요.");
    }

    /* 잡힌 말이 그룹이라면 모든 말들을 되돌림 */
    public void catchPiece(List<Piece> groupedPiece) {
        for (Piece piece : groupedPiece) {
            piece.resetPosition();
        }
    }
}