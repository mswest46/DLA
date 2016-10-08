package quadtree;

import java.util.*;
import basics.*;

class BoundingBox  {

  public double x,y,width,height;
  public ArrayList<Line2D> edges = new ArrayList<Line2D>();

  public BoundingBox(double x, double y, double width, double height) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    makeEdges();
  }

  public String toString() {
    return "Bounding Box with left bottom corner: (" + x + "," + y + "), width " + width + ", height " + height;
  }

  public boolean containsPoint(Point2D p) {
    return (p.getX() > x && p.getX() < x + width && p.getY() > y && p.getY() < y + height);
  }

  public boolean intersectsBox(BoundingBox box) {
    boolean xProjIntersects = (box.x > x && box.x < x + width) 
      || (x > box.x && x < box.x + box.width);
    boolean yProjIntersects = (box.y > y && box.y < y + height) 
      || (y > box.y && y < box.y + box.height);
    return yProjIntersects && xProjIntersects;
  }

  /*
   * make the bounding line segments, for use by intersectsCircleMethod.
   */
  private void makeEdges() {
    edges.add(new Line2D(new Point2D(x,y), new Point2D(x + width, y)));
    edges.add(new Line2D(new Point2D(x,y), new Point2D(x, y + height)));
    edges.add(new Line2D(new Point2D(x + width, y), new Point2D(x + width, y + height)));
    edges.add(new Line2D(new Point2D(x,y + height), new Point2D(x + width, y + height)));
  }

  /*
   * Returns true if the circle specified by the center and radius intersects the box. 
   */
  public boolean intersectsCircle(Point2D center, double radius) {
    // center inside box. 
    if (this.containsPoint(center)) {
      return true;
    }

    // project the startToCenter vector onto each of the line vectors, and the radius and the magnitude of the rejection (startToCenter vector minus this projection). 
    for (Line2D edge : edges) { 
      Line2D startToCenter = new Line2D(edge.start, center);
      Vector2D centerVec = startToCenter.toVector();
      Vector2D lineVec = edge.toVector();
      if (centerVec.rejectOnto(lineVec).magnitude() < radius) {
        return true;
      }
    }
    return false;
  }
}
