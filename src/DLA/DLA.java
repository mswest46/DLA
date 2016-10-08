// TODO: There is a bug if i change how oftwen we reset kd. find out why. 
//
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
import kdtree.*;

public class DLA { 
  // TODO: change all words node to particle. 

  //constants
  private static final double particleRadius = 5; // the radius of the particle. Could be variable at some point? Would make the quadtree difficult. 
  private static final double snapDistance = particleRadius / 1000; // When particles are snapDistance apart, we say they are attached. 
  private static final double killRadius = Integer.MAX_VALUE; // when particle floats killRadius apart, we replace it with a new particle. 
  private int[] jumpCalls; // number of calls to makeJump in each iteration
  private long[] diffuseTimes; // time spent diffusing in each iteration
  private long[] attachTimes; // time spent attaching in each iteration.

  private int nParticles; // how many particles in the simulation. 
  private double aggregateRadius; // the maximum distance of any aggregated particle from the origin. 
  private List<Particle> particleList = new ArrayList<Particle>(); // a list of the particles in the aggregate. 
  private static final RandomGenerator rgen = new RandomGenerator(); // makes the randoms. 
  private int particleNumber = 0; // counts how many particles have attached. 
  private Particle diffuser; // the diffusing particle

  // animation stuff. why should any of these be static?
  private AnimationOptions animationOptions; // animation switch 
  private static AnimationType animationType;
  private static double minDistance;
  private static MyPanel thePanel; // where we animate. 
  private static int PAUSE_TIME; // pause in ms between particle jumps. 
  
  // data tree stuff.
  private StoreType storeType;
  private DataStore data;

  // quadtree stuff.


  /*
   * Constructor. 
   * @param nParticles     number of particles in the simulation
   * @param animationOptions 
   */
  public DLA(int nParticles, AnimationOptions animationOptions, StoreType storeType) {
    this.nParticles = nParticles;
    this.animationOptions = animationOptions;
    this.storeType = storeType;
    DLA.animationType = animationOptions.type;
    DLA.minDistance = animationOptions.minDistance;
    DLA.PAUSE_TIME = animationOptions.pause;
    initialize();
    
    System.out.println("Creating DLA aggregate with " + nParticles + " particles.");
    simulate();
    outputTimings();
    outputAggregate();
  }

  public void initialize() {
    this.data = new DataStore(storeType, nParticles);
    // TODO: 5 particle capacity? what's best? 
    // qt = new Quadtree (50, X_MIN, Y_MIN, AGG_WIDTH, AGG_HEIGHT);

    // TODO: add seed input functionality. i.e. caller sets the seed. 
    Particle seed = new Particle(0,0,particleRadius);
    
    // TODO: add helper to do quadtree and particleList and aggregateRadius. 
    data.addParticle(seed);
    particleList.add(seed);
    aggregateRadius = 0;

    if (animationType != AnimationType.NONE) { 
      createAndShowGUI();
      thePanel.updateParticleList(particleList);
    }

    // TODO: just for debugs, get rid of eventually.
    rgen.setSeed(3);

    // timing stuff. 
    diffuseTimes = new long[nParticles];
    jumpCalls = new int [nParticles];
    attachTimes = new long[nParticles];

  }

  /*
   * sets up the Swing stuff for animations.
   */
  private static void createAndShowGUI(){
        JFrame f = new JFrame("DLA");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        thePanel = new MyPanel(animationType);
        f.add(thePanel);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
  }

  /*
   * werks. 
   */
  private void simulate() {
    long startTime = System.nanoTime();
    for (int i = 0; i < nParticles; i++) {
      introduceDiffuser();
      long diffuseStartTime = System.nanoTime();
      Particle sticker = diffuseUntilHit();
      diffuseTimes[particleNumber] = System.nanoTime() - diffuseStartTime;
      long attachStartTime = System.nanoTime();
      attach(diffuser, sticker);
      attachTimes[particleNumber] = System.nanoTime() - attachStartTime;
      System.out.print("Particle number " + particleNumber + " attached.\n");
      particleNumber++;
    }


    double simulationTime = (double) (System.nanoTime() - startTime) / (60 * Math.pow(10, 9));
    System.out.println("simulation lasted " + simulationTime + " minutes" );



    if (animationType != AnimationType.NONE) {
      try {
        Thread.sleep(PAUSE_TIME);
      } catch (Exception e) {
        System.out.print(e);
      }
      thePanel.updateParticleList(particleList);
      thePanel.finalPaint();
    }

  }
  

  /*
   * introduces a diffusing particle at a random angle at a distance as close as possible to the aggregate without risking appearing on it. 
   * @return  the diffusing particle.
   */
  private void introduceDiffuser() { 
    double R = aggregateRadius + 2 * particleRadius; // close as possible without risking overlap 
    double angle = rgen.nextDouble(0,2*Math.PI);
    diffuser = new Particle(R * Math.cos(angle), R * Math.sin(angle), particleRadius);
    boolean animateDiffuser = (animationType == AnimationType.DIFFUSE_FAST || animationType == AnimationType.DIFFUSE_SLOW);
    if (animateDiffuser) {
      thePanel.setDiffuser(diffuser);
    }
  }

  /*
   * moves the diffusing particle in a Brownian motion until it contacts the aggregate. 
   * @param   the diffusing particle. 
   * @return  the particle in the aggregate that the diffuse hits. 
   */
  private Particle diffuseUntilHit() { 
    
    // get the closest particle in aggregate. 
    Particle closestParticle = closestToDiffuser();
    boolean animateDiffuser = (animationType == AnimationType.DIFFUSE_FAST || animationType == AnimationType.DIFFUSE_SLOW);

    if (animateDiffuser) {
      thePanel.setNearestNode(closestParticle);
    }
    // shrink the distance, accoutngin for particle radii. 
    double distance = adjustDistance(diffuser.distanceTo(closestParticle));

    // jumping loop. 
    while (true) { 
      // make the diffuser jump the distance at a random angle. 
      jumpCalls[particleNumber]++;

      // for animation.
      double oldX = diffuser.getX();
      double oldY = diffuser.getY();


      makeJump(distance);

      // if the diffuser has strayed beyond the kill radius, replace it with a new particle. Resets
      // diffuser in animation as well.
      if (particleOutOfBounds(diffuser)) { 
        introduceDiffuser();
      }

      closestParticle = closestToDiffuser();
      if (animateDiffuser) {
        // diffuser is know already. 
        // set the nearest node. 
        thePanel.setNearestNode(closestParticle);
        // repaints
        thePanel.moveParticle(oldX,oldY,diffuser.getX(),diffuser.getY(),diffuser.getRadius());
        try {
          Thread.sleep(PAUSE_TIME);
        } catch (Exception e) {
          System.out.print(e);
        }
      }
      distance = adjustDistance(diffuser.distanceTo(closestParticle));

      // if we hit the aggregate, break
      if (hasParticleHit(distance)) break;
    }
    
    return closestParticle;
  }

  /*
   * takes in the distance between the centers of two nodes and returns the distance between the edges of two nodes. 
   * TODO: assuming for now that particles have the same radius. Not sure I always will. 
   */
  private double adjustDistance(double distance) {
    distance = distance - 2 * particleRadius;
    if (animationType == AnimationType.DIFFUSE_SLOW) {
      distance = Math.min(distance, minDistance);
    }
    return distance;
  }

  /*
   * jumps the diffuser in a random direction a distance of distance
   * @param diffuser   the particle that is diffusing 
   * @param distance   the distance that it should jump
   */
  private void makeJump(double distance) { 
    double angle = rgen.nextDouble(0,2*Math.PI);
    diffuser.move(distance * Math.cos(angle), distance * Math.sin(angle));
  }

  /* 
   * gets the closest particle in the aggregate and its distance to the diffuser. 
   * @ param diffuser   the diffusing particle
   * @returns           the closest particle and its distance in an object. 
   */
  private Particle closestToDiffuser() {
    return data.nearestNeighbor(diffuser);
    // PointDistancePair cpdp = qt.closestPointDistancePair(diffuser);

    // if (cpdp.getDistance() < 2 * particleRadius) {
    //   //TODO: why is this happening when I use small particle radius? 
    //   System.out.println("NOT A REAL EMPTYSTACK I JUST DONT KNOW HOW EXCEPTIONS WORK");
    //   throw new EmptyStackException();
    // }
  }


  /*
   * attach the diffuser to the sticker. TODO: enable the snapTo method for snappytoness
   * @param diffuser   diffusing particle. 
   * @param sticker    the particle it is sticking to. 
   */
  private void attach(Particle diffuser, Particle sticker) {
    //snapTo(diffuser, sticker);
    sticker.addNeighbor(diffuser);
    diffuser.addNeighbor(sticker);
    boolean animateAttacher = (animationType == AnimationType.DIFFUSE_SLOW 
        || animationType == AnimationType.DIFFUSE_FAST
        || animationType == AnimationType.ATTACH);
    // why? lets the diffusing animation finish? and then goes about attaching? 
    if (animateAttacher) {
      try {
        Thread.sleep(PAUSE_TIME);
      } catch (Exception e) {
        System.out.print(e);
      }
    }
    particleList.add(diffuser);
    // qt.insert(diffuser);
    data.addParticle(diffuser);
    if (animateAttacher) {
      thePanel.updateParticleList(particleList);
    }
    if (animationType == AnimationType.ATTACH) {
      thePanel.finalPaint();
    }

    // update aggregate radius if it has grown. 
    if (diffuser.distanceFromOrigin() > aggregateRadius) {
      aggregateRadius = diffuser.distanceFromOrigin();
    }
  }

  /*
   * @param diffuser    the diffusing particle. 
   * @returns           whether the particle is out of bounds or not. 
   */
  private boolean particleOutOfBounds(Particle diffuser) {
    // if (diffuser.distanceFromOrigin() > killRadius) {
    //   System.out.print("particle out of bounds \n");
    // }
    return (diffuser.distanceFromOrigin() > killRadius);
  }

  /*
   * determines whether or not we declare the particle to have hit the aggregate
   * @param distance    the distance from the particle to the aggregate. 
   * @returns           whether or not we say the particle has hit. 
   */
  private boolean hasParticleHit(double distance ) { 
    //  if (distance < snapDistance) {
    //    System.out.print("Particle has hit\n");
    //  }
    return (distance < snapDistance);
  }

  /*
   * TODO: make this work and use it. 
   */
  // private void snapTo(Particle diffuser, Particle sticker) {
  //   double difX = diffuser.getX() - sticker.getX();
  //   double difY = diffuser.getY() - sticker.getY();
  //   double distance = getSquareDistanceBetween(diffuser, sticker);
  //   double newDistance = diffuser.getRadius() + sticker.getRadius();
  //   double x = (1 - newDistance/distance) * difX; 
  //   double y = (1 - newDistance/distance) * difY; 
  //   diffuser.move(x,y);
  // }
  //

  private void outputAggregate() { 
    try {
    BufferedWriter br = new BufferedWriter(new FileWriter("DLA/data/aggregate.csv"));
    StringBuilder sb = new StringBuilder();
    for (Particle particle: particleList) {
     sb.append(particle.getX());
     sb.append(",");
     sb.append(particle.getY());
     sb.append("\n");
    }
    
    br.write(sb.toString());
    br.close(); 
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void outputTimings() {
    try {
      BufferedWriter br = new BufferedWriter(new FileWriter("DLA/data/timingData.csv"));
      StringBuilder sb = new StringBuilder();
      sb.append("particleNumber, diffuseTime(ns), jumpCalls, attachTime(ns)\n");
      for (int i = 0; i < nParticles; i++) {
        sb.append(i + "," + diffuseTimes[i] + "," + jumpCalls[i] + "," + attachTimes[i] + "\n");
      }
      br.write(sb.toString());
      br.close(); 
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
