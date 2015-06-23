package crazypants.enderio.conduit.redstone;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

import com.enderio.core.api.client.render.VertexTransform;
import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.CubeRenderer;
import com.enderio.core.common.vecmath.Vector3d;

import crazypants.enderio.EnderIO;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.conduit.geom.CollidableComponent;
import crazypants.enderio.conduit.geom.ConduitConnectorType;
import crazypants.enderio.conduit.geom.ConduitGeometryUtil;
import crazypants.enderio.conduit.render.ConduitBundleRenderer;
import crazypants.enderio.conduit.render.DefaultConduitRenderer;

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
      float worldLight, RenderBlocks rb) {

    super.renderEntity(conduitBundleRenderer, bundle, conduit, x, y, z, partialTick, worldLight, rb);

    RedstoneSwitch sw = (RedstoneSwitch) conduit;

    Tessellator tessellator = Tessellator.instance;
    float selfIllum = Math.max(worldLight, conduit.getSelfIlluminationForState(null));
    tessellator.setColorOpaque_F(selfIllum, selfIllum, selfIllum);

    IIcon[] icons = new IIcon[6];
    for (int i = 0; i < icons.length; i++) {
      icons[i] = EnderIO.blockConduitBundle.getConnectorIcon(ConduitConnectorType.INTERNAL);
    }
    icons[3] = sw.getSwitchIcon();    

    Vector3d trans = ConduitGeometryUtil.instance.getTranslation(ForgeDirection.UNKNOWN, bundle.getOffset(IRedstoneConduit.class, ForgeDirection.UNKNOWN));
    BoundingBox bb = switchBounds.translate(trans);

    for (VertexTransform tf : xForms) {
      CubeRenderer.render(bb, icons, tf,null);
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
