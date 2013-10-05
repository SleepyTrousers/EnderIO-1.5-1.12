package crazypants.vecmath;

public class Vector2f {

  public float x;
  public float y;

  public Vector2f() {
    x = 0;
    y = 0;
  }

  public Vector2f(double x, double y) {
    this.x = (float) x;
    this.y = (float) y;
  }

  public Vector2f(float x, float y) {
    this.x = x;
    this.y = y;
  }

  public Vector2f(Vector2d other) {
    this(other.x, other.y);
  }

  public Vector2f(Vector2f other) {
    this(other.x, other.y);
  }

  public void set(double x, double y) {
    this.x = (float) x;
    this.y = (float) y;
  }

  public void set(float x, float y) {
    this.x = x;
    this.y = y;
  }

  public void set(Vector2f vec) {
    x = vec.x;
    y = vec.y;
  }

  public void add(Vector2f vec) {
    x += vec.x;
    y += vec.y;
  }

  public void sub(Vector2f vec) {
    x -= vec.x;
    y -= vec.y;
  }

  public void negate() {
    x = -x;
    y = -y;
  }

  public void scale(double s) {
    x *= s;
    y *= s;
  }

  public void normalize() {
    double scale = 1.0 / Math.sqrt(x * x + y * y);
    scale(scale);
  }

  public double lengthSquared() {
    return x * x + y * y;
  }

  public double length() {
    return Math.sqrt(lengthSquared());
  }

  public double distanceSquared(Vector2f v) {
    double dx, dy, dz;
    dx = x - v.x;
    dy = y - v.y;
    return (dx * dx + dy * dy);
  }

  public double distance(Vector2f v) {
    return Math.sqrt(distanceSquared(v));
  }

  @Override
  public String toString() {
    return "Vector2f(" + x + ", " + y + ")";
  }

}
