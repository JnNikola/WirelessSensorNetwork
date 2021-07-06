package Algorithm_2D;

public final class DistanceCalculator {

    public static double calculateDistanceBetweenPoints( double x1, double y1, double x2, double y2) {
        return Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
    }
}
