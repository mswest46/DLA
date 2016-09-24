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
  private static final double particleRadius = 1; // the radius of the particle. Could be variable at some point? Would make the quadtree difficult. 
  private static final double snapDistance = .01; // When particles are snapDistance apart, we say they are attached. 
  private static final double killRadius = Integer.MAX_VALUE; // when particle floats killRadius apart, we replace it with a new particle. 
  private static final boolean debugOn = true; // debug switch
  private int[] jumpCalls; // number of calls to makeJump in each iteration
  private long[] diffuseTimes; // time spent diffusing in each iteration
  private long[] attachTimes; // time spent attaching in each iteration.

  private int nParticles; // how many particles in the simulation. 
  private double aggregateRadius; // the maximum distance of any aggregated particle from the origin. 
  private List<Particle> particleList = new ArrayList<Particle>(); // a list of the particles in the aggregate. 
  private Quadtree qt; // quadtree for searching for nearest aggregate particle.
  private static final RandomGenerator rgen = new RandomGenerator(); // makes the randoms. 
  private int particleNumber = 0; // counts how many particles have attached. 
  private Particle diffuser; // the diffusing particle

  // animation stuff. 
  private AnimationOptions animationOptions; // animation switch 
  private static MyPanel thePanel; // where we animate. 
  private static final int PAUSE_TIME = 100; // pause in ms between particle jumps. 
  // boundary box for aggregate. TODO: make this automatic, depending on particle radius and nParticles;
  private double X_MIN = -100000;  
  private double Y_MIN = -100000;
  private double AGG_WIDTH = 200000;
  private double AGG_HEIGHT = 200000;


  /*
   * Constructor. 
   * @param nParticles     number of particles in the simulation
   * @param animationOptions 
   */
  public DLA(int nParticles, AnimationOptions animationOptions) {
    this.nParticles = nParticles;
    this.animationOptions = animationOptions;
    initialize();
    
    System.out.println("Creating DLA aggregate with " + nParticles + " particles.");
    simulate();
    outputTimings();
    outputAggregate();
  }

  public void initialize() {
    // TODO: 5 particle capacity? what's best? 
    qt = new Quadtree (50, X_MIN, Y_MIN, AGG_WIDTH, AGG_HEIGHT);

    // TODO: add seed input functionality. i.e. caller sets the seed. 
    Particle seed = new Particle(0,0,particleRadius);
    
    // TODO: add helper to do quadtree and particleList and aggregateRadius. 
    particleList.add(seed);
    qt.insert(seed);
    aggregateRadius = 0;


    System.out.println(animationOptions.type);
    if (animationOptions.type.equals("continous")) { 
      createAndShowGUI();
      thePanel.updateParticleList(particleList);
    }

    // TODO: just for debugs, get rid of eventually.
    rgen.setSeed(1);

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
    if (animationOptions.type.equals("final")) {
      createAndShowGUI();
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
    if (animationOptions.type.equals("continous")) {
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
    PointDistancePair closestParticleAndDistance = closestToDiffuser();
    if (animationOptions.type.equals("continous")) {
      thePanel.setNearestNode((Particle)closestParticleAndDistance.getPoint());
    }
    // shrink the distance, accoutngin for particle radii. 
    double distance = takeParticleSizeIntoAccount(closestParticleAndDistance.getDistance());

    // jumping loop. 
    while (true) { 
      // make the diffuser jump the distance at a random angle. 
      jumpCalls[particleNumber]++;
      makeJump(distance);

      // if the diffuser has strayed beyond the kill radius, replace it with a new particle. 
      // TODO: see if this can just use the distance we already extracted with quadtree, and make the diffuser an instance var. 
      if (particleOutOfBounds(diffuser)) { 
        introduceDiffuser();
      }

      closestParticleAndDistance = closestToDiffuser();
      // colors the nearest node blue. 
      if (animationOptions.type.equals("continous")) {
        thePanel.setNearestNode((Particle)closestParticleAndDistance.getPoint());
      }
      distance = takeParticleSizeIntoAccount(closestParticleAndDistance.getDistance());

      // if we hit the aggregate, break
      if (hasParticleHit(distance)) break;
    }
    
    Particle sticker = (Particle)closestParticleAndDistance.getPoint();
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
  private void makeJump(double distance) { 
    double angle = rgen.nextDouble(0,2*Math.PI);
    double oldX = diffuser.getX();
    double oldY = diffuser.getY();
    diffuser.move(distance * Math.cos(angle), distance * Math.sin(angle));
    if  (animationOptions.type.equals("continous")) {
      try {
        Thread.sleep(PAUSE_TIME);
      } catch (Exception e) {
        System.out.print(e);
      }
      // repaints here. 
      thePanel.moveParticle(oldX,oldY,diffuser.getX(),diffuser.getY(),diffuser.getRadius());
    }
      
  }

  /* 
   * gets the closest particle in the aggregate and its distance to the diffuser. 
   * @ param diffuser   the diffusing particle
   * @returns           the closest particle and its distance in an object. 
   */
  private PointDistancePair closestToDiffuser() {
    PointDistancePair cpdp = qt.closestPointDistancePair(diffuser);

    // if (cpdp.getDistance() < 2 * particleRadius) {
    //   //TODO: why is this happening when I use small particle radius? 
    //   System.out.println("NOT A REAL EMPTYSTACK I JUST DONT KNOW HOW EXCEPTIONS WORK");
    //   throw new EmptyStackException();
    // }
    return cpdp;
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
    if (animationOptions.type.equals("continous")) {
      try {
        Thread.sleep(PAUSE_TIME);
      } catch (Exception e) {
        System.out.print(e);
      }
    }
    particleList.add(diffuser);
    qt.insert(diffuser);
    if (animationOptions.type.equals("continous")) {
      thePanel.updateParticleList(particleList);
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
