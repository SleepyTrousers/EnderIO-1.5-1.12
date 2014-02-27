package crazypants.render;

import java.util.Collection;

import crazypants.vecmath.Vector3d;
import crazypants.vecmath.Vector3f;

public class VertexTransformComposite implements VertexTransform {

  private final VertexTransform[] xforms;

  public VertexTransformComposite(VertexTransform... xforms) {
    this.xforms = xforms;
  }

  VertexTransformComposite(Collection<VertexTransform> xformsIn) {
    xforms = new VertexTransform[xformsIn.size()];
    int i = 0;
    for (VertexTransform xform : xformsIn) {
      xforms[i] = xform;
      i++;
    }
  }

  @Override
  public void apply(Vector3d vec) {
    for (VertexTransform xform : xforms) {
      xform.apply(vec);
    }

  }

  @Override
  public void applyToNormal(Vector3f vec) {
    for (VertexTransform xform : xforms) {
      xform.applyToNormal(vec);
    }
  }

}
