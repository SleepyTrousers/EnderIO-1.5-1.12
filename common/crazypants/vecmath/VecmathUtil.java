package crazypants.vecmath;

import java.awt.Rectangle;

public class VecmathUtil {

  /**
   * Returns the distance from a point to a plane.
   * 
   * @param plane
   *          the plane.
   * @param point
   *          the point.
   * @return the distance between them.
   */
  public static double distanceFromPointToPlane(Vector4d plane, Vector3d point) {
    Vector4d newPoint = new Vector4d(point.x, point.y, point.z, 1);
    return plane.dot(newPoint);
  }

  public static void computePlaneEquation(Vector4d a, Vector4d b, Vector4d c, Vector4d result) {
    computePlaneEquation(new Vector3d(a.x, a.y, a.z), new Vector3d(b.x, b.y, b.z), new Vector3d(c.x, c.y, c.z), result);
  }

  public static Vector3d clamp(Vector3d v, double min, double max) {
    v.x = clamp(v.x, min, max);
    v.y = clamp(v.y, min, max);
    v.z = clamp(v.z, min, max);
    return v;
  }

  public static double clamp(double val, double min, double max) {
    return val < min ? min : (val > max ? max : val);
  }

  public static int clamp(int val, int min, int max) {
    return val < min ? min : (val > max ? max : val);
  }

  /**
   * Compute the plane equation <code>Ax + By + Cz + D = 0</code> for the plane
   * defined by the three points which lie on the plane, a, b, and c, and
   * placing the result into r. The plane equation can be summarised as the
   * normal vector of the plane (A,B,C) and the distance to the plane from the
   * origin (D).
   * 
   * @param a
   *          vector a.
   * @param b
   *          vector b.
   * @param c
   *          vector c.
   * @param result
   *          the result (A,B,C,D) of plane equation.
   */
  public static void computePlaneEquation(Vector3d a, Vector3d b,
      Vector3d c, Vector4d result) {
    Vector3d i = new Vector3d();
    Vector3d j = new Vector3d();
    Vector3d k = new Vector3d();

    // compute normal vector
    i.x = c.x - a.x;
    i.y = c.y - a.y;
    i.z = c.z - a.z;

    j.x = b.x - a.x;
    j.y = b.y - a.y;
    j.z = b.z - a.z;

    k.cross(j, i);
    k.normalize();

    // plane equation: Ax + By + Cz + D = 0
    result.x = k.x; // A
    result.y = k.y; // B
    result.z = k.z; // C
    result.w = -(result.x * a.x + result.y * a.y + result.z * a.z); // D
  }

  /**
   * Projects the point onto the plane.
   * 
   * @param plane
   *          the plane.
   * @param point
   *          the point.
   */
  public static void projectPointOntoPlane(Vector4d plane, Vector4d point) {
    double distance = plane.dot(point);
    Vector4d newPoint = new Vector4d(point);
    Vector3d planeNormal = new Vector3d(plane.x, plane.y, plane.z);
    planeNormal.normalize();
    planeNormal.scale(distance);
    newPoint.sub(new Vector4d(planeNormal.x, planeNormal.y, planeNormal.z, 0));
    point.set(newPoint);
  }

  /**
   * This method calculates the intersection between a line and a plane.
   * 
   * @param plane
   *          the plane (x,y,z = normal, w = distance from origin)
   * @param pointInLine
   *          a point in the line.
   * @param lineDirection
   *          the direction of the line.
   * @return the intersection or null if there is no intersection or the line is
   *         on the plane.
   */
  public static Vector3d computeIntersectionBetweenPlaneAndLine(Vector4d plane,
      Vector3d pointInLine, Vector3d lineDirection) {
    // check for no intersection
    Vector3d planeNormal = new Vector3d(plane.x, plane.y, plane.z);
    if(planeNormal.dot(lineDirection) == 0) {
      // line and plane are perpendicular
      return null;
    }
    // check if line is on the plane
    if(planeNormal.dot(pointInLine) + plane.w == 0) {
      return null;
    }

    // we have an intersection
    Vector4d point = new Vector4d(pointInLine.x, pointInLine.y, pointInLine.z, 1);
    Vector4d lineNorm = new Vector4d(lineDirection.x, lineDirection.y, lineDirection.z, 0);
    double t = -(plane.dot(point) / plane.dot(lineNorm));

    Vector3d result = new Vector3d(pointInLine);
    lineDirection.scale(t);
    result.add(lineDirection);
    return result;
  }

  /**
   * This function computes the ray that goes from the eye, through the
   * specified pixel.
   * 
   * @param camera
   *          the camera.
   * @param x
   *          the x pixel location (x = 0 is the left most pixel)
   * @param y
   *          the y pixel location (y = 0 is the bottom most pixel)
   * @param eyeOut
   *          the eyes position.
   * @param normalOut
   *          the normal description the directional component of the ray.
   */
  public static void computeRayForPixel(Rectangle vp, Matrix4d ipm, Matrix4d ivm, int x, int y,
      Vector3d eyeOut, Vector3d normalOut) {

    // grab the eye's position
    ivm.getTranslation(eyeOut);

    Matrix4d vpm = new Matrix4d();
    vpm.mul(ivm, ipm);

    // Calculate the pixel location in screen clip space (width and height from
    // -1 to 1)
    double screenX = (x - vp.getX()) / vp.getWidth();
    double screenY = (y - vp.getY()) / vp.getHeight();
    screenX = (screenX * 2.0) - 1.0;
    screenY = (screenY * 2.0) - 1.0;

    // Now calculate the XYZ location of this point on the near plane
    Vector4d tmp = new Vector4d();
    tmp.x = screenX;
    tmp.y = screenY;
    tmp.z = -1;
    tmp.w = 1.0;
    vpm.transform(tmp);

    double w = tmp.w;
    Vector3d nearXYZ = new Vector3d(tmp.x / w, tmp.y / w, tmp.z / w);

    // and then on the far plane
    tmp.x = screenX;
    tmp.y = screenY;
    tmp.z = 1;
    tmp.w = 1.0;
    vpm.transform(tmp);

    w = tmp.w;
    Vector3d farXYZ = new Vector3d(tmp.x / w, tmp.y / w, tmp.z / w);

    normalOut.set(farXYZ);
    normalOut.sub(nearXYZ);
    normalOut.normalize();

  }

  // /**
  // * Make a rotation Quat which will rotate vec1 to vec2
  // * <p/>
  // * This routine uses only fast geometric transforms, without costly acos/sin
  // computations. It's
  // * exact, fast, and with less degenerate cases than the acos/sin method.
  // * <p/>
  // * For an explanation of the math used, you may see for example:
  // * http://logiciels.cnes.fr/MARMOTTES/marmottes-mathematique.pdf
  // * <p/>
  // * NB: This is the rotation with shortest angle, which is the one equivalent
  // to the acos/sin
  // * transform method. Other rotations exists, for example to additionally
  // keep a local horizontal
  // * attitude.
  // *
  // * @param from
  // * rotate from this vector
  // * @param to
  // * to this one
  // * @return the rotation to apply to from to get to to.
  // */
  //
  // public static Quat4d makeRotate( Vector3d from, Vector3d to) {
  //
  // Quat4d res = new Quat4d();
  //
  // // This routine takes any vector as argument but normalized
  // // vectors are necessary, if only for computing the dot product.
  // // Too bad the API is that generic, it leads to performance loss.
  // // Even in the case the 2 vectors are not normalized but same length,
  // // the sqrt could be shared, but we have no way to know beforehand
  // // at this point, while the caller may know.
  // // So, we have to test... in the hope of saving at least a sqrt
  // Vector3d sourceVector = new Vector3d(from);
  // Vector3d targetVector = new Vector3d(to);
  //
  // double fromLen2 = sourceVector.lengthSquared();
  // double fromLen;
  // // normalize only when necessary, epsilon test
  // if ((fromLen2 < 1.0 - 1e-7) || (fromLen2 > 1.0 + 1e-7)) {
  // fromLen = Math.sqrt(fromLen2);
  // sourceVector.x /= fromLen;
  // sourceVector.y /= fromLen;
  // sourceVector.z /= fromLen;
  //
  // } else {
  // fromLen = 1.0;
  // }
  //
  // double toLen2 = targetVector.lengthSquared();
  // // normalize only when necessary, epsilon test
  // if ((toLen2 < 1.0 - 1e-7) || (toLen2 > 1.0 + 1e-7)) {
  // double toLen;
  // // re-use fromLen for case of mapping 2 vectors of the same length
  // if ((toLen2 > fromLen2 - 1e-7) && (toLen2 < fromLen2 + 1e-7)) {
  // toLen = fromLen;
  // } else {
  // toLen = Math.sqrt(toLen2);
  // }
  //
  // targetVector.x /= toLen;
  // targetVector.y /= toLen;
  // targetVector.z /= toLen;
  // }
  //
  // // Now let's get into the real stuff
  // // Use "dot product plus one" as test as it can be re-used later on
  // double dotProdPlus1 = 1.0 + sourceVector.dot(targetVector);
  //
  // // Check for degenerate case of full u-turn. Use epsilon for detection
  // if (dotProdPlus1 < 1e-7) {
  //
  // // Get an orthogonal vector of the given vector
  // // in a plane with maximum vector coordinates.
  // // Then use it as quaternion axis with pi angle
  // // Trick is to realize one value at least is >0.6 for a normalized vector.
  // if (Math.abs(sourceVector.x) < 0.6) {
  //
  // double norm = Math.sqrt(1.0 - sourceVector.x * sourceVector.x);
  // res.x = 0.0;
  // res.y = sourceVector.z / norm;
  // res.z = -sourceVector.y / norm;
  // res.w = 0.0;
  // } else if (Math.abs(sourceVector.y) < 0.6) {
  // double norm = Math.sqrt(1.0 - sourceVector.y * sourceVector.y);
  // res.x = -sourceVector.z / norm;
  // res.y = 0.0;
  // res.z = sourceVector.x / norm;
  // res.w = 0.0;
  // } else {
  // double norm = Math.sqrt(1.0 - sourceVector.z * sourceVector.z);
  // res.x = sourceVector.y / norm;
  // res.y = -sourceVector.x / norm;
  // res.z = 0.0;
  // res.w = 0.0;
  // }
  // } else {
  // // Find the shortest angle quaternion that transforms normalized vectors
  // // into one other. Formula is still valid when vectors are colinear
  // double s = Math.sqrt(0.5 * dotProdPlus1);
  // double val = (2.0 * s);
  // targetVector.x /= val;
  // targetVector.y /= val;
  // targetVector.z /= val;
  //
  // Vector3d tmp = new Vector3d();
  // tmp.cross(sourceVector, targetVector);
  //
  // res.x = tmp.x;
  // res.y = tmp.y;
  // res.z = tmp.z;
  // res.w = s;
  // }
  //
  // return res;
  // }
  //
  // /**
  // * Makes a rotation of angle (radians) around the axis specified by the
  // x,y,z values.
  // *
  // * @param angle
  // * the angle in radians for the rotation
  // * @param axis
  // * the axis to rotate about.
  // * @return the quaternion defining the rotation.
  // */

  /**
   * Creates a perspective projection matrix.
   * 
   * @param fovDegrees
   *          The field of view angle in degrees.
   * @param near
   *          near plane.
   * @param far
   *          far plane.
   * @param viewportWidth
   *          viewport width.
   * @param viewportHeight
   *          viewport height.
   * @return the matrix.
   */

  public static Matrix4d createProjectionMatrixAsPerspective(double fovDegrees, double near,
      double far, int viewportWidth, int viewportHeight) {

    Matrix4d matrix = new Matrix4d();
    // for impl details see gluPerspective doco in OpenGL reference manual
    double aspect = (double) viewportWidth / (double) viewportHeight;

    double theta = (Math.toRadians(fovDegrees) / 2d);
    double f = Math.cos(theta) / Math.sin(theta);

    double a = (far + near) / (near - far);
    double b = (2d * far * near) / (near - far);

    matrix.set(new double[] { f / aspect, 0, 0, 0, 0, f, 0, 0, 0, 0, a, b, 0, 0, -1, 0 });

    return matrix;
  }

  /**
   * Creates a projection matrix as per glFrustrum.
   * 
   * @param left
   *          coordinate of left clip plane.
   * @param right
   *          coordinate of right clip plane.
   * @param bottom
   *          coordinate of bottom clip plane.
   * @param top
   *          coordinate of left top plane.
   * @param zNear
   *          distance of the near plane.
   * @param zFar
   *          distance of the near plane.
   * @return the matrix.
   */

  public static Matrix4d createProjectionMatrixAsPerspective(double left, double right,
      double bottom, double top, double zNear, double zFar) {

    double A = (right + left) / (right - left);
    double B = (top + bottom) / (top - bottom);
    double C = (Math.abs(zFar) > Double.MAX_VALUE) ? -1. : -(zFar + zNear) / (zFar - zNear);
    double D = (Math.abs(zFar) > Double.MAX_VALUE) ? -2. * zNear : -2.0 * zFar * zNear
        / (zFar - zNear);

    Matrix4d matrix = new Matrix4d();
    matrix.set(new double[] { 2.0 * zNear / (right - left), 0.0, 0.0, 0.0, 0.0,
        2.0 * zNear / (top - bottom), 0.0, 0.0, A, B, C, -1.0, 0.0, 0.0, D, 0.0

    });

    matrix.transpose();
    return matrix;
  }

  /**
   * Sets the orthographic projection matrix.
   * 
   * @param left
   *          the left value.
   * @param right
   *          the right value.
   * @param bottom
   *          the bottom value.
   * @param top
   *          the top value.
   * @param near
   *          near plane.
   * @param far
   *          far plane.
   * @return the ortho matrix.
   */

  public static Matrix4d createProjectionMatrixAsOrtho(double left, double right, double bottom,
      double top, double near, double far) {

    Matrix4d matrix = new Matrix4d();
    // for impl details see glOrtho doco in OpenGL reference manual
    double tx = -((right + left) / (right - left));
    double ty = -((top + bottom) / (top - bottom));
    double tz = -((far + near) / (far - near));

    matrix.set(new double[] { 2d / (right - left), 0, 0, tx, 0, 2d / (top - bottom), 0, ty, 0, 0,
        -2d / (far - near), tz, 0, 0, 0, 1 });

    return matrix;
  }

  /**
   * Sets the near and far values on an existing perspective projection matrix.
   * 
   * @param projMat
   *          the matrix to be modified.
   * @param near
   *          the new near value.
   * @param far
   *          the new far value.
   */
  public static void setNearFarOnPerspectiveProjectionMatrix(Matrix4d projMat,
      double near, double far) {

    projMat.transpose();

    double transNearPlane = (-near * projMat.getElement(2, 2) + projMat.getElement(3, 2))
        / (-near * projMat.getElement(2, 3) + projMat.getElement(3, 3));
    double transFarPlane = (-far * projMat.getElement(2, 2) + projMat.getElement(3, 2))
        / (-far * projMat.getElement(2, 3) + projMat.getElement(3, 3));

    double ratio = Math.abs(2.0 / (transNearPlane - transFarPlane));
    double center = -(transNearPlane + transFarPlane) / 2.0;

    Matrix4d mat = new Matrix4d();
    mat.setIdentity();
    mat.setElement(2, 2, ratio);
    mat.setElement(3, 2, center * ratio);

    projMat.mul(mat);
    projMat.transpose();

  }

  /**
   * Creates a look at matrix.
   * 
   * @param eyePos
   *          the position of the eye.
   * @param lookAtPos
   *          the point to look at.
   * @param upVec
   *          the up vector.
   * @return the look at matrix.
   */

  public static Matrix4d createMatrixAsLookAt(Vector3d eyePos,
      Vector3d lookAtPos, Vector3d upVec) {

    Vector3d eye = new Vector3d(eyePos);
    Vector3d lookAt = new Vector3d(lookAtPos);
    Vector3d up = new Vector3d(upVec);

    Vector3d forwardVec = new Vector3d(lookAt);
    forwardVec.sub(eye);
    forwardVec.normalize();

    Vector3d sideVec = new Vector3d();
    sideVec.cross(forwardVec, up);
    sideVec.normalize();

    Vector3d upVed = new Vector3d();
    upVed.cross(sideVec, forwardVec);
    upVed.normalize();

    Matrix4d mat = new Matrix4d(sideVec.x, sideVec.y, sideVec.z, 0, upVed.x, upVed.y, upVed.z, 0,
        -forwardVec.x, -forwardVec.y, -forwardVec.z, 0, 0, 0, 0, 1);

    eye.negate();
    // mat.transform(eye);
    mat.transformNormal(eye);
    mat.setTranslation(eye);

    return mat;
  }

  /**
   * Pre-multiplies the vector by the matrix.
   * 
   * @param v
   *          the vector.
   * @param mat
   *          the matrix.
   * @return the result of the multiplication.
   */

  public static Vector3d preMultiply(Vector3d v, Matrix4d mat) {
    Matrix4d m = new Matrix4d(mat);
    m.transpose();
    double d = 1.0f / (m.getElement(0, 3) * v.x + m.getElement(1, 3) * v.y + m.getElement(2, 3)
        * v.z + m.getElement(3, 3));
    double x = (m.getElement(0, 0) * v.x + m.getElement(1, 0) * v.y + m.getElement(2, 0) * v.z + m
        .getElement(3, 0)) * d;
    double y = (m.getElement(0, 1) * v.x + m.getElement(1, 1) * v.y + m.getElement(2, 1) * v.z + m
        .getElement(3, 1)) * d;
    double z = (m.getElement(0, 2) * v.x + m.getElement(1, 2) * v.y + m.getElement(2, 2) * v.z + m
        .getElement(3, 2) * d);
    return new Vector3d(x, y, z);
  }

  /**
   * Extracts the s,t,r and q eye planes from the specified matrix.
   * 
   * @param matrix
   *          the matrix to extract the planes from.
   * @return the s,t,r and q planes from the specified matrix.
   */

  public static Vector4d[] getEyePlanesForMatrix(Matrix4d matrix) {

    Matrix4d copy = new Matrix4d(matrix);
    copy.transpose();

    Vector4d[] res = new Vector4d[4];
    // s plane
    res[0] = new Vector4d(copy.getElement(0, 0), copy.getElement(1, 0), copy.getElement(2, 0),
        copy.getElement(3, 0));
    // t plane
    res[1] = new Vector4d(copy.getElement(0, 1), copy.getElement(1, 1), copy.getElement(2, 1),
        copy.getElement(3, 1));
    // r plane
    res[2] = new Vector4d(copy.getElement(0, 2), copy.getElement(1, 2), copy.getElement(2, 2),
        copy.getElement(3, 2));
    // q plane
    res[3] = new Vector4d(copy.getElement(0, 3), copy.getElement(1, 3), copy.getElement(2, 3),
        copy.getElement(3, 3));

    return res;
  }

  /**
   * Computes the cross product of the two tuples.
   * 
   * @param vec1
   *          the first tuple.
   * @param vec2
   *          the second tuple.
   * @return the cross product of the two tuples.
   */

  public static Vector3d cross(Vector3d vec1, Vector3d vec2) {
    Vector3d res = new Vector3d();
    res.cross(new Vector3d(vec1), new Vector3d(vec2));
    return res;
  }

  /**
   * Returns the distance between the two point from and to.
   * 
   * @param from
   *          the from point.
   * @param to
   *          the to point.
   * @return the distance between the two point from and to.
   */
  public static double distance(Vector3d from, Vector3d to) {
    return from.distance(to);
  }

  /**
   * Returns the distance squared between the two point from and to.
   * 
   * @param from
   *          the from point.
   * @param to
   *          the to point.
   * @return the distance squared between the two point from and to.
   */
  public static double distanceSquared(Vector3d from, Vector3d to) {
    return from.distanceSquared(to);
  }

  /**
   * Extracts the directional vectors from the specified view matrix.
   * 
   * @param matrix
   *          the view matrix.
   * @param upVecOut
   *          the up vector.
   * @param sideVecOut
   *          the side vector.
   * @param lookVecOut
   *          the look vector.
   */
  public static void getVectorsForMatrix(Matrix4d matrix, Vector3d upVecOut,
      Vector3d sideVecOut, Vector3d lookVecOut) {

    sideVecOut.set(matrix.getElement(0, 0), matrix.getElement(0, 1), matrix.getElement(0, 2));
    sideVecOut.normalize();

    upVecOut.set(matrix.getElement(1, 0), matrix.getElement(1, 1), matrix.getElement(1, 2));
    upVecOut.normalize();

    lookVecOut.set(matrix.getElement(2, 0), matrix.getElement(2, 1), matrix.getElement(2, 2));
    lookVecOut.negate();
    lookVecOut.normalize();

  }

  /**
   * Extracts the up vector from the specified view matrix.
   * 
   * @param matrix
   *          the matrix.
   * @return the up vector from the specified view matrix.
   */

  public static Vector3d getUpFromMatrix(Matrix4d matrix) {
    Vector3d res = new Vector3d(matrix.getElement(1, 0), matrix.getElement(1, 1),
        matrix.getElement(1, 2));
    res.normalize();
    return res;
  }

}
