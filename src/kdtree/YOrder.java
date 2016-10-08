
package kdtree;

import java.util.Comparator;
import basics.*;

public class YOrder implements Comparator<Point2D> {
  public int compare(Point2D p1, Point2D p2) {
    if (p1.getY() < p2.getY()) return -1;
    if (p1.getY() > p2.getY()) return 1;
    return 0;
  }
}
