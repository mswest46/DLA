package DLA;
import java.util.*;

public class ExampleDLA {
  public static final Scanner reader = new Scanner(System.in);
  public static void main(String[] args) {
    AggregateOptions aggregateOptions = inputAggregateOptions();
    AnimationOptions animationOptions = inputAnimationOptions();
    StorageOptions storageOptions = inputStorageOptions();
    DLA aggregate = new DLA(aggregateOptions, animationOptions, storageOptions);
  }
  public static AnimationOptions inputAnimationOptions(){
    AnimationOptions animationOptions = new AnimationOptions();
    System.out.println("Type \"diffuse\", \"attach\", or \"final\" to pick animation type.");
    String typeString = reader.next();
    AnimationType type;
    switch (typeString) { 
      case "diffuse": type = AnimationType.DIFFUSE_FAST;
                      break;
      case "attach": type = AnimationType.ATTACH;
                    break;
      case "final": type = AnimationType.FINAL;
                    break;
      default: 
                    throw new RuntimeException("enter one of the three types");
    }
    animationOptions.setType(type);

    System.out.println("Pause between animation steps in ms.");
    animationOptions.setPause(reader.nextInt());

    return animationOptions;
  }
  public static StorageOptions inputStorageOptions(){
    System.out.println("Type \"kd\", \"qt\", or \"linear\" to store data in a KDtree, Quadtree, or simple array.");
    String typeString = reader.next();
    StorageType type;
    switch (typeString) { 
      case "kd": type = StorageType.KDTREE;
        break;
      case "qt": type = StorageType.QUADTREE;
        break;
      case "linear": type = StorageType.LINEAR;
        break;
      default: 
        throw new RuntimeException("enter one of the three types");
    }
    return new StorageOptions(type);
  }
  public static AggregateOptions inputAggregateOptions(){
    System.out.println("Particle radius (5 is good): ");
    double rad = reader.nextDouble();

    System.out.println("radius: " + rad);
    System.out.println("number of particles in simulation (1000 is good): ");
    int num = reader.nextInt();
    System.out.println("number: " + num);

    return new AggregateOptions(num, rad);
  }
}
