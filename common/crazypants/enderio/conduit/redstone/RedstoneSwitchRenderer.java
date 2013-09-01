package crazypants.enderio.conduit.redstone;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Icon;
import net.minecraftforge.common.ForgeDirection;
import crazypants.enderio.EnderIO;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.conduit.geom.CollidableComponent;
import crazypants.enderio.conduit.geom.ConduitGeometryUtil;
import crazypants.enderio.conduit.render.ConduitBundleRenderer;
import crazypants.enderio.conduit.render.DefaultConduitRenderer;
import crazypants.render.BoundingBox;
import crazypants.render.CubeRenderer;
import crazypants.render.VertexRotation;
import crazypants.render.VertexTransform;
import crazypants.vecmath.Vector3d;

public class RedstoneSwitchRenderer extends DefaultConduitRenderer {

  
  
  

    
  static RedstoneSwitchRenderer instance;
  
  public static RedstoneSwitchRenderer createInstance(float conduitScale) {
    instance = new RedstoneSwitchRenderer(conduitScale);    
    return instance;
  }
  
  

  private final VertexTransform[] xForms;
  private final BoundingBox switchBounds;
  private final BoundingBox connectorBounds;

  private final BoundingBox[] aabb;

  private RedstoneSwitchRenderer(float conduitScale) {
    
    float size = Math.max(0.2f, conduitScale * 0.5f);
    float halfWidth = size / 3;
    float halfHeight = size / 2;
    
    //float DEPTH = 0.05f;
    float halfDepth = 0.025f;
    float distance = Math.max(0.25f, conduitScale * 0.3f);

    BoundingBox bb = new BoundingBox(0.5 - halfWidth, 0.5 - halfHeight, 0.5 - halfDepth, 0.5 + halfWidth, 0.5 + halfHeight, 0.5 + halfDepth);
    switchBounds = bb.translate(0, 0, distance);

    float connectorHalfWidth = (float)Math.max(0.015, conduitScale * 0.05);
    connectorBounds = new BoundingBox(0.5 - connectorHalfWidth, 0.5 - connectorHalfWidth, 0.5 - connectorHalfWidth, 0.5 + connectorHalfWidth, 0.5 + connectorHalfWidth, 0.5 + distance);

    Vector3d axis = new Vector3d(0, 1, 0);
    Vector3d p = new Vector3d(0.5, 0.5, 0.5);
    xForms = new VertexTransform[4];
    double angle = Math.toRadians(45);
    for (int i = 0; i < xForms.length; i++) {
      // TODO
      // xForms[i] = new Rotation(angle, axis, p);
      xForms[i] = new VertexRotation(angle, axis, p);
      angle += Math.toRadians(90);
    }

    aabb = new BoundingBox[xForms.length];
    for (int i = 0; i < xForms.length; i++) {
      aabb[i] = switchBounds.transform(xForms[i]);
    }

  }

  public BoundingBox[] getAABB() {
    return aabb;
  }

  @Override
  public boolean isRendererForConduit(IConduit conduit) {
    return conduit.getClass() == RedstoneSwitch.class;
  }

  @Override
  public void renderEntity(ConduitBundleRenderer conduitBundleRenderer, IConduitBundle bundle, IConduit conduit, double x, double y, double z,
      float partialTick,
      float worldLight) {

    super.renderEntity(conduitBundleRenderer, bundle, conduit, x, y, z, partialTick, worldLight);

    RedstoneSwitch sw = (RedstoneSwitch) conduit;

    Tessellator tessellator = Tessellator.instance;
    float selfIllum = Math.max(worldLight, conduit.getSelfIlluminationForState(null));
    tessellator.setColorOpaque_F(selfIllum, selfIllum, selfIllum);

    Icon[] icons = new Icon[6];
    for (int i = 0; i < icons.length; i++) {
      icons[i] = EnderIO.blockConduitBundle.getConnectorIcon();
    }
    icons[1] = sw.getSwitchIcon();
    ;

    Vector3d trans = ConduitGeometryUtil.instance.getTranslation(ForgeDirection.UNKNOWN, bundle.getOffset(IRedstoneConduit.class, ForgeDirection.UNKNOWN));
    BoundingBox bb = switchBounds.translate(trans);

    for (VertexTransform tf : xForms) {
      CubeRenderer.render(bb, icons, tf);
    }
    bb = connectorBounds.translate(trans);
    for (VertexTransform tf : xForms) {
      CubeRenderer.render(bb, icons[0], tf);
    }

  }

  @Override
  protected boolean renderComponent(CollidableComponent component) {
    return !RedstoneSwitch.SWITCH_TAG.equals(component.data);
  }

}
