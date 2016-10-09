package DLA;

public class AnimationOptions {
  AnimationType type;
  public int pause;
  public double maxDistance = Integer.MAX_VALUE;

  public AnimationOptions() { 
  }
    
  public AnimationOptions(AnimationType type, int pause) { 
    this(type, pause, Integer.MAX_VALUE);

  }
  public AnimationOptions(AnimationType type, int pause, double minDistance) { 
    this.pause = pause;
    this.type = type;
    this.maxDistance = maxDistance;

  }
  public void setType(AnimationType type) {
    this.type= type;
  }
  public void setPause(int pause) {
    this.pause = pause;
  }
  public void setMinDistance(double minDistance) {
    this.maxDistance = maxDistance;
  }
}

