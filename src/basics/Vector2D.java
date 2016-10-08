package basics;

public class Vector2D {
  public double x, y;
  public Vector2D(double x, double y) {
    this.x = x;
    this.y = y;
  }
  public double dotProduct(Vector2D vec) {
    return this.x * vec.x + this.y * vec.y;
  }
  public Vector2D scale(double scalar) {
    return new Vector2D(x * scalar, y * scalar);
  }
  /*
   * makes into unit vector. 
   */
  public Vector2D unitize() {
    return scale(1/this.magnitude());
  }
  /*
   * projects this vector onto vec. 
   */
  public Vector2D projectOnto(Vector2D vec) {
    double scalar = this.dotProduct(vec.unitize());
    return vec.unitize().scale(scalar);
  }

  /*
   * "rejects" returns the component of this vector that is perpendicular to vec. 
   */
  public Vector2D rejectOnto(Vector2D vec) {
    return this.subtract(projectOnto(vec));
  }
  public Vector2D add (Vector2D vec) {
    return new Vector2D(this.x + vec.x, this.y + vec.y);
  }
  public Vector2D subtract(Vector2D vec) {
    return new Vector2D(this.x - vec.x, this.y - vec.y);
  }
  public double magnitude() {
    return Math.sqrt(this.dotProduct(this));
  }
  public String toString() {
    return "vector (" + x + ", " + y + ")";
  }

}
