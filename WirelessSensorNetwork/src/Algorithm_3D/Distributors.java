package Algorithm_3D;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public final class Distributors {

    private static double rrError=0;
    private static final Random rnd = new Random();

    public static void setError(double error){
        rrError=error;
    }

    //returns radiorange with a gaussian distribution error
    public static double generate(double distance){
        return (rrError * rnd.nextGaussian()) + distance;
    }

    //returns radiorange with a student distribution error
    public static double genStudent(double distance){
        double lowerBound = distance - distance * rrError/100;
        double upperBound = distance + distance * rrError/100;
        if (rrError==0) return distance;
        return ThreadLocalRandom.current().nextDouble(lowerBound, upperBound);

    }
}
