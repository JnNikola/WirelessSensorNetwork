package Algorithm_3D;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class NodeBase {

    /**
     * Simulates node localization in 3D space and returns average error of n simulations
     * and the fraction of localized nodes
     *
     * Grid size, radio range, radio range error, number of known and unknown nodes, iterative or non-iterative
     * algorithm is chosen manually
     *
     */


    public static void main(String[] args) throws InterruptedException {

        //size of grid
        int maxX=200;
        int maxY=200;
        int maxZ=200;

        //radiorange in percent
        double radioRange=50;                 //input your desired radio range in % of the grid size

        //radiorange error in percent
        double radioRangeError=20;           //input your desired radio range error

        int nNodes=150;                      //input your desired number of nodes
        int percentAnchors=30;               //input your desired number of anchors in % of all nodes

        boolean isIter=false;                //should it do iterative alg. (non-iterative by default)


        boolean degHu=false;                 //should it do degree determination heuristic (distance by default)
        
        int nSim=15;                         //number of simulations

        Distributors.setError(radioRangeError);

        double averageError = 0;
        double averageCalcFraction=0;

        radioRange= (maxX*radioRange)/100; //radiorange from percent to meters


        for (int i=1; i<=nSim; i++){

//            System.out.println("\n"+i+"\n");
            NodeBase nb= new NodeBase(maxX, maxY, maxZ, radioRange, nNodes, percentAnchors, degHu);
            nb.init(isIter);

            averageError+=nb.determineError();
//            System.out.println(nb.determineClcFraction());
            averageCalcFraction+=nb.determineClcFraction();

        }
        averageError/=nSim;
        averageCalcFraction/=nSim;
        double RRNormalizedError= averageError * (100/radioRange);

        System.out.printf("Average error is: %.2f meters\n", averageError);
        System.out.printf("Average RR normalized error is: %.2f%%\n", RRNormalizedError);
        System.out.printf("Average calc fraction is: %.2f%%\n", averageCalcFraction);
    }


    private List<Known> knowns;
    private List<Unknown> unknowns;
    private final int maxX, maxY, maxZ;
    public static double RADIORANGE;
    private  final boolean degHu;
    private final int nAnch;
    private int percentAnchors;
    private final int nUnknowns;
    private final int nNodes;

    public NodeBase(int maxX, int maxY, int maxZ, double rr, int nNodes, int percentAnchors, boolean degHu){
        this.knowns = new ArrayList<>();
        this.unknowns= new ArrayList<>();
        this.maxX=maxX;
        this.maxY=maxY;
        this.maxZ=maxZ;
        RADIORANGE=rr;
        this.degHu=degHu;
        this.nAnch= nNodes * percentAnchors/100;
        this.nUnknowns= nNodes-nAnch;
        this.nNodes= nNodes;
    }

    //creates anchors and unknowns, calculates positions, does iterations
    public void init(boolean isIter) throws InterruptedException {

        fillAnchors(nAnch);
//        System.out.println("\n\nNUMBER OF ANCHORS: "+ knowns.size()+"\n");
        fillUnknowns(nUnknowns);
//        System.out.println("\n\nNUMBER OF UNKNOWNS: "+ unknowns.size()+"\n");

        // if is iterative it does max 100 iterations
        int nIter=1;
        if (isIter){
            nIter=100;
        }

        int i=1;
        while (!unknowns.isEmpty() && i<=nIter) {

//            System.out.println("\nITERATION NO: " + i + "\n");
            determineAnchors();

    //        unknowns.forEach(System.out::println);

            calculatePositions();

//            System.out.println("\n\nUNKNOWNS:\n\n");
    //      unknowns.forEach(System.out::println);
//            System.out.println("\nNUMBER OF UNKNOWNS LEFT: " + unknowns.size() + "\n");

//            System.out.println("\n\nKNOWNS:\n\n");
    //      knowns.forEach(System.out::println);
//            System.out.println("\nNUMBER OF KNOWNS NOW: " + knowns.size() + "\n");

            i++;

        }

    }


    public double determineError(){
        return knowns.stream().filter(u -> u.getType().equals(TYPE.discovered)).mapToDouble(Known::determineError).sum()/knowns.size();
    }

    public double determineClcFraction(){
        return (((double)(knowns.size()-nAnch))/nUnknowns)*100;
    }


    public void fillAnchors(int numAnchors){
        IntStream.range(0, numAnchors).forEach(i-> knowns.add(new Anchor(maxX, maxY, maxZ)));
//        System.out.println("fill Anchors done");

    }

    public void fillUnknowns(int numUnknowns){
        IntStream.range(0, numUnknowns).forEach(i-> unknowns.add(new UnknownNode(maxX, maxY, maxZ, RADIORANGE)));
//        System.out.println("fill Unknowns done");
    }

    public void determineAnchors(){
        unknowns.forEach(u-> u.setAnchors(knowns, degHu));
//        System.out.println("determining knowns done");
    }

    public void calculatePositions(){
        List<Unknown> toRemove= new ArrayList<>();
        unknowns.forEach(u->{
            Known discovered= u.calculatePosition();
            if (discovered != null){
                knowns.add(discovered);
                toRemove.add(u);
            }
        });
        toRemove.forEach(t-> unknowns.remove(t));
    }

}
