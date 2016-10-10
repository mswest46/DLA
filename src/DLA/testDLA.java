package DLA;

public class testDLA {
  public static void main(String[] args) {
    AnimationOptions animationOptions = new AnimationOptions();
    animationOptions.setType(AnimationType.ATTACH);
    animationOptions.setPause(100);
    AggregateOptions aggregateOptions = new AggregateOptions(10000, 1);
    StorageOptions storageOptions = new StorageOptions(StorageType.KDTREE);

    DLA aggregate = new DLA(aggregateOptions, animationOptions, storageOptions);
  }
}
