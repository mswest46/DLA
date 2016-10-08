package kdtree;

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import acm.util.RandomGenerator;
import basics.*;

public class testKDtree { 
  public static RandomGenerator rgen = new RandomGenerator();
  public static void main(String[] args) 
  {
    testKDtree tester = new testKDtree();
    tester.test1();
    //tester.test2();
    // tester.test3();
    // tester.test4();
    // tester.test5();
    // tester.test6();
  }

  private void test6() { 
      KDtree kd1 = new KDtree();
      List<Point> points = readCSV("./DLA/data/brokenKD.csv");
      Point target = readCSV("./DLA/data/brokenKDparticle.csv").get(0);
      System.out.println(points);
      for (Point point : points) { 
        kd1.insert(point);
      }

      Point[] pointArray = points.toArray(new Point[points.size()]);
      KDtree kd2 = new KDtree(pointArray);
      kd1.print();
      kd2.print();
      
      System.out.println("The target point is: " + target.toString());
      Point trueClosestPoint = naiveArraySearch(points, target);
      double trueDistance = target.distanceTo(trueClosestPoint);
      System.out.println("True closest point is: " + trueClosestPoint.toString() + ", distance:" + trueDistance);

      Point kd1ClosestPoint = kd1.nearestNeighbor(target);
      double kd1Distance = target.distanceTo(kd1ClosestPoint);
      System.out.println("The kd1 thinks: " + kd1ClosestPoint.toString() + ", distance:" + kd1Distance);

      Point kd2ClosestPoint = kd2.nearestNeighbor(target);
      double kd2Distance = target.distanceTo(kd2ClosestPoint);
      System.out.println("The kd2 thinks: " + kd2ClosestPoint.toString() + ", distance:" + kd2Distance);


      
  }

  private List<Point> readCSV(String filename) {
    try { 
      List<Point> points = new ArrayList<Point>();
      BufferedReader br = new BufferedReader(new FileReader(filename));
      String line = "";
      while ((line = br.readLine()) != null) {
        String[] coordStrings = line.split(",");
        points.add(new Point(Double.parseDouble(coordStrings[0]), Double.parseDouble(coordStrings[1])));
      }
      return points;
    } catch (Exception e) { 
      e.printStackTrace();
      throw new RuntimeException();
    }
  }
  private Point naiveArraySearch(List<Point> pointList, Point point) { 
    double bestD = point.distanceTo(pointList.get(0));
    Point bestP = pointList.get(0);
    for (Point stickee : pointList) { 
      if (point.distanceTo(stickee) < bestD) { 
        bestD = point.distanceTo(stickee);
        bestP = stickee;
      }
    }
    return bestP;
  }

  private void test5() { 
    KDtree kd = new KDtree();
    kd.insert(new Point(0,0));
    kd.print();
  }
  private void test4() { 
    KDtree kd = new KDtree(new Point[] {new Point(0,0)});
    System.out.println("height: " + kd.height);
  }

  private void test3() {
    int nPoints = 100;
    Point[] pointArray = new Point[nPoints];
    for (int i = 0; i < nPoints; i++) {
      pointArray[i] = new Point(rgen.nextDouble(), rgen.nextDouble());
    }
    KDtree kd = new KDtree(pointArray);
    int nInserts = 1000;
    for (int i = 0; i < nInserts; i++) {
      kd.insert(new Point(i,i));
      System.out.println("balance: " + kd.getBalance());
      System.out.println("height: " + kd.height + ", nPoints: " + kd.nPointsStored);
      // kd.print();
      // should be close to 0
    }

  }
  private void test2() { 
    int nPoints = 100;
    Point[] pointArray = new Point[nPoints];
    for (int i = 0; i < nPoints; i++) {
      pointArray[i] = new Point(rgen.nextDouble(), rgen.nextDouble());
    }
    KDtree kd = new KDtree(pointArray);

    System.out.println(kd.nPointsStored);
    System.out.println(kd.height);

    int nInserts = 10000;
    for (int i = 0; i < nInserts ; i++) {
      Point point = new Point(rgen.nextDouble(),rgen.nextDouble());
      kd.insert(point);
      System.out.println("balance: " + kd.getBalance());
    }
    int nSearches = 1000000;
    for (int i = 0; i < nSearches; i++) {
      Point point = new Point(rgen.nextDouble(),rgen.nextDouble());
      Point nn = kd.nearestNeighbor(point);
    }

  }
  private void test1() {
    List<Point> pointList = new ArrayList<Point>();
    pointList.add(new Point(0,0));
    pointList.add(new Point(1,-3));
    pointList.add(new Point(3,-5));
    pointList.add(new Point(4,-7));
    pointList.add(new Point(-6,-3));
    pointList.add(new Point(-7,1));
    pointList.add(new Point(-3,8));
    Point[] pointArray = pointList.toArray(new Point[pointList.size()]);
    KDtree kd = new KDtree(pointArray);

    System.out.println(kd.nPointsStored);
    System.out.println(kd.height);
    
    kd.print();
    System.out.println("inserting new point");
    kd.insert(new Point(5,5));
    double balance = kd.getBalance();
    System.out.println("number of points: " + kd.nPointsStored);
    System.out.println("height: " + kd.height);
    kd.print();

    Point nn = kd.nearestNeighbor(new Point(-1,-3));
    System.out.println(nn.toString());
    // kd.insert(new Point(-1,-3));
    // kd.print();
  }
}


