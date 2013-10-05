package crazypants.vecmath;

public class Vector2d {

  public double x;
  public double y;

  public Vector2d() {
    x = 0;
    y = 0;
  }

  public Vector2d(double x, double y) {
    this.x = x;
    this.y = y;
  }

  public Vector2d(Vector2d other) {
    this(other.x, other.y);
  }

  public void set(double x, double y) {
    this.x = x;
    this.y = y;
  }

  public void set(Vector2f vec) {
    x = vec.x;
    y = vec.y;
  }

  public void set(Vector2d vec) {
    x = vec.x;
    y = vec.y;
  }

  public void add(Vector2d vec) {
    x += vec.x;
    y += vec.y;
  }

  public void sub(Vector2d vec) {
    x -= vec.x;
    y -= vec.y;
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

  public double distanceSquared(Vector2d v) {
    double dx, dy, dz;
    dx = x - v.x;
    dy = y - v.y;
    return (dx * dx + dy * dy);
  }

  public double distance(Vector2d v) {
    return Math.sqrt(distanceSquared(v));
  }

  @Override
  public String toString() {
    return "Vector2d(" + x + ", " + y + ")";
  }

}
