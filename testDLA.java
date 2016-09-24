package DLA;

public class testDLA {
  private static final int nNodes = 100000;
  public static void main(String[] args) {
    AnimationOptions animationOptions = new AnimationOptions();
    animationOptions.setType("final");
    DLA aggregate = new DLA(nNodes, animationOptions);
  }
}
