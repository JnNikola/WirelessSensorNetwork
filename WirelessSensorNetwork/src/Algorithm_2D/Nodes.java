package Algorithm_2D;

import org.apache.commons.math3.linear.RealVector;

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

    public abstract int getDegree();

    public abstract void setAnchors(List<Known> anchors, boolean degreeHu);

    public abstract Known calculatePosition();

    public abstract TYPE getType();

}

abstract class Known{

    public abstract double getTrueX();

    public abstract double getTrueY();

    public abstract int getDegree();

    public abstract TYPE getType();

    public abstract double determineError();
}

class Anchor extends Known {

    private final String ID;
    private final TYPE type;
    private final double x;
    private final double y;

    public Anchor(int maxX, int maxY){
        this.x= ThreadLocalRandom.current().nextDouble(0, maxX);
        this.y=ThreadLocalRandom.current().nextDouble(0, maxY);
        type = TYPE.anchor;
        this.ID= IDCreator.createID();
    }

    public double getTrueX(){
        return x;
    }

    public double getTrueY(){
        return y;
    }

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
        return String.format("Type: Algorithm_2D.Anchor, ID: %s, coordinates are: x: %.3f, y: %.3f", ID, x, y);
    }
}

class Discovered extends Known{

    private final String ID;
    private final TYPE type;
    private final int degree;
    private final double trueX;
    private final double trueY;
    private final double xP;
    private final double yP;


    Discovered(String id, int degree, double trueX, double trueY, double xP, double yP) {
        ID = id;
        this.degree = degree;
        this.type = TYPE.discovered;
        this.trueX = trueX;
        this.trueY = trueY;
        this.xP = xP;
        this.yP = yP;
    }

    public double getxP() {
        return xP;
    }

    public double getyP() {
        return yP;
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
    public int getDegree() {
        return degree;
    }

    @Override
    public TYPE getType() {
        return type;
    }

    @Override
    public double determineError() {
        return Math.abs(DistanceCalculator.calculateDistanceBetweenPoints(trueX, trueY, xP, yP));
    }

    @Override
    public String toString() {
        return String.format("Type: Algorithm_2D.Discovered, ID: %s, Degree: %d, real coordinates are: x: %.3f, y: %.3f ,  " +
                " calculated coordinates are: x: %.3f, y: %.3f", ID, getDegree(), trueX, trueY, xP, yP);
    }
}

class UnknownNode extends Unknown {

    private final String ID;
    private final TYPE type;
    private final double x;
    private final double y;

    private Map<Known, Double> knownsMap;
    private final double rr;
    private final int degree;


    public UnknownNode(int maxX, int maxY, double rr){
        type = TYPE.unknown;
        this.x= ThreadLocalRandom.current().nextDouble(0, maxX);
        this.y=ThreadLocalRandom.current().nextDouble(0, maxY);
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
    public TYPE getType() {
        return type;
    }

    @Override
    public int getDegree() {
        return degree;
    }

    @Override
    public void setAnchors(List<Known> knowns,boolean degreeHu){

        //put anchors in radio range with error in a map
        //TODO: get calculated x and y for discovered nodes if u do iterations
        for (Known a: knowns){
            double distance= DistanceCalculator.calculateDistanceBetweenPoints(x, y, a.getTrueX(), a.getTrueY());

            distance= Distributors.genStudent(distance);
            if (distance<=rr){

                    knownsMap.putIfAbsent(a, distance);

            }
        }

        //sort anchors by value(distance/degree) ascending
        knownsMap = knownsMap.entrySet().stream()
                .sorted((e1, e2) -> {

                    if (degreeHu){
                        int rez= Integer.compare(e1.getKey().getDegree(), e2.getKey().getDegree());
                        if(rez==0) return e1.getValue().compareTo(e2.getValue());
                        return rez;
                    }else return e1.getValue().compareTo(e2.getValue());

                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));


    }

    //UNUSED OLD ALGORITHM
//    public Known calculatePosition(){
//
//        //if no 3 knowns in radio range do nothing
//        if (knownsMap.size()<3) return null;
//
//        //get points from 3 closest knowns and its degree
//        List<Point> points = new LinkedList<>();
//        int i=0;
//        for (Map.Entry<Known, Double> entry : knownsMap.entrySet()){
//            if (i>=3) break;
//            i++;
//            points.add(new Point(entry.getKey().getTrueX(), entry.getKey().getTrueY(), entry.getValue(), entry.getKey().getDegree()));
//        }
//
//        double[] co= TrilaterationService.trilaterate(points);
//
//        return new Discovered(ID, calcDegree(points.get(0).getDegree(), points.get(1).getDegree(), points.get(2).getDegree()), x, y, co[0], co[1]);
//    }

    //calculate position of unknown
    public Known calculatePosition(){

        //if no 3 knowns in radio range do nothing
        if (knownsMap.size()<3) return null;


        double[] distances = new double[3];
        double[][] locations = new double[3][2];
        int [] degrees= new int[3];
        int i=0;
        for (Map.Entry<Known, Double> entry : knownsMap.entrySet()){
            if (i>=3) break;

            locations[i][0]=entry.getKey().getTrueX();
            locations[i][1]=entry.getKey().getTrueY();

            distances[i]= entry.getValue();

            degrees[i]= entry.getKey().getDegree();


            i++;
        }

        TrilaterationFunction trilaterationFunction= new TrilaterationFunction(locations, distances);
        LinearLeastSquaresSolver linearLeastSquaresSolver= new LinearLeastSquaresSolver(trilaterationFunction);
        RealVector solution= linearLeastSquaresSolver.solve();
        return new Discovered(ID, calcDegree(degrees), x, y, solution.getEntry(0), solution.getEntry(1));
    }

    //calculates degree of discovered node
    private int calcDegree(int [] degrees){
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
        return "Algorithm_2D.Unknown node with ID: " + ID +
                " Knowns= {" + stringBuilder.toString() +
                '}';
    }
}






