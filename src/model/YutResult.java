package model;

public enum YutResult {
    BackDo(-1),
    DO(1),
    GAE(2),
    GUL(3),
    YUT(4),
    MO(5);

    private final int step;

    YutResult(int step) {
        this.step = step;
    }

    public int getStep() {
        return step;
    }

    public boolean isBonusTurn() {
        // 윷이나 모는 한 번 더 던질 수 있는 조건
        return this == YUT || this == MO;
    }

    public String getKoreanName() {
        switch (this) {
            case BackDo: return "빽도";
            case DO:     return "도";
            case GAE:    return "개";
            case GUL:    return "걸";
            case YUT:    return "윷";
            case MO:     return "모";
            default:     return name(); // 혹시 모를 fallback
        }
    }
}
