package basics;

import java.lang.Math;

public class Point2D {
  protected double x,y;
  public Point2D(double x, double y) {
    this.x = x;
    this.y = y;
  }
  public double getX() {
    return x;
  }
  public double getY() {
    return y;
  }
  public void setX(double x) {
    this.x = x;
  }
  public void setY(double y) {
    this.y = y;
  }
  public double distanceTo(Point2D point) {
    return Math.sqrt((Math.pow(this.x - point.x,2) + Math.pow(this.y - point.y,2)));
  }
  public double squaredDistanceTo(Point2D point) {
    return (Math.pow(this.x - point.x,2) + Math.pow(this.y - point.y,2));
  }
  public String toString() {
    return "(" + x + ", " + y + ")";
  }
}
