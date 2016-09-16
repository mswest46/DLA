package DLA;

import java.lang.Math.*;
import acm.util.*;
import java.util.*;
import java.io.*;
import javax.swing.SwingUtilities;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics; 
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseMotionAdapter;
import java.lang.Thread;
import quadtree.*;

public class DLA { 

  /*
   * aggregate variables that are useful in general.
   */
  private int nNodes;
  private double aggregateRadius; // the maximum distance of any aggregated particle from the origin
  private List<Node> nodeList = new ArrayList<Node>();
  private Quadtree<Node> qt;

  /* 
   * variables that are useful only in simulation. 
   */
  private static final int PAUSE_TIME = 30;
  private static final double startRadius = 10;
  private static final double particleRadius = 10;
  private static final double snapDistance = .01;
  private static final double killRadius = Integer.MAX_VALUE;

  /*
   *
   */
  private double X_MIN = -100000;
  private double Y_MIN = -100000;
  private double AGG_WIDTH = 200000;
  private double AGG_HEIGHT = 200000;

  /*
   * Random generator.
   */
  private static final RandomGenerator rgen = new RandomGenerator();

  private static final boolean debugOn = false;
  private int nodeNumber = 0;
  private boolean animateOn;
  private static MyPanel thePanel;

  /*
   * Construct a DLA aggregate with nNodes 
   */
  public DLA(int nNodes, boolean animateOn) {
    this.nNodes = nNodes;
    this.animateOn = animateOn;
    initialize();
    simulate();
  }

  public void initialize() {
    // can change the seed if want. 
    Node seed = new Node(0,0,particleRadius);
    nodeList.add(seed);
    qt = new Quadtree<Node> (5, X_MIN, Y_MIN, AGG_WIDTH, AGG_HEIGHT);
    qt.insert(seed.toPoint());
    if (animateOn) { 
      createAndShowGUI();
      thePanel.updateNodeList(nodeList);
    }
    aggregateRadius = 0;
    rgen.setSeed(1);


  }
  private static void createAndShowGUI(){
        JFrame f = new JFrame("DLA");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        thePanel = new MyPanel();
        f.add(thePanel);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
  }

  private void simulate() {
    if (debugOn) System.out.print("simulate\n");
    for (int i = 0; i < nNodes; i++) {
      if (debugOn) System.out.print("\n particle" + i);
      nodeNumber++;
      Node diffuser = introduceDiffuser();
      Node sticker = diffuseUntilHit(diffuser);
      attach(diffuser, sticker);
      System.out.print("Node number " + (i+1) + " attached.\n");
    }
    System.out.print("\nDUNZO");
  }
  
  private Node introduceDiffuser() { 
    if (debugOn) System.out.print("introduceDiffuser\n");
    // should introduce particle as near as possible to the aggregate.
    double R = aggregateRadius + 2 * particleRadius; // close as possible without risking ;w
    double angle = rgen.nextDouble(0,2*Math.PI);
    Node diffuser = new Node(R * Math.cos(angle), R * Math.sin(angle), particleRadius);
    if (animateOn) {
      thePanel.setDiffuser(diffuser);
    }
    return diffuser;
  }
  private void replaceDiffuser(Node diffuser) {
    double R = aggregateRadius + 2 * particleRadius; // close as possible without risking ;w
    double angle = rgen.nextDouble(0,2*Math.PI);
    diffuser.setLocation(R * Math.cos(angle), R * Math.sin(angle));
  }


  private Node diffuseUntilHit(Node diffuser) { 
    if (debugOn) System.out.print("diffuseUntilHit\n");
    // reintroduce a particle if out of bounds.
    

     PointDistancePair<Node> distanceNodePair = getDistanceNodePair(diffuser);
     if (animateOn) {
       thePanel.setNearestNode(distanceNodePair.getPoint().data);
     }
     double distance = takeParticleSizeIntoAccount(distanceNodePair.getDistance());

     while (true) { 
       makeJump(diffuser, distance);
       if (particleOutOfBounds(diffuser)) { 
         // really shitty. change up. just seeing if it works. 
         replaceDiffuser(diffuser);
       }
       distanceNodePair = getDistanceNodePair(diffuser);
       if (animateOn) {
         thePanel.setNearestNode(distanceNodePair.getPoint().data);
       }
       distance = takeParticleSizeIntoAccount(distanceNodePair.getDistance());

       if (hasParticleHit(distance)) break;
     }
     Node sticker = distanceNodePair.getPoint().data;
     return sticker;
  }

  /*
   * takes in the distance between the centers of two nodes and returns the distance between the edges of two nodes. 
   * TODO: assuming for now that particles have the same radius. Not sure I always will. 
   */
  private double takeParticleSizeIntoAccount(double distance) {
    return distance - 2 * particleRadius;
  }

  /*
   * jumps the diffuser in a random direction a distance of distance
   */
  private void makeJump(Node diffuser, double distance) { 
    if (debugOn) System.out.print("makeJump\n");
    double angle = rgen.nextDouble(0,2*Math.PI);
    double oldX = diffuser.getX();
    double oldY = diffuser.getY();
    diffuser.move(distance * Math.cos(angle), distance * Math.sin(angle));
    if (animateOn) {
      try {
        Thread.sleep(PAUSE_TIME);
      } catch (Exception e) {
        System.out.print(e);
      }
      thePanel.moveNode(oldX, oldY, diffuser.getX(), diffuser.getY());
    }
      
  }

  /* 
   * returns a point-distance pair. The point is the point closest to the diffuser, and the
   * distance is the distance between the CENTERS of the two points. 
   */

  private PointDistancePair<Node> getDistanceNodePair(Node diffuser) {
    if (debugOn) System.out.print("getNextJumpRadius\n");
    PointDistancePair<Node> cpdp = qt.closestPointDistancePair(diffuser.toPoint());

    if (cpdp.getDistance() < 2 * particleRadius) {
      throw new EmptyStackException();
    }
    

    return cpdp;

    // Node closestNode = nodeList.get(0);
    // double squareDistance = getSquareDistanceBetween(closestNode, diffuser);
    // for (Node node : nodeList) { 
    //   if (getSquareDistanceBetween(node, diffuser) < squareDistance) {
    //     closestNode = node;
    //     squareDistance = getSquareDistanceBetween(node, diffuser);
    //   }
    // } 
    // double distance = Math.sqrt(squareDistance) - 
    //   diffuser.getRadius() - closestNode.getRadius();
  }


  private void attach(Node diffuser, Node sticker) {
    if (debugOn) {
      System.out.print("attaching \n");
      System.out.print(diffuser.toString() + " attaching to " +  sticker.toString() + "\n");
    }
    //snapTo(diffuser, sticker);
    sticker.addNeighbor(diffuser);
    diffuser.addNeighbor(sticker);
    if (animateOn) {
      try {
        Thread.sleep(PAUSE_TIME);
      } catch (Exception e) {
        System.out.print(e);
      }
    }
    nodeList.add(diffuser);
    qt.insert(diffuser.toPoint());
    if (animateOn) {
      thePanel.updateNodeList(nodeList);
    }

    // update aggregate radius if it has grown. 
    if (diffuser.getDistanceFromOrigin() > aggregateRadius) {
      aggregateRadius = diffuser.getDistanceFromOrigin();
    }
  }

  private double getSquareDistanceBetween(Node node1, Node node2) {
    return  Math.pow(node1.getX() - node2.getX(),2) + Math.pow(node1.getY() - node2.getY(),2);
  }

  private boolean particleOutOfBounds(Node diffuser) {
    if (debugOn) {
      if (diffuser.getDistanceFromOrigin() > killRadius) {
        System.out.print("particle out of bounds \n");
      }
    }
    return (diffuser.getDistanceFromOrigin() > killRadius);
  }

  /*
   * distance is the smallest distance between the edge of the diffuser and the edge of the 
   * closest node in the aggregate. 
   * snapDistance is the minimum distance between particle boundaries for which we attach.
   */
  private boolean hasParticleHit(double distance ) { 
    if (debugOn) 
      if (distance < snapDistance) {
        System.out.print("Particle has hit\n");
      }
    return (distance < snapDistance);
  }

  private void snapTo(Node diffuser, Node sticker) {
    double difX = diffuser.getX() - sticker.getX();
    double difY = diffuser.getY() - sticker.getY();
    double distance = getSquareDistanceBetween(diffuser, sticker);
    double newDistance = diffuser.getRadius() + sticker.getRadius();
    double x = (1 - newDistance/distance) * difX; 
    double y = (1 - newDistance/distance) * difY; 
    diffuser.move(x,y);
  }

//  private void saveData() {
//    try { 
//      FileOutputStream fileOut = new FileOutputStream("../data/hey.ser");
//      ObjectOutputStream out = new ObjectOutputStream(fileOut);
//      out.writeObject(nodeList);
//      out.close();
//      fileOut.close();
//    }
//    catch (IOException e) { 
//      e.printStackTrace();
//    }
//  }

}
