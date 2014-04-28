package crazypants.render;

import crazypants.vecmath.Vector3d;
import crazypants.vecmath.Vector3f;
import crazypants.vecmath.Vertex;

public interface VertexTransform {

  public abstract void apply(Vertex vertex);

  public abstract void apply(Vector3d vec);

  public abstract void applyToNormal(Vector3f vec);

}
