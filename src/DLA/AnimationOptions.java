package DLA;

public class AnimationOptions {
  AnimationType type;
  public int pause;
  public double minDistance;
  public void setType(AnimationType type) {
    this.type= type;
  }
  public void setPause(int pause) {
    this.pause = pause;
  }
  public void setMinDistance(double minDistance) {
    this.minDistance = minDistance;
  }
}

