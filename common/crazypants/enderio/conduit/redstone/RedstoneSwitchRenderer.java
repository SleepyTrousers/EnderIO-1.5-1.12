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
import crazypants.render.VertexTransform;
import crazypants.vecmath.Vector3d;

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
