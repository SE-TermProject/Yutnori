package controller.fx;

import javafx.geometry.Point2D;
import java.util.ArrayList;
import java.util.List;

public class BoardLayoutCalculator {
    private final int numSides;
    private final Point2D center;
    private final double radius;

    public BoardLayoutCalculator(int numSides, Point2D center, double radius) {
        this.numSides = numSides;
        this.center = center;
        this.radius = radius;
    }

    public java.util.List<Point2D> calculateVertices() {
        java.util.List<Point2D> vertices = new ArrayList<>();
        double startAngle;
        switch (numSides) {
            case 4: startAngle = Math.PI / 4; break;
            case 5: startAngle = Math.PI / 2 + Math.PI / 5; break;
            case 6: startAngle = 0; break;
            default: startAngle = -Math.PI / 2;
        }

        for (int i = 0; i < numSides; i++) {
            double angle = 2 * Math.PI * i / numSides + startAngle;
            double x = center.getX() + (radius * Math.cos(angle));
            double y = center.getY() + (radius * Math.sin(angle));
            vertices.add(new Point2D(x, y));
        }

        return vertices;
    }

    public java.util.List<Point2D> calculateIntermediatePoints(Point2D from, Point2D to, int divisions, boolean includeEnds) {
        java.util.List<Point2D> points = new ArrayList<>();
        int start = includeEnds ? 0 : 1;
        int end = includeEnds ? divisions : divisions - 1;

        for (int i = start; i <= end; i++) {
            double t = i / (double) divisions;
            double x = (from.getX() * (1 - t) + to.getX() * t);
            double y = (from.getY() * (1 - t) + to.getY() * t);
            points.add(new Point2D(x, y));
        }

        return points;
    }

    public Point2D findStartPoint(List<Point2D> vertices) {
        Point2D start = vertices.get(0);
        for (Point2D point : vertices) {
            if ((numSides == 6 && point.getY() > start.getY()) ||
                    (numSides == 5 && point.getY() >= start.getY() && point.getX() >= start.getX()) ||
                    (numSides != 5 && numSides != 6 && point.getY() >= start.getY() && point.getX() >= start.getX())) {
                start = point;
            }
        }
        return start;
    }
}
