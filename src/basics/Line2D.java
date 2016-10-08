package basics;
import java.util.*;

public class Line2D{
  public Point2D start, end;

  public Line2D (Point2D start, Point2D end) {
    this.start = start;
    this.end = end;
  }

  /*
   * returns the vector that runs from the start point to the end point. 
   */ 
  public Vector2D toVector() {
    return new Vector2D(end.getX() - start.getX(), end.getY() - start.getY());
  }

  public String toString() {
    return "line from " + start.toString() + " to " + end.toString();
  }

}
