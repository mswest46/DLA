package DLA;
import java.io.*;
import quadtree.*;
import kdtree.*;
import acm.util.*;
import java.util.*;


public class DataStore { 
  private StoreType type;

  private List<Particle> particleList; 
  private KDtree kd;
  private Quadtree qt;

  public DataStore(StoreType type, int nParticles) { 
    this.type = type;
    switch (type) { 
      case LINEAR:
        makeLinearStore();
        break;
      case QUADTREE:
        makeQuadtree();
        break;
      case KDTREE:
        makeKDTree();
        break;
      case DEBUG: 
        makeKDTree();
        makeQuadtree();
        makeLinearStore();
        break;
      default: 
        System.out.println("data store type doesn't fit");
    }
  }
  public void addParticle(Particle particle){
    switch (type) { 
      case LINEAR:
        particleList.add(particle);
        break;
      case QUADTREE:
        qt.insert(particle);
        break;
      case KDTREE:
        kd.insert(particle);
        break;
      case DEBUG:
        int j = kd.nPointsStored;
        while (j > 9 && j % 10 ==0) {
          j = j/10;
        }
        if (j==1) {
          kd = new KDtree(particleList.toArray(new Particle[particleList.size()]));
        }
        kd.insert(particle);
        qt.insert(particle);
        particleList.add(particle);
        break;
    }
  }
  public Particle nearestNeighbor(Particle particle) { 
    switch (type) { 
      case LINEAR:
        return naiveArraySearch(particle);
      case QUADTREE:
        return (Particle) qt.nearestNeighbor(particle);
      case KDTREE:
        return (Particle) kd.nearestNeighbor(particle);
      case DEBUG:

        Particle linear = naiveArraySearch(particle);
        if (!linear.equals(kd.nearestNeighbor(particle)))  {
          outputCSV(particle, "DLA/data/brokenKDparticle.csv");
          outputCSV(particleList, "DLA/data/brokenKD.csv");
        }
        assert(linear.equals(qt.nearestNeighbor(particle)));
        assert(linear.equals(kd.nearestNeighbor(particle)));
        return linear;
      default :
        throw new RuntimeException();
    }
  }
  private Particle naiveArraySearch(Particle particle) { 
    double bestD = particle.distanceTo(particleList.get(0));
    Particle bestP = particleList.get(0);
    for (Particle stickee : particleList) { 
      if (particle.distanceTo(stickee) < bestD) { 
        bestD = particle.distanceTo(stickee);
        bestP = stickee;
      }
    }
    return bestP;
  }
  private void makeLinearStore() { 
    particleList = new ArrayList<Particle>();
  }
  private void makeQuadtree() {
  // boundary box for aggregate. TODO: make this automatic, depending on particle radius and nParticles;
   double X_MIN = -100000;  
   double Y_MIN = -100000;
   double AGG_WIDTH = 200000;
   double AGG_HEIGHT = 200000;
    qt = new Quadtree(5, X_MIN, Y_MIN, AGG_WIDTH, AGG_HEIGHT);
  }
  private void makeKDTree() { 
    kd = new KDtree();
  }

  private void outputCSV(Particle particle, String filename) { 
    List<Particle> list = new ArrayList<Particle>();
    list.add(particle);
    outputCSV(list, filename);
  }

  private void outputCSV(List<Particle> particleList, String filename) { 
    try {
    BufferedWriter br = new BufferedWriter(new FileWriter(filename));
    StringBuilder sb = new StringBuilder();
    for (Particle particle : particleList) {
     sb.append(particle.getX());
     sb.append(",");
     sb.append(particle.getY());
     sb.append("\n");
    }
    
    br.write(sb.toString());
    br.close(); 
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
