package crazypants.vecmath;

import java.awt.Rectangle;

public class Camera {

  private Rectangle viewport;

  private Matrix4d projectionTranspose;
  private Matrix4d projectionMatrix;
  private Matrix4d projectionInverse;

  private Matrix4d viewTranspose;
  private Matrix4d viewMatrix;
  private Matrix4d viewInverse;

  public boolean isValid() {
    return viewMatrix != null && projectionMatrix != null && viewport != null;
  }

  public void setProjectionMatrixAsPerspective(double fovDegrees, double near, double far, int viewportWidth, int viewportHeight) {
    setProjectionMatrix(VecmathUtil.createProjectionMatrixAsPerspective(fovDegrees, near, far, viewportWidth, viewportHeight));
  }

  public void setViewMatrixAsLookAt(Vector3d eyePos, Vector3d lookAtPos, Vector3d upVec) {
    setViewMatrix(VecmathUtil.createMatrixAsLookAt(eyePos, lookAtPos, upVec));
  }

  public Vector3d getEyePoint() {
    Matrix4d vpm = new Matrix4d();
    Matrix4d ivm = getInverseViewMatrix();
    if(ivm == null) {
      return null;
    }
    Matrix4d ipm = getInverseProjectionMatrix();
    if(ipm == null) {
      return null;
    }
    vpm.mul(ivm, ipm);

    Vector3d eye = new Vector3d();
    ivm.getTranslation(eye);
    return eye;
  }

  public boolean getRayForPixel(int x, int y, Vector3d eyeOut, Vector3d normalOut) {
    if(isValid()) {
      VecmathUtil.computeRayForPixel(viewport, getInverseProjectionMatrix(), getInverseViewMatrix(), x, y, eyeOut, normalOut);
      return true;
    }
    return false;
  }

  public Vector2d getScreenPoint(Vector3d point3d) {
    Vector4d transPoint = new Vector4d(point3d.x, point3d.y, point3d.z, 1);

    viewMatrix.transform(transPoint);
    projectionMatrix.transform(transPoint);

    int halfWidth = viewport.width / 2;
    int halfHeight = viewport.height / 2;
    Vector2d screenPos = new Vector2d(transPoint.x, transPoint.y);
    screenPos.scale(1 / transPoint.w);
    screenPos.x = screenPos.x * halfWidth + halfWidth;
    screenPos.y = -screenPos.y * halfHeight + halfHeight;

    return screenPos;
  }

  public void setViewport(Rectangle viewport) {
    if(viewport != null) {
      setViewport(viewport.x, viewport.y, viewport.width, viewport.height);
    }
  }

  public void setViewport(int x, int y, int width, int height) {
    viewport = new Rectangle(x, y, width, height);
  }

  public Rectangle getViewport() {
    return viewport;
  }

  public Matrix4d getProjectionMatrix() {
    return projectionMatrix;
  }

  public Matrix4d getTransposeProjectionMatrix() {
    return projectionTranspose;
  }

  public Matrix4d getInverseProjectionMatrix() {
    if(projectionMatrix != null) {
      if(projectionInverse == null) {
        projectionInverse = new Matrix4d(projectionMatrix);
        projectionInverse.invert();
      }
      return projectionInverse;
    } else {
      return null;
    }
  }

  public void setProjectionMatrix(Matrix4d matrix) {
    if(projectionMatrix == null) {
      projectionMatrix = new Matrix4d();
      projectionTranspose = new Matrix4d();
    }
    projectionMatrix.set(matrix);
    projectionTranspose.set(matrix);
    projectionTranspose.transpose();
    projectionInverse = null;
  }

  public Matrix4d getViewMatrix() {
    return viewMatrix;
  }

  public Matrix4d getTransposeViewMatrix() {
    return viewTranspose;
  }

  public Matrix4d getInverseViewMatrix() {
    if(viewMatrix != null) {
      if(viewInverse == null) {
        viewInverse = new Matrix4d(viewMatrix);
        viewInverse.invert();
      }
      return viewInverse;
    } else {
      return null;
    }
  }

  public void setViewMatrix(Matrix4d matrix) {
    if(viewMatrix == null) {
      viewMatrix = new Matrix4d();
      viewTranspose = new Matrix4d();
    }
    viewMatrix.set(matrix);
    viewTranspose.set(matrix);
    viewTranspose.transpose();
    viewInverse = null;
  }

}
