package DLA;

public class testDLA {
  private static final int nNodes = 10000;
  public static void main(String[] args) {
    AnimationOptions animationOptions = new AnimationOptions();
    animationOptions.setType(AnimationType.ATTACH);
    animationOptions.setPause(1);
    AggregateOptions aggregateOptions = new AggregateOptions(1000,5);
    StorageOptions storageOptions = new StorageOptions(StorageType.KDTREE);

    DLA aggregate = new DLA(aggregateOptions, animationOptions, storageOptions);
  }
}
