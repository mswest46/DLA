package quadtree;
import java.util.*;
import basics.*;

class PointDistancePair {
  private Point2D point;
  private double distance;

  public PointDistancePair(Point2D point, double distance) {
    this.point = point;
    this.distance = distance;
  }

  public void set(Point2D point, double distance) {
    this.point = point;
    this.distance = distance;
  }

  public Point2D getPoint() { 
    return this.point;
  }

  public double getDistance() { 
    return this.distance;
  }

  public String toString() {
    return "point: " + point.toString() + ", distance: " + distance;
  }

}
