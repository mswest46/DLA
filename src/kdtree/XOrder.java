package kdtree;
import java.util.Comparator;
import basics.*;

public class XOrder implements Comparator<Point2D> {
  public int compare(Point2D p1, Point2D p2) {
    if (p1.getX() < p2.getX()) return -1;
    if (p1.getX() > p2.getX()) return 1;
    return 0;
  }
}
