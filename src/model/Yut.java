package model;

import java.util.Random;

public class Yut {

    private final Random random = new Random();
    private String result;

    // 윷 던지기 (각 경우의 확률 설정)
    public String throwYut() {
        int rand = random.nextInt(32); // 0부터 31까지의 랜덤 값 생성

        if (rand < 4) {
            result = "빽도"; // 16.5% (5/32)
        } else if (rand < 11) {
            result = "도"; // 18.75% (6/32)
        } else if (rand < 23) {
            result = "개"; // 37.5% (12/32)
        } else if (rand < 30) {
            result = "걸"; // 25% (8/32)
        } else if (rand < 31) {
            result = "윷"; // 16.5% (5/32)
        } else {
            result = "모"; // 3.125% (1/32)
        }
        return result;
    }

    public String getResult() {
        return result;
    }
}