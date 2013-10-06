package crazypants.render;

import crazypants.vecmath.Vector3d;
import crazypants.vecmath.Vector3f;

public class VertexTranslation implements VertexTransform {

  private double x;
  private double y;
  private double z;

  public VertexTranslation(double x, double y, double z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public VertexTranslation(float x, float y, float z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public VertexTranslation(Vector3d trans) {
    this(trans.x, trans.y, trans.z);
  }

  public VertexTranslation(Vector3f trans) {
    this(trans.x, trans.y, trans.z);
  }

  @Override
  public void apply(Vector3d vec) {
    vec.x += x;
    vec.y += y;
    vec.z += z;
  }

  public void set(double x, double y, double z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public void set(Vector3d trans) {
    set(trans.x, trans.y, trans.z);
  }

  @Override
  public void applyToNormal(Vector3f vec) {

  }

}
