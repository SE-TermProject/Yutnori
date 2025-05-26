package model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BoardTest {

    private Board board;

    @BeforeEach
    void setUp() {
        board = new Board(5);
        List<Player> players = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            List<Piece> pieces = new ArrayList<>();
            for (int j = 0; j < 3; j++) {
                pieces.add(new Piece());
            }
            players.add(new Player(i, pieces));
        }
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void indexToPoint() {
        // 존재하는 점이어야 함
        BoardPoint point = board.indexToPoint(new int[]{0, 5});
        assertNotNull(point);

        point = board.indexToPoint(new int[]{2, 14});
        assertNotNull(point);

        point = board.indexToPoint(new int[]{0, 25});
        assertNotNull(point);

        point = board.indexToPoint(new int[]{2, 16});
        assertNotNull(point);

        // 존재하지 않는 점이어야 함
        point = board.indexToPoint(new int[]{0, -1});
        assertNull(point);

        point = board.indexToPoint(new int[]{0, 27});
        assertNull(point);

        point = board.indexToPoint(new int[]{2, 17});
        assertNull(point);

        point = board.indexToPoint(new int[]{3, 19});
        assertNull(point);
    }

    @Test
    void findPossiblePos() {
        Stack<int[]> prePositions = new Stack<>();
        List<int[]> actual = board.findPossiblePos(prePositions, 0, 5, 2);

        List<int[]> expected = new ArrayList<>();
        expected.add(new int[]{0, 7});
        expected.add(new int[]{1, 7});

        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertArrayEquals(expected.get(i), actual.get(i));
        }

        prePositions.add(new int[]{2, 12});
        actual = board.findPossiblePos(prePositions, 1, 8, -1);

        expected = new ArrayList<>();
        expected.add(new int[]{2, 12});

        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertArrayEquals(expected.get(i), actual.get(i));
        }
    }

    @Test
    void calculatePath() {
        int[] from = new int[2];
        int[] to = new int[2];

        // 빽도
        from[0] = 0; from[1] = 8;
        to[0] = 0; to[1] = 7;
        List<int[]> path = new ArrayList<>();
        path.add(from); path.add(to);
        List<BoardPoint> pathPoint = board.pathIndexToPoint(path);

        List<BoardPoint> actual = board.calculatePath(from, to, YutResult.BackDo);
        for (int i = 0; i < actual.size(); i++) {
            assertEquals(pathPoint.get(i), actual.get(i));
        }

        // 꼭짓점에서 안쪽으로 이동
        from[0] = 0; from[1] = 10;
        to[0] = 2; to[1] = 12;
        path.clear();
        path.add(from); path.add(new int[]{2, 11}); path.add(to);
        pathPoint = board.pathIndexToPoint(path);

        actual = board.calculatePath(from, to, YutResult.GAE);
        for (int i = 0; i < actual.size(); i++) {
            assertEquals(pathPoint.get(i), actual.get(i));
        }

        // 중심점을 이동
        from[0] = 3; from[1] = 16;
        to[0] = 1; to[1] = 9;
        path.clear();
        path.add(from); path.add(new int[]{3, 17}); path.add(new int[]{3, 18}); path.add(to);
        pathPoint = board.pathIndexToPoint(path);

        actual = board.calculatePath(from, to, YutResult.GUL);
        for (int i = 0; i < actual.size(); i++) {
            assertEquals(pathPoint.get(i), actual.get(i));
        }

        // 도착 가능
        from[0] = 0; from[1] = 23;
        to[0] = 0; to[1] = 27;
        path.clear();
        path.add(from); path.add(new int[]{0, 24}); path.add(new int[]{0,25});
        pathPoint = board.pathIndexToPoint(path);

        actual = board.calculatePath(from, to, YutResult.YUT);
        for (int i = 0; i < actual.size(); i++) {
            assertEquals(pathPoint.get(i), actual.get(i));
        }
    }
}