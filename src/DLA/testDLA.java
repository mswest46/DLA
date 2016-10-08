package DLA;

public class testDLA {
  private static final int nNodes = 1000;
  public static void main(String[] args) {
    AnimationOptions animationOptions = new AnimationOptions();
    animationOptions.setType(AnimationType.ATTACH);
    animationOptions.setPause(50);
    animationOptions.setMinDistance(5);
    DLA aggregate = new DLA(nNodes, animationOptions, StoreType.DEBUG);
  }
}
