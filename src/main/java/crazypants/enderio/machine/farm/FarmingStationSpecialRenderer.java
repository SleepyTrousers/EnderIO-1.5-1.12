package crazypants.enderio.machine.farm;

import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.vecmath.Vector3f;

import crazypants.enderio.config.Config;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FarmingStationSpecialRenderer extends TileEntitySpecialRenderer<TileFarmStation> {

  @Override
  public void renderTileEntityAt(TileFarmStation tile, double x, double y, double z, float partialTickTime, int destroyStage) {
    if (!tile.notification.isEmpty() || Config.disableFarmNotification) {
    GlStateManager.enableLighting();
    GlStateManager.disableLighting();
      float offset = 0;
      for (FarmNotification note : tile.notification) {
        RenderUtil.drawBillboardedText(new Vector3f(x + 0.5, y + 1.5 + offset, z + 0.5), note.getDisplayString(), 0.25f);
        offset += 0.375f;
      }
    GlStateManager.enableLighting();
    }
  }

}
