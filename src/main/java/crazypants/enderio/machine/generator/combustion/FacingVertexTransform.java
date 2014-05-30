package crazypants.enderio.machine.generator.combustion;

import crazypants.render.VertexTransform;
import crazypants.vecmath.Vector3d;
import crazypants.vecmath.Vector3f;
import crazypants.vecmath.Vertex;

class FacingVertexTransform implements VertexTransform {


  boolean transX;

  public FacingVertexTransform() {
  }

  public void setFacing(int facing) {
    transX = facing != 4 && facing != 5;
  }

  @Override
  public void apply(Vertex vertex) {
    apply(vertex.xyz);
  }

  @Override
  public void apply(Vector3d vec) {
    if(vec.y > 0.8 || vec.y < 0.2) {
      if(transX) {
        vec.x -= 0.5;
        vec.x *= 0.6;
        vec.x += 0.5;
      } else {
        vec.z -= 0.5;
        vec.z *= 0.6;
        vec.z += 0.5;
      }
    }
  }

  @Override
  public void applyToNormal(Vector3f vec) {
  }

}