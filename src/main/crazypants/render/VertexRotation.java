package crazypants.render;

import crazypants.vecmath.Quat4d;
import crazypants.vecmath.Vector3d;
import crazypants.vecmath.Vector3f;

public class VertexRotation implements VertexTransform {

  private final Vector3d center;
  private Quat4d quat;
  private double angle;
  private final Vector3d axis;

  public VertexRotation(double angle, Vector3d axis, Vector3d center) {
    this.center = new Vector3d(center);
    this.axis = new Vector3d(axis);
    this.angle = angle;
    quat = Quat4d.makeRotate(angle, axis);
  }

  @Override
  public void apply(Vector3d vec) {
    vec.sub(center);
    quat.rotate(vec);
    vec.add(center);
  }

  public void setAngle(double angle) {
    this.angle = angle;
    quat = Quat4d.makeRotate(angle, axis);
  }

  public void setAxis(Vector3d axis) {
    this.axis.set(axis);
    quat = Quat4d.makeRotate(angle, axis);
  }

  public void setCenter(Vector3d cen) {
    center.set(cen);
  }

  @Override
  public void applyToNormal(Vector3f vec) {
    quat.rotate(vec);
  }

}
