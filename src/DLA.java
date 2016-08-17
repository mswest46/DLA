import java.lang.Math.*;
import acm.util.*;
import java.util.*;
import java.io.*;

public class DLA { 
  private static final int nNodes = (int) Math.pow(10,6);
  private static final double startRadius = 10;
  private static final RandomGenerator rgen = new RandomGenerator();
  private static final double particleRadius = 1;
  private static final double snapDistance = .01;
  private static final double killRadius = Integer.MAX_VALUE;
  private static final boolean debugOn = false;

  private ArrayList<Node> aggregate = new ArrayList<Node>();

  public static void main(String[] args) {
    DLA dla = new DLA();
    dla.initialize();
    dla.simulate();
    dla.saveData();
  }

  public void initialize() {
    // can change the seed if want. 
    Node seed = new Node(0,0,particleRadius);
    aggregate.add(seed);
    rgen.setSeed(1);
  }

  private void simulate() {
    if (debugOn) System.out.print("simulate\n");
    for (int i = 0; i < nNodes; i++) {
      if (debugOn) System.out.print("\n particle" + i);
      Node diffuser = introduceDiffuser();
      Node sticker = diffuseUntilHit(diffuser);
      if (debugOn) System.out.print("diffuser position" + diffuser.getX() + "  " + diffuser.getY());
      attach(diffuser, sticker);
//      gatherData();
    }
    if (debugOn) radiiList();
  }
  
  private void radiiList() { 
    for (Node node : aggregate) 
      //print radii of vxs for debug
      System.out.print("\n" + (Math.pow(node.getX(),2) + Math.pow(node.getY(),2)));
  }
  
  private Node introduceDiffuser() { 
    if (debugOn) System.out.print("introduceDiffuser\n");
    // should introduce particle as near as possible to the aggregate.
    double R = startRadius;
    double angle = rgen.nextDouble(0,2*Math.PI);
    // System.out.print("particle introduced");
    return new Node(R * Math.cos(angle), R * Math.sin(angle), particleRadius);
  }

  private Node diffuseUntilHit(Node diffuser) { 
    if (debugOn) System.out.print("diffuseUntilHit\n");
    double radius = getNextJumpRadius(diffuser);
    // System.out.print("particle jumping");
    // reintroduce a particle if out of bounds.
    //
    while (!hasParticleHit(radius)) { 
      makeJump(diffuser, radius);
      if (particleOutOfBounds(diffuser)) { 
        diffuser = introduceDiffuser();
      }
      radius = getNextJumpRadius(diffuser);
    }
    Node sticker = diffuser.getClosestNodeInAggregate(aggregate);
    return sticker;
    // System.out.print("particle attached");
  }

  private boolean particleOutOfBounds(Node diffuser) {
    if (debugOn) System.out.print("particleOutOfBounds \n");
    return (diffuser.distanceFromOrigin() > killRadius);
  }

  private void makeJump(Node diffuser, double radius) { 
    if (debugOn) System.out.print("makeJump\n");
    double angle = rgen.nextDouble(0,2*Math.PI);
    diffuser.move(radius * Math.cos(angle), radius * Math.sin(angle));
  }

  private boolean hasParticleHit(double radius) { 
    if (debugOn) System.out.print("hasParticleHit\n");
    return (radius < snapDistance);
  }

  private double getNextJumpRadius(Node diffuser) { 
    if (debugOn) System.out.print("getNextJumpRadius\n");
    Node minNode = diffuser.getClosestNodeInAggregate(aggregate);
    double radius = Math.sqrt(diffuser.getSquareDistanceTo(minNode)) - 
      diffuser.getRadius() - minNode.getRadius();
    return radius;
  }

  private void attach(Node diffuser, Node sticker) {
    if (debugOn) System.out.print("attach\n");
    System.out.print(diffuser.toString() + " attaching to " +  sticker.toString() + "\n");
    diffuser.snapTo(sticker);
    sticker.addNeighbor(diffuser);
    diffuser.addNeighbor(sticker);
    aggregate.add(diffuser);
  }
  private void gatherData() { 
    int n = aggregate.size();
    double[] xPositions = new double[n];
    double[] yPositions = new double[n];
    for (int i = 0; i < n; i++) {
      xPositions[i] = aggregate.get(i).getX();
      yPositions[i] = aggregate.get(i).getY();
    }
  }

  private void saveData() {
    try { 
      FileOutputStream fileOut = new FileOutputStream("../data/hey.ser");
      ObjectOutputStream out = new ObjectOutputStream(fileOut);
      out.writeObject(aggregate);
      out.close();
      fileOut.close();
    }
    catch (IOException e) { 
      e.printStackTrace();
    }
  }
}
