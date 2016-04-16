package crazypants.enderio.machine.farm;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.vecmath.Vector3f;

import crazypants.enderio.config.Config;

@SideOnly(Side.CLIENT)
public class FarmingStationSpecialRenderer extends TileEntitySpecialRenderer<TileFarmStation> {

  @Override
  public void renderTileEntityAt(TileFarmStation tile, double x, double y, double z, float partialTickTime, int destroyStage) {
    String toRender = tile.notification;
    if ("".equals(toRender) || Config.disableFarmNotification) {
      return;
    }

    GlStateManager.disableLighting();
    RenderUtil.drawBillboardedText(new Vector3f(x + 0.5, y + 1.5, z + 0.5), toRender, 0.25f);
    GlStateManager.enableLighting();
  }

}
