package model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class YutTest {

    private Yut yut;

    @BeforeEach
    void setUp() {
        yut = new Yut();
    }

    @Test
    void getRandomResult() {
        YutResult resultStr = yut.getRandomResult();
        assertNotNull(resultStr);
    }

    @Test
    void setManualResult() {
        YutResult yutResult;

        yutResult = yut.setManualResult(YutResult.GUL);
        assertEquals(YutResult.GUL, yutResult);

        yutResult = yut.setManualResult(YutResult.MO);
        assertEquals(YutResult.MO, yutResult);
    }
}