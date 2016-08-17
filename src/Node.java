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

  /*
   * moves node 
   */
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


  /*
   * returns the closest node in an aggregate. 
   */
  public Node getClosestNodeInAggregate(ArrayList<Node> aggregate) { 
    Node minNode = aggregate.get(0);
    double squareDistance = getSquareDistanceTo(minNode);
    for (Node node : aggregate) { 
      if (getSquareDistanceTo(node) < squareDistance) {
        minNode = node;
        squareDistance = getSquareDistanceTo(node);
      }
    } 
    return minNode;
  }

  public double getSquareDistanceTo(Node node) {
    double squareDistance = Math.pow((x-node.getX()),2) + Math.pow((y-node.getY()),2);
    return squareDistance;
  }
  

  public void snapTo(Node sticker) {
    double tempX = x - sticker.getX();
    double tempY = y - sticker.getY();
    double distance = Math.sqrt(getSquareDistanceTo(sticker));
    double newDistance = radius + sticker.getRadius();
    
    tempY *= newDistance / distance;
    tempX *= newDistance / distance;

    x = sticker.getX() + tempX;
    y = sticker.getY() + tempY;
  }

  public void addNeighbor(Node node) { 
    neighbors.add(node);
  }

  private double radius;
  private double x;
  private double y;
  private double vel;
  private double angle;
  private ArrayList<Node> neighbors = new ArrayList<Node>();
}
