package crazypants.vecmath;

public class ViewFrustum {

  public static final int LTN = 0;
  public static final int LTF = 1;
  public static final int LBN = 2;
  public static final int LBF = 3;
  public static final int RTN = 4;
  public static final int RTF = 5;
  public static final int RBN = 6;
  public static final int RBF = 7;

  public static final int VERTEX_COUNT = 8;

  public static final int PLANE_COUNT = 6;

  private static final int TOP = 0;
  private static final int BOTTOM = 1;
  private static final int LEFT = 2;
  private static final int RIGHT = 3;
  private static final int NEAR = 4;
  private static final int FAR = 5;

  private Vector4d vertices[] = new Vector4d[8];
  private Vector4d planes[] = new Vector4d[6];

  private Vector3d eye;

  private Vector3d min;
  private Vector3d max;

  public ViewFrustum() {
    eye = new Vector3d();
    min = new Vector3d();
    max = new Vector3d();
    for (int i = 0; i < VERTEX_COUNT; i++) {
      vertices[i] = new Vector4d();
    }
    for (int i = 0; i < PLANE_COUNT; i++) {
      planes[i] = new Vector4d();
    }
  }

  public ViewFrustum(ViewFrustum other) {
    eye = new Vector3d(other.eye);
    min = new Vector3d(other.min);
    max = new Vector3d(other.max);
    for (int i = 0; i < VERTEX_COUNT; i++) {
      vertices[i] = new Vector4d(other.vertices[i]);
    }
    for (int i = 0; i < PLANE_COUNT; i++) {
      planes[i] = new Vector4d(other.planes[i]);
    }
  }

  public boolean containsPoint(Vector3d point) {
    for (Vector4d plane : planes) {
      if(distanceFromPointToPlane(plane, point) < 0) {
        return false;
      }
    }
    return true;
  }

  private Vector3d toThreeComponent(Vector3d from) {
    return new Vector3d(from.x, from.y, from.z);
  }

  private double distanceFromPointToPlane(Vector4d plane, Vector3d point) {
    Vector4d newPoint = new Vector4d(point.x, point.y, point.z, 1);
    return plane.dot(newPoint);
  }

  public Vector4d getVertex(int index) {
    return vertices[index];
  }

  public Vector3d getEye() {
    return eye;
  }

  public Vector3d getMin() {
    return min;
  }

  public Vector3d getMax() {
    return max;
  }

  public Vector4d getLeftPlane() {
    return getPlane(LEFT);
  }

  public Vector4d getRightPlane() {
    return getPlane(RIGHT);
  }

  public Vector4d getTopPlane() {
    return getPlane(TOP);
  }

  public Vector4d getBottomPlane() {
    return getPlane(BOTTOM);
  }

  public Vector4d getNearPlane() {
    return getPlane(NEAR);
  }

  public Vector4d getFarPlane() {
    return getPlane(FAR);
  }

  /**
   * @param ivm
   *          the inverse view transformation matrix
   * @param ipm
   *          the inverse projection matrix
   */
  public void computeFrustum(Matrix4d ivm, Matrix4d ipm) {

    Matrix4d vpm = new Matrix4d();
    vpm.mul(ivm, ipm);

    ivm.getTranslation(eye);

    vertices[LBF].set(-1, -1, 1, 1);
    vertices[LTF].set(-1, 1, 1, 1);
    vertices[RTF].set(1, 1, 1, 1);
    vertices[RBF].set(1, -1, 1, 1);
    vertices[LBN].set(-1, -1, -1, 1);
    vertices[LTN].set(-1, 1, -1, 1);
    vertices[RTN].set(1, 1, -1, 1);
    vertices[RBN].set(1, -1, -1, 1);

    min.set(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
    max.set(-Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE);

    for (int i = 0; i < VERTEX_COUNT; i++) {
      vpm.transform(vertices[i]);
      double w = vertices[i].w;
      // apply perspective
      vertices[i].x /= w;
      vertices[i].y /= w;
      vertices[i].z /= w;

      min.x = Math.min(min.x, vertices[i].x);
      min.y = Math.min(min.y, vertices[i].y);
      min.z = Math.min(min.z, vertices[i].z);

      max.x = Math.max(max.x, vertices[i].x);
      max.y = Math.max(max.y, vertices[i].y);
      max.z = Math.max(max.z, vertices[i].z);
    }

    VecmathUtil.computePlaneEquation(vertices[LBN], vertices[LBF], vertices[LTF], planes[LEFT]);
    VecmathUtil.computePlaneEquation(vertices[LBN], vertices[LBF], vertices[LTF], planes[LEFT]);
    VecmathUtil.computePlaneEquation(vertices[RBN], vertices[RTF], vertices[RBF], planes[RIGHT]);
    VecmathUtil.computePlaneEquation(vertices[LTN], vertices[LTF], vertices[RTF], planes[TOP]);
    VecmathUtil.computePlaneEquation(vertices[LBN], vertices[RBF], vertices[LBF], planes[BOTTOM]);
    VecmathUtil.computePlaneEquation(vertices[LBN], vertices[LTN], vertices[RTN], planes[NEAR]);
    VecmathUtil.computePlaneEquation(vertices[LBF], vertices[RTF], vertices[LTF], planes[FAR]);

  }

  private final Vector4d getPlane(int index) {
    assert ((index >= 0) && (index < PLANE_COUNT)) : "Illegal index : 0 <= index < " + PLANE_COUNT;
    return planes[index];
  }

}
