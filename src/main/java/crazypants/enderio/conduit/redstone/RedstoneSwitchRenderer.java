package crazypants.enderio.conduit.redstone;

import java.util.List;

import com.enderio.core.api.client.render.VertexTransform;
import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.common.vecmath.Vector3d;
import com.enderio.core.common.vecmath.Vector4f;
import com.enderio.core.common.vecmath.Vertex;

import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.conduit.geom.CollidableComponent;
import crazypants.enderio.conduit.geom.ConduitConnectorType;
import crazypants.enderio.conduit.geom.ConduitGeometryUtil;
import crazypants.enderio.conduit.render.BakedQuadBuilder;
import crazypants.enderio.conduit.render.ConduitBundleRenderManager;
import crazypants.enderio.conduit.render.ConduitBundleRenderer;
import crazypants.enderio.conduit.render.DefaultConduitRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;

public class RedstoneSwitchRenderer extends DefaultConduitRenderer {

  private static final RedstoneSwitchRenderer instance = new RedstoneSwitchRenderer();

  public static RedstoneSwitchRenderer getInstance() {
    return instance;
  }

  private final VertexTransform[] xForms;
  private final BoundingBox switchBounds;
  private final BoundingBox connectorBounds;

  private RedstoneSwitchRenderer() {
    xForms = RedstoneSwitchBounds.getInstance().xForms;
    switchBounds = RedstoneSwitchBounds.getInstance().switchBounds;
    connectorBounds = RedstoneSwitchBounds.getInstance().connectorBounds;
  }

  @Override
  public boolean isRendererForConduit(IConduit conduit) {
    return conduit.getClass() == RedstoneSwitch.class;
  }

  @Override
  public void addBakedQuads(ConduitBundleRenderer conduitBundleRenderer, IConduitBundle bundle, IConduit conduit, float brightness, List<BakedQuad> quads) {
    // TODO Auto-generated method stub
    super.addBakedQuads(conduitBundleRenderer, bundle, conduit, brightness, quads);

    RedstoneSwitch sw = (RedstoneSwitch) conduit;

    float selfIllum = Math.max(brightness, conduit.getSelfIlluminationForState(null));
    // tessellator.setColorOpaque_F(selfIllum, selfIllum, selfIllum);
    Vector4f col = new Vector4f(selfIllum, selfIllum, selfIllum, 1);

    TextureAtlasSprite[] icons = new TextureAtlasSprite[6];
    for (int i = 0; i < icons.length; i++) {
      icons[i] = ConduitBundleRenderManager.instance.getConnectorIcon(ConduitConnectorType.INTERNAL);
    }
    icons[3] = sw.getSwitchIcon();

    Vector3d trans = ConduitGeometryUtil.instance.getTranslation(null, bundle.getOffset(IRedstoneConduit.class, null));
    BoundingBox bb = switchBounds.translate(trans);

    for (VertexTransform tf : xForms) {
      for (EnumFacing facing : EnumFacing.values()) {
        // BoundingBox xfBounds = bb.transform(tf);

        List<Vertex> corners = applyXForm(bb.getCornersWithUvForFace(facing), tf);
        BakedQuadBuilder.addBakedQuads(quads, corners, icons[facing.ordinal()], col);
      }
    }
    bb = connectorBounds.translate(trans);
    for (VertexTransform tf : xForms) {
      // BoundingBox xfBounds = bb.transform(tf);
      // BakedQuadBuilder.addBakedQuads(quads, xfBounds, icons[0], col);
      for (EnumFacing facing : EnumFacing.values()) {
        List<Vertex> corners = applyXForm(bb.getCornersWithUvForFace(facing), tf);
        BakedQuadBuilder.addBakedQuads(quads, corners, icons[facing.ordinal()], col);
      }
    }

  }

  private List<Vertex> applyXForm(List<Vertex> list, VertexTransform tf) {
    for (Vertex corner : list) {
      tf.apply(corner.xyz);
    }
    return list;
  }

  @Override
  protected void addConduitQuads(IConduitBundle bundle, IConduit conduit, TextureAtlasSprite tex, CollidableComponent component, float lightIn,
      List<BakedQuad> quads) {

    super.addConduitQuads(bundle, conduit, tex, component, lightIn, quads);

  }

  @Override
  protected boolean renderComponent(CollidableComponent component) {
    return !RedstoneSwitch.SWITCH_TAG.equals(component.data);
  }

}
