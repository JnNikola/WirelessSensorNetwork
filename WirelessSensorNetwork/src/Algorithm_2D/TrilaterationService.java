package Algorithm_2D;

import java.util.List;

public final class TrilaterationService{

    public static double [] trilaterate(List<Point> points) {
        // copy values because we might swap coordinates
        double r1 = points.get(0).getR();
        double r2 = points.get(1).getR();
        double r3 = points.get(2).getR();

        double v1x = points.get(0).getX();
        double v1y = points.get(0).getY();
        double v2x = points.get(1).getX();
        double v2y = points.get(1).getY();
        double v3x = points.get(2).getX();
        double v3y = points.get(2).getY();

        if (v2x == v3x) {
            double tmpX = v2x;
            double tmpY = v2y;
            double tmpR = r2;
            v2x = v1x;
            v2y = v1y;
            r2 = r1;
            v1x = tmpX;
            v1y = tmpY;
            r1 = tmpR;
        }
        // still equal, than exit => all have same x-coordinate
        if (v2x == v3x) {
            return null;
        }
        double r2Squared = r2*r2;
        double v2xSquared = v2x*v2x;
        double v2ySquared = v2y*v2y;
        double s = (v3x*v3x - v2xSquared + v3y*v3y - v2ySquared + r2Squared - r3*r3) / 2.0;
        double t = (v1x*v1x - v2xSquared + v1y*v1y - v2ySquared + r2Squared - r1*r1) / 2.0;
        double div = (((v1y - v2y)*(v3x - v2x)) - ((v3y - v2y)*(v1x - v2x)));
        if (div == 0) {
            return null;
        }
        double y = ((t * (v3x - v2x) - s * (v1x - v2x))) / div;
        double x =  (s - y * (v3y - v2y)) / (v3x - v2x);
        double [] result = new double[2];
        result[0]=x;
        result[1]=y;
        return result;
    }
}
