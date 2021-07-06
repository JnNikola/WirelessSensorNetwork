package Algorithm_3D;

public class Point3D {
    private double x,y, z, r;
    private int degree;
    public Point3D(double x, double y, double z, double r, int degree){
        this.x =x;
        this.y=y;
        this.z=z;
        this.r=r;
        this.degree=degree;
    }
    public double getX(){return x;}
    public double getY(){return y;}
    public double getZ(){return z;}
    public double getR(){return r;}

    public int getDegree() {
        return degree;
    }

    @Override
    public String toString() {
        return "Point3D{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", r=" + r +
                '}';
    }
}

