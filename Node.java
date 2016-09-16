package DLA;

import java.util.*;
import acm.util.*;
import java.awt.Color;
import java.awt.Graphics;
import quadtree.*;

public class Node{

  private static final boolean debugOn = true;

  private double radius;
  private double x;
  private double y;
  private double vel;
  private double angle;
  private List<Node> neighbors = new ArrayList<Node>();


  public Node(double x,double y) {
    this.x = x;
    this.y = y;
  }

  public Point<Node> toPoint() { 
    Point<Node> point = new Point<Node>(x,y);
    point.setData(this);
    return point;
  }
  /*
   * initializes node with position and radius. 
   */
  public Node(double x, double y, double radius) {
    this.x = x;
    this.y = y;
    this.radius = radius;
  }

  public void move(double addX, double addY) { 
    x += addX;
    y += addY;
  }

  public void setLocation(double x, double y) {
    this.x = x;
    this.y = y;
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  public double getRadius() {
    return radius ;
  }

  public double getDistanceFromOrigin() {
    return Math.sqrt(Math.pow(x,2) + Math.pow(y,2));
  }

  public String toString() { 
    return "node located at (" + x + " , " + y + ")";
  } 

  public void addNeighbor(Node node) { 
    neighbors.add(node);
  }

  public void paintNode(Graphics g, Color color, int offsetWide, int offsetTall) {
    g.setColor(color);
    g.fillOval((int) x + offsetWide, (int) y + offsetTall, 2* (int) radius, 2* (int) radius);
    g.drawOval((int) x + offsetWide, (int) y + offsetTall, 2* (int) radius, 2* (int) radius);
  }

}
