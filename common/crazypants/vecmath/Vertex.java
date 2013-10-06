package crazypants.vecmath;

public class Vertex {

  public final Vector3d xyz = new Vector3d();
  public final Vector2f uv = new Vector2f();
  public final Vector3f normal = new Vector3f();

  public Vertex() {
  }

  public Vertex(Vertex other) {
    xyz.set(other.xyz);
    uv.set(other.uv);
    normal.set(other.normal);
  }

  public Vertex(Vector3d xyz, Vector3f normal, Vector2f uv) {
    this.xyz.set(xyz);
    this.normal.set(normal);
    this.uv.set(uv);
  }

  public void setXYZ(double x, double y, double z) {
    xyz.set(x, y, z);
  }

  public void setUV(double u, double v) {
    uv.set(u, v);
  }

  public void setNormal(double x, double y, double z) {
    normal.set((float) x, (float) y, (float) z);
    normal.normalize();
  }

  public void transform(Matrix4d xform) {
    xform.transform(xyz);
    xform.transformNormal(normal);
  }

  public void translate(Vector3d trans) {
    xyz.add(trans);
  }

  public double x() {
    return xyz.x;
  }

  public double y() {
    return xyz.y;
  }

  public double z() {
    return xyz.z;
  }

  public float nx() {
    return normal.x;
  }

  public float ny() {
    return normal.y;
  }

  public float nz() {
    return normal.z;
  }

  public float u() {
    return uv.x;
  }

  public float v() {
    return uv.y;
  }

  @Override
  public String toString() {
    return "Vertex [xyz=" + xyz + ", uv=" + uv + "]";
  }

}
