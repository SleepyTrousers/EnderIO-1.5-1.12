package crazypants.vecmath;

public class Vector4d {

  public double x;
  public double y;
  public double z;
  public double w;

  public Vector4d() {
    x = 0;
    y = 0;
    z = 0;
    w = 0;
  }

  public Vector4d(double x, double y, double z, double w) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.w = w;
  }

  public Vector4d(Vector4d other) {
    this(other.x, other.y, other.z, other.w);
  }

  public void set(Vector4d vec) {
    x = vec.x;
    y = vec.y;
    z = vec.z;
    w = vec.w;
  }

  public void set(double x, double y, double z, double w) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.w = w;
  }

  public void add(Vector4d vec) {
    x += vec.x;
    y += vec.y;
    z += vec.z;
    w += vec.w;
  }

  public void sub(Vector4d vec) {
    x -= vec.x;
    y -= vec.y;
    z -= vec.z;
    w -= vec.w;
  }

  public void negate() {
    x = -x;
    y = -y;
    z = -z;
    w = -w;
  }

  public void scale(double s) {
    x *= s;
    y *= s;
    z *= s;
    w *= s;
  }

  public void normalize() {
    double scale = 1.0 / Math.sqrt(x * x + y * y + z * z + w * w);
    scale(scale);
  }

  public double dot(Vector4d other) {
    return x * other.x + y * other.y + z * other.z + w * other.w;
  }

  public double lengthSquared() {
    return x * x + y * y + z * z + w * w;
  }

  public double length() {
    return Math.sqrt(lengthSquared());
  }

  @Override
  public String toString() {
    return "Vector4d(" + x + ", " + y + ", " + z + ", " + w + ")";
  }
}
