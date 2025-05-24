package controller.swing;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BoardLayoutCalculator {
    private final int numSides;
    private final Point center;
    private final int radius;

    public BoardLayoutCalculator(int numSides, Point center, int radius) {
        this.numSides = numSides;
        this.center = center;
        this.radius = radius;
    }

    public List<Point> calculateVertices() {
        List<Point> vertices = new ArrayList<>();
        double startAngle;
        switch (numSides) {
            case 4: startAngle = Math.PI / 4; break;
            case 5: startAngle = Math.PI / 2 + Math.PI / 5; break;
            case 6: startAngle = 0; break;
            default: startAngle = -Math.PI / 2;
        }

        for (int i = 0; i < numSides; i++) {
            double angle = 2 * Math.PI * i / numSides + startAngle;
            int x = center.x + (int) (radius * Math.cos(angle));
            int y = center.y + (int) (radius * Math.sin(angle));
            vertices.add(new Point(x, y));
        }

        return vertices;
    }

    public List<Point> calculateIntermediatePoints(Point from, Point to, int divisions, boolean includeEnds) {
        List<Point> points = new ArrayList<>();
        int start = includeEnds ? 0 : 1;
        int end = includeEnds ? divisions : divisions - 1;

        for (int i = start; i <= end; i++) {
            double t = i / (double) divisions;
            int x = (int) (from.x * (1 - t) + to.x * t);
            int y = (int) (from.y * (1 - t) + to.y * t);
            points.add(new Point(x, y));
        }

        return points;
    }

    public Point findStartPoint(List<Point> vertices) {
        Point start = vertices.get(0);
        for (Point point : vertices) {
            if ((numSides == 6 && point.y > start.y) ||
                    (numSides == 5 && point.y >= start.y && point.x >= start.x) ||
                    (numSides != 5 && numSides != 6 && point.y >= start.y && point.x >= start.x)) {
                start = point;
            }
        }
        return start;
    }
}
