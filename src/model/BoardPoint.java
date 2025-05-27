package model;

import java.util.Objects;

public class BoardPoint {
    private final int x;
    private final int y;

    public BoardPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }

    public java.awt.Point toAwtPoint() {
        return new java.awt.Point(x, y);
    }

    public javafx.geometry.Point2D toJavaPoint() {
        return new javafx.geometry.Point2D(x, y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BoardPoint boardPoint)) return false;
        return x == boardPoint.x && y == boardPoint.y;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
