package model;

import java.util.Random;

public class Yut {

    private final Random random = new Random();
    private YutResult result;

    // 윷 던지기 (각 경우의 확률 설정)
    public String getRandomResult() {
        int rand = random.nextInt(100); // 0부터 31까지의 랜덤 값 생성

        if (rand < 6.25) {
            result = YutResult.BackDo;
        } else if (rand < 25) {
            result = YutResult.DO;
        } else if (rand < 62.5) {
            result = YutResult.GAE;
        } else if (rand < 87.5) {
            result = YutResult.GUL;
        } else if (rand < 93.75) {
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