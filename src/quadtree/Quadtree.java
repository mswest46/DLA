//TODO: add a print method like with kd tree. 
//TODO: exceptions.
//TODO: remove method. 
//TODO: comment better.
//TODO: comprehensive tests. 
package quadtree;

import java.util.*;
import basics.*;

public class Quadtree {
  private final int NODE_CAPACITY;
  private List<Point2D> points;
  private BoundingBox bb;
  private Quadtree NE, SE, SW, NW;
  private List<Quadtree> children = new ArrayList<Quadtree>();

  // for debugging/visualization. 
  public int level;
  public int nPointsStored; //includes points in subtrees
  
  public Quadtree (int NODE_CAPACITY, double x, double y, double width, double height) {
    // initial call creates root at level 0;
    this(NODE_CAPACITY, x, y, width, height, 0);
  }

  public Quadtree (int NODE_CAPACITY, double x, double y, double width, double height, int level) {
    this.NODE_CAPACITY = NODE_CAPACITY;
    this.points = new ArrayList<Point2D >();;
    this.bb = new BoundingBox(x, y, width, height);
    this.level = level;
    this.nPointsStored = 0;
  }

  /*
   * inserts p into quadtree. 
   */
  public boolean insert(Point2D p) {
    // point does not lie in bounding box. 
    if (!bb.containsPoint(p)) {
      return false;
    }

    // point lies in boudning box and there is space in points array for new point. 
    if (points.size() < NODE_CAPACITY) {
      points.add(p);
      nPointsStored++;
      return true;
    }
    
    // no space in points array, and the first insertion for which this occurs. subdivide. 
    if (NE==null) {
      subdivide();
    }

    // try to insert in each of the children. 
    for (Quadtree qt : children) {
      if (qt.insert(p)){
        nPointsStored++;
        return true;
      }
    }

    // if the point lies in this bounding box, it should lie in the bounding box of a child. 
    System.out.print("\n something is wrong \n");
    return false;
  }

  /*
   * creates children NE, NW, SE, SW, with bounding boxes the quadrants of this bounding box.
   */
  public void subdivide() {

    double x = bb.x;
    double y = bb.y;
    double width = bb.width;
    double height = bb.height;

    NE = new Quadtree(NODE_CAPACITY, x + width/2, y + height/2, width/2, height/2, level + 1);
    SE = new Quadtree(NODE_CAPACITY, x + width/2, y, width/2, height/2, level + 1);
    SW = new Quadtree(NODE_CAPACITY, x, y, width/2, height/2, level + 1);
    NW = new Quadtree(NODE_CAPACITY, x, y + height/2, width/2, height/2, level + 1);

    children.add(NE);
    children.add(SE);
    children.add(SW);
    children.add(NW);
  }

  public Point2D nearestNeighbor(Point2D p) { 
    Point2D bestP = points.get(0);
    return nearestNeighbor(p, bestP);
  }

  private Point2D nearestNeighbor(Point2D p, Point2D bestP) { 

    double bestD = p.distanceTo(bestP);
    
    if (!bb.intersectsCircle(p, bestD)) {
      return bestP;
    }

    // update min distance in this box. 
    for (Point2D point : points) {
      if (bestD > p.distanceTo(point)) {
        bestP = point;
        bestD = p.distanceTo(bestP);
      }
    }

    // update min distance from children. 
    if (!(NE==null)) {
      for (Quadtree qt : children) {
        bestP = qt.nearestNeighbor(p, bestP);
      }
    }

    return bestP;
  }

  /*
   * Returns all points in range given by bounding box range. 
   */
  public List<Point2D > queryRange(BoundingBox range) {
    List<Point2D > pointsInRange = new ArrayList<Point2D >();

    // if the range doesn't intersect this box, we done. 
    if (!bb.intersectsBox(range)) {
      return pointsInRange;
    }

    // if the range intersects this box, check each point in the box. 
    for (Point2D p : points) {
      if (range.containsPoint(p)) {
        pointsInRange.add(p);
      }
    }
    
    // if this node has children, check each of these. 
    if (!(NE==null)) {
      for (Quadtree qt : children) {
        pointsInRange.addAll(qt.queryRange(range));
      }
    }
    return pointsInRange;
  }

  public void remove(Point2D p) {
  }

  public String toString() {
    return "quadtree with node cap " + NODE_CAPACITY;
  }
}

