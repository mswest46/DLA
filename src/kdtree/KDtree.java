
// TODO: rename KDNode. is closer to the truth. also some other bad names.
// TODO: clean up the constructor mess. 
// TODO: make some real interactive tests. 

package kdtree;


import java.util.Collections;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.lang.Math;
import basics.*;

public class KDtree {
  private Point2D location;
  private KDtree leftChild;
  private KDtree rightChild;
  private KDtree parent;
  private int splitAxis; // for now is 0 or 1
  private int depth; // the level of this node
  public int height; // the deepest node depth minus this depth. 
  public int nPointsStored; // the number of points in this node's subtree. 
  private int pointDim;
  private boolean isLeft; //true means left node, false means right node
  private boolean isEmpty; //true means no children and no location. 

  ////////////////////////////////////////////////////////////////////////////////
  //Constructors. Can definitely be condensed somewhat.  

  // initial call with list of points. 
  public KDtree(Point2D[] pointList) {
    this(pointList, 2, 0, null, true);
  }

  // initial with no list of points. 
  public KDtree() {
    this(2, 0, null, true);
  }

  // call with no points. 
  public KDtree(int k, int depth, KDtree parent, boolean isLeft) {
    instantiateMyself(k, depth, parent, isLeft);
  }
  // call with points
  public KDtree(Point2D[] pointList, int k, int depth, KDtree parent, boolean isLeft) {
    instantiateMyself(k, depth, parent, isLeft);
    fillMyself(pointList);
  }

  // call with single point
  public KDtree(Point2D point, int k, int depth, KDtree parent, boolean isLeft) {
    this(new Point2D[] {point}, k, depth, parent, isLeft);
  }

  ////////////////////////////////////////////////////////////////////////////////

  //  makes node with all variables filled but location and children. 
  //  this is set with parent, BEFORE the parent claims this as child. 
  private void instantiateMyself(int k, int depth, KDtree parent, boolean isLeft) {
    this.pointDim = k;
    this.depth = depth;
    this.splitAxis = depth % k;
    this.isEmpty = true;
    this.height = 0;
    this.nPointsStored = 0;
    this.isLeft = isLeft;
    // establish relationships.
    this.parent = parent;
  }

  public void fillMyself(Point2D point) {
    fillMyself(new Point2D[]{point});
  }

  public void fillMyself(Point2D[] pointList) {
    assert (this.isEmpty == true);
    this.isEmpty = false;
    this.nPointsStored = pointList.length;
    sortList(pointList, splitAxis);
    Point2D[] leftList;
    Point2D[] rightList;
    if (pointList.length == 0) {
      throw new RuntimeException("you're filling a node without supplying any points. Don't do that!");
    }
    createEmptyChildren(); //first create empty children. This should extablish relationships between this node and these children, and update height along this node's parent chain.
    switch(pointList.length) {
      case 1: // don't fill any children.
        location = pointList[0];
        break;
      case 2: // fill left child only.
        location = pointList[1];
        leftList = Arrays.copyOfRange(pointList, 0,1);
        leftChild.fillMyself(leftList);
        break;
      default: // fill both children. 
        int medianIndex = pointList.length/2; 
        location = pointList[medianIndex];
        leftList = Arrays.copyOfRange(pointList, 0, medianIndex);
        rightList = Arrays.copyOfRange(pointList, medianIndex + 1, pointList.length);
        leftChild.fillMyself(leftList);
        rightChild.fillMyself(rightList);
    }
  }

  private void createEmptyChildren() { 
    leftChild = createEmptyChild(true); 
    rightChild = createEmptyChild(false);
    // update height along the parent chain. 
    increaseHeight();
  }
    
  private KDtree createEmptyChild(boolean isLeft){
    return new KDtree(pointDim, depth + 1, this, isLeft);
  }

  public void increaseHeight() {
    height = Math.max(leftChild.height, rightChild.height) + 1;
    if (parent != null) {
      parent.increaseHeight();
    }
  }

  public void sortList(Point2D[] pointList, int splitAxis) {
    Comparator<Point2D> comparator;
    switch (splitAxis) {
      case 0:
        comparator = new XOrder();
        break;
      case 1: 
        comparator = new YOrder();
        break;
      default: 
        throw new RuntimeException("splitAxis has to be 0 or 1 yo");
    }
    Arrays.sort(pointList, comparator);
  }

  public boolean insert(Point2D point) {
    if (isEmpty) { 
      fillMyself(new Point2D[]{point});
      return true;
    }
    nPointsStored++; // we are inserting a point somewhere in this node's subtree.
    if (goesInLeft(point)) { // i.e. along the splitAxis, this point falls to the left of location
      if (leftChild.isEmpty) { // we've reached a leaf. 
        leftChild.fillMyself(point);
        return true;
      } 
      return leftChild.insert(point);
    } else { // i.e. point goes in right
      if (rightChild.isEmpty) {
        rightChild.fillMyself(point);
        return true;
      }  
      return rightChild.insert(point);
    }
  }

  private boolean goesInLeft(Point2D point) {
    switch (splitAxis) {
      case 0:
        if (point.getX() < location.getX()) return true;
        break;
      case 1:
        if (point.getY() < location.getY()) return true;
        break;
    }
    return false;
  }

  public int getDepth() {
    return depth;
  }
  
  public Point2D getLocation() { 
    return location;
  }


  // returns the would-be insertion node of target, if we were to insert target into the tree. 
  public KDtree search(Point2D target) { 
    if (isEmpty) return this;
    if (goesInLeft(target)) { // i.e. along the splitAxis, this point falls to the left of location
      return leftChild.search(target);
    } else { // i.e. point goes in right
      return rightChild.search(target);
    }
  }

  public String toString() { 
    return "KD node with location: " + location.toString();
  }
  // caller is the node that is calling. We don't want to back up beyond caller. 
  //
  public Point2D nearestNeighbor(Point2D target) { 
    KDtree node = search(target);
    Point2D guess = node.parent.getLocation();
    double bestDistance = target.distanceTo(guess);

    while (!node.equals(this)){ 

      boolean cameFromLeft = node.isLeft; // records which child we have already searched
      node = node.parent; //backing up along the search. 

      // updates the guess.
      if (target.distanceTo(node.getLocation()) < bestDistance) { 
        guess = node.getLocation();
        bestDistance = target.distanceTo(guess);
      }

      double distanceToSplit;
      switch (node.splitAxis) { 
        case 0: 
          distanceToSplit = Math.abs(target.getX() - node.getLocation().getX());
          break;
        case 1: 
          distanceToSplit = Math.abs(target.getY() - node.getLocation().getY());
          break;
        default: 
          throw new RuntimeException();
      }



     KDtree otherChild; 
     otherChild = (cameFromLeft) ? node.rightChild : node.leftChild;

      if (bestDistance > distanceToSplit && !otherChild.isEmpty) { 
        Point2D potentialGuess = otherChild.nearestNeighbor(target);
        double potentialDistance = target.distanceTo(potentialGuess);
        if (potentialDistance < bestDistance) { 
          guess = potentialGuess;
          bestDistance = potentialDistance;
        }
      }

    }
    return guess;
  }

  // /* 
  //  * traverses the tree until finds a leaf, (i.e. and empty node. TODO: replace isEmpty wiht isLeaf?);
  //  */
  // public BestPointCurrentNodePair search (Point target, Point bestPoint) {

  //   // we reach leaf node. we return the best Point and the current Node. 
  //   if (this.isEmpty) {
  //     BestPointCurrentNodePair pointNode = new BestPointCurrentNodePair();
  //     pointNode.currentNode = this;
  //     pointNode.bestPoint = bestPoint;
  //     return pointNode;
  //   }

  //   // we are currently closer to target than best point. update best point. 
  //   if (target.distanceTo(this.location) < target.distanceTo(bestPoint)) {
  //     bestPoint = this.location;
  //   }
  //   // we keep traversing
  //   if (goesInLeft(target)) {
  //     return leftChild.search(target, bestPoint);
  //   } else {
  //     return rightChild.search(target, bestPoint);
  //   }
  // }

  public void print() {
    print("",true);
  }

  public void print(String prefix, boolean isTail) {
    String name;
    if (this.isEmpty) {
      name = "empty";
    } else {
      name = location.toString();
    }
    System.out.println(prefix + (isTail ? "└── " : "├── ") + name);
    if (!(leftChild==null)) leftChild.print(prefix + (isTail ? "    " : "│   "), false);  
    if (!(rightChild==null)) rightChild.print(prefix + (isTail ? "    " : "│   "), true);  
  }

  /*
   * returns a crude measure of the balance of the tree.
   */
  public double getBalance() {
    double balance = (nPointsStored - height) / (nPointsStored - Math.log(nPointsStored)/Math.log(2));
    double threshold = (nPointsStored - Math.pow(Math.log(nPointsStored)/Math.log(2), 2) ) / (nPointsStored - Math.log(nPointsStored)/Math.log(2));
    if (balance < threshold) { 
      System.out.println("unbalanced");
    }
    return balance;
  }
}
