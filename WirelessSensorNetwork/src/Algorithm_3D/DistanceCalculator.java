package Algorithm_3D;

public final class DistanceCalculator {

    public static double calculateDistanceBetweenPoints( double x1, double y1, double z1, double x2, double y2, double z2) {
        return Math.pow((Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2) + Math.pow(z2 - z1, 2) * 1.0), 0.5);
    }

}
