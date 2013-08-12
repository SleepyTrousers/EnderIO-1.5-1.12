package crazypants.render;

import crazypants.vecmath.Quat4d;
import crazypants.vecmath.Vector3d;

public class VertexRotation implements VertexTransform {

  private final Vector3d center;
  private final Quat4d quat;
  
  public VertexRotation(double angle, Vector3d axis, Vector3d center) {      
    this.center = center;
    quat = Quat4d.makeRotate(angle, axis);
  }

  @Override
  public void apply(Vector3d vec) {
    vec.sub(center);
    quat.rotate(vec);
    vec.add(center);    
  }

}
