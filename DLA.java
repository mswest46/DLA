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

public class DLA { 

  /*
   * aggregate variables that are useful in general.
   */
  private int nNodes;
  private double aggregateRadius; // the maximum distance of any aggregated particle from the origin
  private List<Node> nodeList = new ArrayList<Node>();

  /* 
   * variables that are useful only in simulation. 
   */
  private static final double startRadius = 10;
  private static final double particleRadius = 10;
  private static final double snapDistance = .01;
  private static final double killRadius = Integer.MAX_VALUE;

  /*
   * Random generator.
   */
  private static final RandomGenerator rgen = new RandomGenerator();

  private static final boolean debugOn = false;
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
      Node diffuser = introduceDiffuser();
      Node sticker = diffuseUntilHit(diffuser);
      if (debugOn) System.out.print("diffuser position" + diffuser.getX() + "  " + diffuser.getY());
      attach(diffuser, sticker);
    }
  }
  
  private Node introduceDiffuser() { 
    if (debugOn) System.out.print("introduceDiffuser\n");
    // should introduce particle as near as possible to the aggregate.
    double R = aggregateRadius + 2 * particleRadius; // close as possible without risking ;w
    double angle = rgen.nextDouble(0,2*Math.PI);
    // System.out.print("particle introduced");
    Node diffuser = new Node(R * Math.cos(angle), R * Math.sin(angle), particleRadius);
    if (animateOn) {
      thePanel.setDiffuser(diffuser);
    }
    return diffuser;
  }

  private Node diffuseUntilHit(Node diffuser) { 
    if (debugOn) System.out.print("diffuseUntilHit\n");
    // System.out.print("particle jumping");
    // reintroduce a particle if out of bounds.
    tuple <Double, Node> distanceNodePair = getDistanceNodePair(diffuser);
    double distance = distanceNodePair.first.doubleValue();
    while (true) { 
      makeJump(diffuser, distance);
      if (particleOutOfBounds(diffuser)) { 
        diffuser = introduceDiffuser();
      }
      distanceNodePair = getDistanceNodePair(diffuser);
      distance = distanceNodePair.first.doubleValue();
      if (hasParticleHit(distance)) break;
    }
    Node sticker = distanceNodePair.second;
    return sticker;
  }

  private void makeJump(Node diffuser, double radius) { 
    if (debugOn) System.out.print("makeJump\n");
    double angle = rgen.nextDouble(0,2*Math.PI);
    double oldX = diffuser.getX();
    double oldY = diffuser.getY();
    diffuser.move(radius * Math.cos(angle), radius * Math.sin(angle));
    thePanel.moveNode(oldX, oldY, diffuser.getX(), diffuser.getY());
      
  }

  private tuple<Double, Node> getDistanceNodePair(Node diffuser) {
    if (debugOn) System.out.print("getNextJumpRadius\n");
    Node closestNode = nodeList.get(0);
    double squareDistance = getSquareDistanceBetween(closestNode, diffuser);
    for (Node node : nodeList) { 
      if (getSquareDistanceBetween(node, diffuser) < squareDistance) {
        closestNode = node;
        squareDistance = getSquareDistanceBetween(node, diffuser);
      }
    } 
    double distance = Math.sqrt(squareDistance) - 
      diffuser.getRadius() - closestNode.getRadius();
    return new tuple <Double, Node> (distance, closestNode);
  }

  private void attach(Node diffuser, Node sticker) {
    if (debugOn) System.out.print("attach\n");
    System.out.print(diffuser.toString() + " attaching to " +  sticker.toString() + "\n");
    snapTo(diffuser, sticker);
    sticker.addNeighbor(diffuser);
    diffuser.addNeighbor(sticker);
    nodeList.add(diffuser);
    if (animateOn) {
      System.out.print("here\n");
      thePanel.updateNodeList(nodeList);
    }
    if (diffuser.getDistanceFromOrigin() > aggregateRadius) {
      aggregateRadius = diffuser.getDistanceFromOrigin();
    }
  }

  private double getSquareDistanceBetween(Node node1, Node node2) {
    return  Math.pow(node1.getX() - node2.getX(),2) + Math.pow(node1.getY() - node2.getY(),2);
  }

  private boolean particleOutOfBounds(Node diffuser) {
    if (debugOn) System.out.print("particleOutOfBounds \n");
    return (diffuser.getDistanceFromOrigin() > killRadius);
  }

  private boolean hasParticleHit(double radius) { 
    if (debugOn) System.out.print("hasParticleHit\n");
    return (radius < snapDistance);
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