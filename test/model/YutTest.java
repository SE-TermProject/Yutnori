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
        String resultStr = yut.getRandomResult();
        assertNotNull(resultStr);
    }

    @Test
    void getResult() {
    }

    @Test
    void setManualResult() {
        yut.setManualResult(YutResult.GUL);
        assertEquals(YutResult.GUL, yut.getResult());

        yut.setManualResult(YutResult.MO);
        assertEquals(YutResult.MO, yut.getResult());
    }
}