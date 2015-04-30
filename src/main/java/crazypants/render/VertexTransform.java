package crazypants.render;

import com.enderio.core.common.vecmath.Vector3d;
import com.enderio.core.common.vecmath.Vector3f;
import com.enderio.core.common.vecmath.Vertex;

public interface VertexTransform {

  public abstract void apply(Vertex vertex);

  public abstract void apply(Vector3d vec);

  public abstract void applyToNormal(Vector3f vec);

}
