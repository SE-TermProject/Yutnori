package model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameTest {

    private Game game;

    @BeforeEach
    void setUp() {
        game = new Game(5, 2, 3);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void throwYut() {
        // 윷을 두 번 던짐
        game.throwYut();
        game.throwYut();
        assertEquals(2, game.getYutResults().size());

        game.throwYut();
        assertEquals(3, game.getYutResults().size());
    }

    @Test
    void setManualYutResult() {
        game.setManualYutResult(YutResult.MO);
        game.setManualYutResult(YutResult.GUL);
        List<YutResult> results = game.getYutResults();

        assertEquals(2, results.size());
        assertEquals(YutResult.MO, results.get(0));
        assertEquals(YutResult.GUL, results.get(1));
    }

    @Test
    void consumeResult() {
        game.setManualYutResult(YutResult.DO);
        game.setManualYutResult(YutResult.GAE);

        assertEquals(2, game.getYutResults().size());
        game.consumeResult(YutResult.DO);
        assertEquals(1, game.getYutResults().size());
        assertFalse(game.getYutResults().contains(YutResult.DO));
    }

    @Test
    void checkWin() {
        // 모든 말의 상태를 도착 상태로 변경
        for (Piece piece : game.getCurrentPlayer().getPieces()) {
            piece.setFinished(true);
        }
        assertTrue(game.checkWin());

        // 하나라도 도착 안 하면 false
        game.getCurrentPlayer().getPieces().get(0).setFinished(false);
        assertFalse(game.checkWin());
    }

    @Test
    void findCurrentPossiblePos() {
        Player currentPlayer = game.getCurrentPlayer();
        List<Piece> pieces = currentPlayer.getPieces();
        Piece piece1 = pieces.get(0);
        Piece piece2 = pieces.get(1);
        Piece piece3 = pieces.get(2);
        piece1.setPosition(new int[]{0, 0});
        piece2.setPosition(new int[]{0, 10});
        piece3.setPosition(new int[]{3, 17});
        game.setManualYutResult(YutResult.GUL);
        game.setManualYutResult(YutResult.MO);

        HashMap<Piece, HashMap<YutResult, List<int[]>>> actual = game.findCurrentPossiblePos();

        assertEquals(3, actual.size());

        List<int[]> piece1PossiblePos = new ArrayList<>();
        piece1PossiblePos.add(new int[]{0, 3});
        piece1PossiblePos.add(new int[]{0, 5});
        List<int[]> piece2PossiblePos = new ArrayList<>();
        piece2PossiblePos.add(new int[]{0, 13});
        piece2PossiblePos.add(new int[]{0, 15});
        piece2PossiblePos.add(new int[]{2, 13});
        piece2PossiblePos.add(new int[]{1, 10});
        List<int[]> piece3PossiblePos = new ArrayList<>();
        piece3PossiblePos.add(new int[]{1, 10});
        piece3PossiblePos.add(new int[]{0, 21});

        int i = 0;
        while (i < piece1PossiblePos.size()) {
            HashMap<YutResult, List<int[]>> actualPossiblePos = actual.get(piece1);
            assertNotNull(actualPossiblePos);
            assertArrayEquals(actualPossiblePos.get(YutResult.GUL).get(i/2), piece1PossiblePos.get(i));
            i += 1;
            assertArrayEquals(actualPossiblePos.get(YutResult.MO).get(i/2), piece1PossiblePos.get(i));
            i += 1;
        }

        i = 0;
        while (i < piece2PossiblePos.size()) {
            HashMap<YutResult, List<int[]>> actualPossiblePos = actual.get(piece2);
            assertNotNull(actualPossiblePos);
            assertArrayEquals(actualPossiblePos.get(YutResult.GUL).get(i/2), piece2PossiblePos.get(i));
            i += 1;
            assertArrayEquals(actualPossiblePos.get(YutResult.MO).get(i/2), piece2PossiblePos.get(i));
            i += 1;
        }

        i = 0;
        while (i < piece3PossiblePos.size()) {
            HashMap<YutResult, List<int[]>> actualPossiblePos = actual.get(piece3);
            assertNotNull(actualPossiblePos);
            assertArrayEquals(actualPossiblePos.get(YutResult.GUL).get(i/2), piece3PossiblePos.get(i));
            i += 1;
            assertArrayEquals(actualPossiblePos.get(YutResult.MO).get(i/2), piece3PossiblePos.get(i));
            i += 1;
        }

    }
}