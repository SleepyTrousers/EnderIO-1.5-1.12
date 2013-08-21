package crazypants.render;

import crazypants.vecmath.Vector3d;

public interface VertexTransform {

  public abstract void apply(Vector3d vec);

}
