package Algorithm_2D;

public class Point{
    private double x,y,r;
    private int degree;
    public Point(double x,double y,double r, int degree){
        this.x =x;
        this.y=y;
        this.r=r;
        this.degree=degree;
    }
    public double getX(){return x;}
    public double getY(){return y;}
    public double getR(){return r;}

    public int getDegree() {
        return degree;
    }

    @Override
    public String toString() {
        return "Algorithm_2D.Point{" +
                "x=" + x +
                ", y=" + y +
                ", r=" + r +
                '}';
    }
}

