package DLA;

import java.util.*;
import acm.util.*;
import java.awt.Color;
import java.awt.Graphics;
import quadtree.*;

public class Particle extends Point {

  private double radius;
  private double vel;
  private double angle;
  private List<Particle> neighbors = new ArrayList<Particle>();

  /*
   * initializes node with position and radius. 
   */
  public Particle (double x, double y, double radius) {
    super(x,y);
    this.radius = radius;
  }

  public double getRadius() {
    return radius;
  }

  /*
   * moves this particle addX and addY in x and y directions respectively. 
   * @param addX    moves this point addX in x direction. 
   * @param addY    moves this point addY in y direction. 
   */
  public void move(double addX, double addY) { 
    x += addX;
    y += addY;
  }
  
  /*
   * set the position of the particle to (x,y)
   */
  public void setPosition(double x,double y) {
    this.x = x;
    this.y = y;
  }

  /*
   * @override
   */
  public String toString() { 
    return "particle located at " + super.toString();
  } 

  /*
   * inserts new neighbor in neighbors list. 
   */
  public void addNeighbor(Particle particle) { 
    neighbors.add(particle);
  }

  public double distanceFromOrigin() {
    return Math.sqrt(Math.pow(x,2) + Math.pow(y,2));
  }

  /*
   * for painting in animation. 
   */
  public void paintNode(Graphics g, Color color, int offsetWide, int offsetTall) {
    g.setColor(color);
    g.fillOval((int) x + offsetWide, (int) y + offsetTall, 2* (int) radius, 2* (int) radius);
    g.drawOval((int) x + offsetWide, (int) y + offsetTall, 2* (int) radius, 2* (int) radius);
  }
}
