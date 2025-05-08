package model;

import java.util.Random;

public class Yut {

    private final Random random = new Random();
    private YutResult result;

    // 윷 던지기 (각 경우의 확률 설정)
    public String getRandomResult() {
        int rand = random.nextInt(32); // 0부터 31까지의 랜덤 값 생성

        if (rand < 4) {
            result = YutResult.BackDo;
        } else if (rand < 11) {
            result = YutResult.DO;
        } else if (rand < 23) {
            result = YutResult.GAE;
        } else if (rand < 30) {
            result = YutResult.GUL;
        } else if (rand < 31) {
            result = YutResult.YUT;
        } else {
            result = YutResult.MO;
        }
        return result.toString();
    }

    // 마지막 던진 결과 반환
    public YutResult getResult() {
        return result;
    }

    // 수동으로 결과 설정
    public void setManualResult(YutResult result) {
        this.result = result;
    }
}