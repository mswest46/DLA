package DLA;

import java.lang.Math.*;
import acm.util.*;
import java.util.*;
import java.io.*;
import javax.swing.SwingUtilities;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics; 
import java.lang.Thread;
import quadtree.*;

public class DLA { 
  // TODO: change all words node to particle. 

  //constants
  private static final double particleRadius = 5; // the radius of the particle. Could be variable at some point? Would make the quadtree difficult. 
  private static final double snapDistance = .01; // When particles are snapDistance apart, we say they are attached. 
  private static final double killRadius = Integer.MAX_VALUE; // when particle floats killRadius apart, we replace it with a new particle. 
  private static final boolean debugOn = false; // debug switch

  //instance vars. TODO: diffuser
  private int nNodes; // how many nodes in the simulation. 
  private double aggregateRadius; // the maximum distance of any aggregated particle from the origin. 
  private List<Node> nodeList = new ArrayList<Node>(); // a list of the particles in the aggregate. 
  private Quadtree<Node> qt; // quadtree for searching for nearest aggregate particle.
  private static final RandomGenerator rgen = new RandomGenerator(); // makes the randoms. 
  private int nodeNumber = 0; // counts how many nodes have attached. 

  // animation stuff. 
  private boolean animateOn; // animation switch 
  private static MyPanel thePanel; // where we animate. 
  private static final int PAUSE_TIME = 1; // pause in ms between particle jumps. 
  // boundary box for aggregate. TODO: make this automatic, depending on particle radius and nNodes;
  private double X_MIN = -100000;  
  private double Y_MIN = -100000;
  private double AGG_WIDTH = 200000;
  private double AGG_HEIGHT = 200000;


  /*
   * Constructor. 
   * @param nNodes     number of particles in the simulation
   * @param animateOn  animation switch
   */
  public DLA(int nNodes, boolean animateOn) {
    this.nNodes = nNodes;
    this.animateOn = animateOn;
    initialize();
    simulate();
  }

  public void initialize() {
    // TODO: 5 node capacity? what's best? 
    qt = new Quadtree<Node> (5, X_MIN, Y_MIN, AGG_WIDTH, AGG_HEIGHT);

    // TODO: add seed input functionality. i.e. caller sets the seed. 
    Node seed = new Node(0,0,particleRadius);
    
    // TODO: add helper to do quadtree and nodeList and aggregateRadius. 
    nodeList.add(seed);
    qt.insert(seed.toPoint());
    aggregateRadius = 0;

    if (animateOn) { 
      createAndShowGUI();
      thePanel.updateNodeList(nodeList);
    }

    // TODO: just for debugs, get rid of eventually.
    rgen.setSeed(1);
  }

  /*
   * sets up the Swing stuff for animations.
   */
  private static void createAndShowGUI(){
        JFrame f = new JFrame("DLA");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        thePanel = new MyPanel();
        f.add(thePanel);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
  }

  /*
   * werks. 
   */
  private void simulate() {
    for (int i = 0; i < nNodes; i++) {
      nodeNumber++;
      Node diffuser = introduceDiffuser();
      Node sticker = diffuseUntilHit(diffuser);
      attach(diffuser, sticker);
      System.out.print("Node number " + nodeNumber + " attached.\n");
    }
    System.out.print("\nDUNZO");
  }
  

  /*
   * introduces a diffusing particle at a random angle at a distance as close as possible to the aggregate without risking appearing on it. 
   * @return  the diffusing particle.
   */
  private Node introduceDiffuser() { 
    double R = aggregateRadius + 2 * particleRadius; // close as possible without risking overlap 
    double angle = rgen.nextDouble(0,2*Math.PI);
    Node diffuser = new Node(R * Math.cos(angle), R * Math.sin(angle), particleRadius);
    if (animateOn) {
      thePanel.setDiffuser(diffuser);
    }
    return diffuser;
  }

  /*
   * takes the existing diffuser and moves it to a random angle at a distance as close as possible to the aggreaget without risking appearing on it. 
   * TODO: there should be a diffusing particle as an instance variable. it is part of the aggreagation simulation. 
   */
  private void replaceDiffuser(Node diffuser) {
    double R = aggregateRadius + 2 * particleRadius; // close as possible without risking ;w
    double angle = rgen.nextDouble(0,2*Math.PI);
    diffuser.setLocation(R * Math.cos(angle), R * Math.sin(angle));
  }

  /*
   * moves the diffusing particle in a Brownian motion until it contacts the aggregate. 
   * @param   the diffusing particle. 
   * @return  the particle in the aggregate that the diffuse hits. 
   */
  private Node diffuseUntilHit(Node diffuser) { 
    
    // get the closest particle in aggregate. TODO: rename
    PointDistancePair<Node> distanceNodePair = getDistanceNodePair(diffuser);
    if (animateOn) {
      thePanel.setNearestNode(distanceNodePair.getPoint().data);
    }
    // shrink the distance, accoutngin for particle radii. 
    double distance = takeParticleSizeIntoAccount(distanceNodePair.getDistance());

    // jumping loop. 
    while (true) { 
      // make the diffuser jump the distance at a random angle. 
      makeJump(diffuser, distance);

      // if the diffuser has strayed beyond the kill radius, replace it with a new particle. 
      // TODO: see if this can just use the distance we already extracted with quadtree, and make the diffuser an instance var. 
      if (particleOutOfBounds(diffuser)) { 
        // really shitty. change up. just seeing if it works. 
        replaceDiffuser(diffuser);
      }

      distanceNodePair = getDistanceNodePair(diffuser);
      // colors the nearest node blue. 
      if (animateOn) {
        thePanel.setNearestNode(distanceNodePair.getPoint().data);
      }
      distance = takeParticleSizeIntoAccount(distanceNodePair.getDistance());

      // if we hit the aggregate, break
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
   * @param diffuser   the particle that is diffusing 
   * @param distance   the distance that it should jump
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
      // repaints here. 
      thePanel.moveNode(oldX, oldY, diffuser.getX(), diffuser.getY());
    }
      
  }

  /* 
   * gets the closest particle in the aggregate and its distance to the diffuser. 
   * @ param diffuser   the diffusing particle
   * @returns           the closest particle and its distance in an object. TODO: make position an interface that quadtree handles, and make Node class implement it. Avoid a lot of this container bullshit. 
   */
  private PointDistancePair<Node> getDistanceNodePair(Node diffuser) {
    if (debugOn) System.out.print("getNextJumpRadius\n");
    PointDistancePair<Node> cpdp = qt.closestPointDistancePair(diffuser.toPoint());

    if (cpdp.getDistance() < 2 * particleRadius) {
      System.out.println("NOT A REAL EMPTYSTACK I JUST DONT KNOW HOW EXCEPTIONS WORK");
      throw new EmptyStackException();
    }
    return cpdp;
  }


  /*
   * attach the diffuser to the sticker. TODO: enable the snapTo method for snappytoness
   * @param diffuser   diffusing particle. 
   * @param sticker    the particle it is sticking to. 
   */
  private void attach(Node diffuser, Node sticker) {
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

  /*
   * TODO: this method should really belong to Node class. 
   */
  private double getSquareDistanceBetween(Node node1, Node node2) {
    return  Math.pow(node1.getX() - node2.getX(),2) + Math.pow(node1.getY() - node2.getY(),2);
  }

  /*
   * @param diffuser    the diffusing particle. 
   * @returns           whether the particle is out of bounds or not. 
   */
  private boolean particleOutOfBounds(Node diffuser) {
    if (debugOn) {
      if (diffuser.getDistanceFromOrigin() > killRadius) {
        System.out.print("particle out of bounds \n");
      }
    }
    return (diffuser.getDistanceFromOrigin() > killRadius);
  }

  /*
   * determines whether or not we declare the particle to have hit the aggregate
   * @param distance    the distance from the particle to the aggregate. 
   * @returns           whether or not we say the particle has hit. 
   */
  private boolean hasParticleHit(double distance ) { 
    if (debugOn) 
      if (distance < snapDistance) {
        System.out.print("Particle has hit\n");
      }
    return (distance < snapDistance);
  }

  /*
   * TODO: make this work and use it. 
   */
  private void snapTo(Node diffuser, Node sticker) {
    double difX = diffuser.getX() - sticker.getX();
    double difY = diffuser.getY() - sticker.getY();
    double distance = getSquareDistanceBetween(diffuser, sticker);
    double newDistance = diffuser.getRadius() + sticker.getRadius();
    double x = (1 - newDistance/distance) * difX; 
    double y = (1 - newDistance/distance) * difY; 
    diffuser.move(x,y);
  }

}
