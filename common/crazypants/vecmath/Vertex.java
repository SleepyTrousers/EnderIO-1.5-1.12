package crazypants.vecmath;

public class Vertex {

  public Vector3d xyz = new Vector3d();
  public Vector2f uv = null;
  public Vector3f normal = null;
  public Vector4f color = null;

  public int brightness = -1;

  public Vertex() {
  }

  public Vertex(Vertex other) {
    xyz.set(other.xyz);
    if(other.uv != null) {
      uv = new Vector2f(other.uv);
    }
    if(other.normal != null) {
      normal = new Vector3f(other.normal);
    }
    if(other.color != null) {
      color = new Vector4f(other.color);
    }
    brightness = other.brightness;
  }

  public Vertex(Vector3d xyz, Vector3f normal, Vector2f uv) {
    this.xyz.set(xyz);
    this.normal = new Vector3f(normal);
    this.uv = new Vector2f(uv);
  }

  public void setXYZ(double x, double y, double z) {
    xyz.set(x, y, z);
  }

  public void setUV(double u, double v) {
    if(uv == null) {
      uv = new Vector2f(u, v);
    } else {
      uv.set(u, v);
    }
  }

  public void setNormal(double x, double y, double z) {
    if(normal == null) {
      normal = new Vector3f();
    }
    normal.set((float) x, (float) y, (float) z);
    normal.normalize();
  }

  public void setNormal(Vector3f normal2) {
    if(normal2 == null) {
      normal = null;
      return;
    }
    setNormal(normal2.x, normal2.y, normal2.z);
  }

  public void setBrightness(int brightness) {
    this.brightness = brightness;
  }

  public Vector4f getColor() {
    return color;
  }

  public void setColor(Vector4f color) {
    this.color = color;
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

  public float r() {
    return color.x;
  }

  public float g() {
    return color.y;
  }

  public float b() {
    return color.z;
  }

  public float a() {
    return color.w;
  }

  @Override
  public String toString() {
    return "Vertex [xyz=" + xyz + ", uv=" + uv + ", normal=" + normal + ", color=" + color + ", brightness=" + brightness + "]";
  }

}
