package Algorithm_3D;

import Algorithm_2D.Point;
import Algorithm_2D.TrilaterationService;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

enum TYPE{
    anchor,
    discovered,
    unknown;
}


abstract class Unknown {
    public abstract double getX();

    public abstract double getY();

    public abstract double getZ();

    public abstract int getDegree();

    public abstract void setAnchors(List<Known> anchors, boolean degreeHu);

    public abstract Known calculatePosition();

    public abstract TYPE getType();

}

abstract class Known{

    public abstract double getTrueX();

    public abstract double getTrueY();
    
    public abstract double getTrueZ();

    public abstract int getDegree();

    public abstract TYPE getType();

    public abstract double determineError();
}

class Anchor extends Known {

    private final String ID;
    private final TYPE type;
    private final double x, y, z;
    
    

    public Anchor(int maxX, int maxY, int maxZ){
        this.x= ThreadLocalRandom.current().nextDouble(0, maxX);
        this.y=ThreadLocalRandom.current().nextDouble(0, maxY);
        this.z= ThreadLocalRandom.current().nextDouble(0, maxZ);
        type = TYPE.anchor;
        this.ID= IDCreator.createID();
    }

    public double getTrueX(){
        return x;
    }

    public double getTrueY(){
        return y;
    }
    
    public double getTrueZ() {return z;}

    @Override
    public int getDegree() {
        return 0;
    }

    @Override
    public TYPE getType() {
        return type;
    }

    @Override
    public double determineError() {
        return 0;
    }

    @Override
    public String toString() {
        return String.format("Type: Algorithm_2D.Anchor, ID: %s, coordinates are: x: %.3f, y: %.3f",ID.toString(), x, y);
    }
}

class Discovered extends Known{

    private final String ID;
    private final TYPE type;
    private final int degree;
    private final double trueX;
    private final double trueY;
    private final double trueZ;
    private double xP, yP, zP;


    Discovered(String id, int degree, double trueX, double trueY, double trueZ, double [][] calculatedLocation) {
        ID = id;
        this.degree = degree;
        this.type = TYPE.discovered;
        this.trueX = trueX;
        this.trueY = trueY;
        this.trueZ= trueZ;
        this.xP = calculatedLocation[0][0];
        this.yP = calculatedLocation[1][0];
        this.zP = calculatedLocation[2][0];
    }

    public double getxP() {
        return xP;
    }

    public double getyP() {
        return yP;
    }
    
    public double getzP() {
        return zP;
    }

    @Override
    public double getTrueX() {
        return trueX;
    }

    @Override
    public double getTrueY() {
        return trueY;
    }

    @Override
    public double getTrueZ() {
        return trueZ;
    }

    @Override
    public int getDegree() {
        return degree;
    }

    @Override
    public TYPE getType() {
        return type;
    }

    @Override
    public double determineError() {
        return Math.abs(DistanceCalculator.calculateDistanceBetweenPoints(trueX, trueY, trueZ, xP, yP, zP));
    }

    @Override
    public String toString() {
        return String.format("Type: Discovered, ID: %s, Degree: %d, real coordinates are: x: %.3f, y: %.3f, z: %.3f , " +
                " calculated coordinates are: x: %.3f, y: %.3f, z: %.3f", ID.toString(), getDegree(), trueX, trueY, trueZ, xP, yP, zP);
    }
}

class UnknownNode extends Unknown {

    private final String ID;
    private final TYPE type;
    private final double x, y, z;
    

    private Map<Known, Double> knownsMap;
    private final double rr;
    private final int degree;


    public UnknownNode(int maxX, int maxY, int maxZ, double rr){
        type = TYPE.unknown;
        this.x= ThreadLocalRandom.current().nextDouble(0, maxX);
        this.y=ThreadLocalRandom.current().nextDouble(0, maxY);
        this.z=ThreadLocalRandom.current().nextDouble(0, maxY);
        this.knownsMap = new HashMap<>();
        this.rr=rr;
        this.ID= IDCreator.createID();
        this.degree=0;

    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public double getZ() {
        return z;
    }

    @Override
    public TYPE getType() {
        return type;
    }

    @Override
    public int getDegree() {
        return degree;
    }

    @Override
    public void setAnchors(List<Known> knowns, boolean degreeHu){

        //put anchors in radio range with error in a map
        for (Known a: knowns){
            double distance= DistanceCalculator.calculateDistanceBetweenPoints(x, y, z, a.getTrueX(), a.getTrueY(), a.getTrueZ());

            distance= Distributors.genStudent(distance);
            if (distance<=rr){

                    knownsMap.putIfAbsent(a, distance);

            }
        }

        //sort anchors by value(distance/degree) ascending
        knownsMap = knownsMap.entrySet().stream()
                .sorted((e1, e2) -> {
                    //if comparing by degree
                    if (degreeHu){
                        int rez= Integer.compare(e1.getKey().getDegree(), e2.getKey().getDegree());   
                        if(rez==0) return e1.getValue().compareTo(e2.getValue());    //if degree is same, compare by distance
                        return rez;
                    }else return e1.getValue().compareTo(e2.getValue());  //if comparing by distance

                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));


    }

    //calculates position and returns a new discovered class
    public Known calculatePosition(){

        //if no 3 knowns in radio range do nothing
        if (knownsMap.size()<4) return null;

        //get points from 4 closest knowns and its degree

        double[] distances = new double[4];
        double[][] locations = new double[4][3];
        int [] degrees= new int[4];
        int i=0;

        for (Map.Entry<Known, Double> entry : knownsMap.entrySet()){
            if (i>=4) break;
            
            locations[i][0]=entry.getKey().getTrueX();
            locations[i][1]=entry.getKey().getTrueY();
            locations[i][2]=entry.getKey().getTrueZ();
            
            distances[i]= entry.getValue();

            degrees[i]= entry.getKey().getDegree();
            
            i++;
        }

        TrilaterationService3D trilaterationService3D= new TrilaterationService3D();

        double[][] calculatedlocation = trilaterationService3D.calculateLocation(distances, locations);

        return new Discovered(ID, calcDegree(degrees), x, y, z, calculatedlocation);
    }

    //calculates degree of discovered node
    private int calcDegree(int[] degrees){
        return Arrays.stream(degrees).sum()+1;
    }

//    public Map<Algorithm_2D.Known, Double> getKnownsMap(){
//        return knownsMap;
//    }

    @Override
    public String toString() {
        StringBuilder stringBuilder= new StringBuilder();
        for (Map.Entry<Known, Double> entry : knownsMap.entrySet()) {
            stringBuilder.append(entry.getKey().toString()).append(" Distance ");
            stringBuilder.append(entry.getValue().toString())
            .append("; ");
        }
        return "Algorithm_2D.Unknown node with ID: " + ID.toString() +
                " Knowns= {" + stringBuilder.toString() +
                '}';
    }
}






