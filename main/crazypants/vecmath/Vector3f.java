package crazypants.vecmath;

public class Vector3f {

  public float x;
  public float y;
  public float z;

  public Vector3f() {
    x = 0;
    y = 0;
    z = 0;
  }

  public Vector3f(double x, double y, double z) {
    this.x = (float) x;
    this.y = (float) y;
    this.z = (float) z;
  }

  public Vector3f(float x, float y, float z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public Vector3f(Vector3d other) {
    this(other.x, other.y, other.z);
  }

  public Vector3f(Vector3f other) {
    this(other.x, other.y, other.z);
  }

  public void set(float x, float y, float z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public void set(Vector3f vec) {
    x = vec.x;
    y = vec.y;
    z = vec.z;
  }

  public void add(Vector3f vec) {
    x += vec.x;
    y += vec.y;
    z += vec.z;
  }

  public void add(Vector3d vec) {
    x += vec.x;
    y += vec.y;
    z += vec.z;
  }

  public void sub(Vector3f vec) {
    x -= vec.x;
    y -= vec.y;
    z -= vec.z;
  }

  public void sub(Vector3d vec) {
    x -= vec.x;
    y -= vec.y;
    z -= vec.z;
  }

  public void negate() {
    x = -x;
    y = -y;
    z = -z;
  }

  public void scale(float s) {
    x *= s;
    y *= s;
    z *= s;
  }

  public void normalize() {
    double scale = 1.0 / Math.sqrt(x * x + y * y + z * z);
    x *= scale;
    y *= scale;
    z *= scale;
  }

  public double dot(Vector3f other) {
    return x * other.x + y * other.y + z * other.z;
  }

  public double lengthSquared() {
    return x * x + y * y + z * z;
  }

  public double length() {
    return Math.sqrt(lengthSquared());
  }

  @Override
  public String toString() {
    return "Vector3f(" + x + ", " + y + ", " + z + ")";
  }

  public Vector3d asVector3d() {
    return new Vector3d(x, y, z);
  }

}
