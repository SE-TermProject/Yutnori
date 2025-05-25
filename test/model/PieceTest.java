package model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Stack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PieceTest {

    private Piece piece;

    @BeforeEach
    void setUp() {
        piece = new Piece();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void resetPosition() {
        piece.setPosition(new int[]{0, 5});
        piece.setGrouped(true);
        piece.setFinished(true);
        piece.getPieceGroup().add(new Piece());
        piece.getPrePositions().push(new int[]{0, 1});

        piece.resetPosition();

        assertEquals(0, piece.getPosition().length);
        assertFalse(piece.isGrouped());
        assertFalse(piece.isFinished());
        assertTrue(piece.getPieceGroup().isEmpty());
        assertTrue(piece.getPrePositions().isEmpty());
    }

    @Test
    void removeGroupedPiece() {
        piece.setGrouped(true);
        piece.getPieceGroup().add(new Piece());
        piece.removeGroupedPiece();

        assertFalse(piece.isGrouped());
        assertTrue(piece.getPieceGroup().isEmpty());
    }

    @Test
    void isFinished() {
        piece.setPosition(new int[]{0, 18});
        assertFalse(piece.isFinished(4));

        piece.setPosition(new int[]{2, 18});
        assertTrue(piece.isFinished(5));

        piece.setPosition(new int[]{4, 21});
        assertFalse(piece.isFinished(6));

        piece.setPosition(new int[]{0, 0});
        assertFalse(piece.isFinished(5, 5));

        piece.setPosition(new int[]{2, 12});
        assertFalse(piece.isFinished(5, 5));

        piece.setPosition(new int[]{2, 14});
        assertTrue(piece.isFinished(5, 5));

        piece.setPosition(new int[]{3, 18});
        assertTrue(piece.isFinished(6, 4));
    }

    @Test
    void recordPrePositions() {
        piece.recordPrePositions(5, new int[]{0, 5}, new int[]{1, 7}, YutResult.GAE);
        piece.recordPrePositions(5, new int[]{2, 12}, new int[]{1, 10}, YutResult.GUL);

        Stack<int[]> actual = piece.getPrePositions();
        assertEquals(2, actual.size());
        assertArrayEquals(new int[]{0, 5}, actual.get(0));
        assertArrayEquals(new int[]{2, 12}, actual.get(1));
    }
}