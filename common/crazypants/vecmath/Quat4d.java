package crazypants.vecmath;

public class Quat4d {

  public static Quat4d makeRotate(double angle, Vector3d axis) {
    double epsilon = 0.0000001;

    double x = axis.x;
    double y = axis.y;
    double z = axis.z;
    double length = Math.sqrt(x * x + y * y + z * z);
    if(length < epsilon) {
      // ~zero length axis, so reset rotation to zero.
      return new Quat4d();
    }

    double inversenorm = 1.0 / length;
    double coshalfangle = Math.cos(0.5 * angle);
    double sinhalfangle = Math.sin(0.5 * angle);

    Quat4d res = new Quat4d();
    res.x = x * sinhalfangle * inversenorm;
    res.y = y * sinhalfangle * inversenorm;
    res.z = z * sinhalfangle * inversenorm;
    res.w = coshalfangle;
    return res;
  }

  public double x;
  public double y;
  public double z;
  public double w;

  public Quat4d() {
  }

  public void rotate(Vector3d vec) {
    double d = -x * vec.x - y * vec.y - z * vec.z;
    double d1 = w * vec.x + y * vec.z - z * vec.y;
    double d2 = w * vec.y - x * vec.z + z * vec.x;
    double d3 = w * vec.z + x * vec.y - y * vec.x;
    vec.x = d1 * w - d * x - d2 * z + d3 * y;
    vec.y = d2 * w - d * y + d1 * z - d3 * x;
    vec.z = d3 * w - d * z - d1 * y + d2 * x;
  }

  public void rotate(Vector3f vec) {
    double d = -x * vec.x - y * vec.y - z * vec.z;
    double d1 = w * vec.x + y * vec.z - z * vec.y;
    double d2 = w * vec.y - x * vec.z + z * vec.x;
    double d3 = w * vec.z + x * vec.y - y * vec.x;
    vec.x = (float) (d1 * w - d * x - d2 * z + d3 * y);
    vec.y = (float) (d2 * w - d * y + d1 * z - d3 * x);
    vec.z = (float) (d3 * w - d * z - d1 * y + d2 * x);
  }

}
