import java.util.*;
import acm.util.*;

public class Node implements java.io.Serializable{

  private static final boolean debugOn = true;
  private static final long serialVersionUID = 1;

  public Node(double x,double y) {
    this.x = x;
    this.y = y;
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

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  public double getRadius() {
    return radius ;
  }

  public double distanceFromOrigin() {
    return Math.sqrt(Math.pow(x,2) + Math.pow(y,2));
  }

  public String toString() { 
    return "node located at (" + x + " , " + y + ")";
  } 

  public void addNeighbor(Node node) { 
    neighbors.add(node);
  }

  private double radius;
  private double x;
  private double y;
  private double vel;
  private double angle;
  private List<Node> neighbors = new ArrayList<Node>();
}
