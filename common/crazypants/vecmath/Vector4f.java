package crazypants.vecmath;

public class Vector4f {
  public float x;
  public float y;
  public float z;
  public float w;

  public Vector4f() {
    x = 0;
    y = 0;
    z = 0;
    w = 0;
  }

  public Vector4f(float x, float y, float z, float w) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.w = w;
  }

  public Vector4f(double x, double y, double z, double w) {
    this.x = (float) x;
    this.y = (float) y;
    this.z = (float) z;
    this.w = (float) w;
  }

  public Vector4f(Vector4f other) {
    this(other.x, other.y, other.z, other.w);
  }

  public void set(Vector4f vec) {
    x = vec.x;
    y = vec.y;
    z = vec.z;
    w = vec.w;
  }

  public void set(float x, float y, float z, float w) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.w = w;
  }

  public void add(Vector4f vec) {
    x += vec.x;
    y += vec.y;
    z += vec.z;
    w += vec.w;
  }

  public void sub(Vector4f vec) {
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

  public double dot(Vector4f other) {
    return x * other.x + y * other.y + z * other.z + w * other.w;
  }

  public double lengthSquared() {
    return x * x + y * y + z * z + w * w;
  }

  public double length() {
    return Math.sqrt(lengthSquared());
  }

  public Vector3f toVector3f() {
    return new Vector3f(x, y, z);
  }

  @Override
  public String toString() {
    return "Vector4f(" + x + ", " + y + ", " + z + ", " + w + ")";
  }
}
