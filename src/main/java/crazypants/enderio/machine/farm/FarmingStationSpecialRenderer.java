package crazypants.enderio.machine.farm;

import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.vecmath.Vector3f;

import static org.lwjgl.opengl.GL11.GL_LIGHTING;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;

import crazypants.enderio.config.Config;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FarmingStationSpecialRenderer extends TileEntitySpecialRenderer<TileFarmStation> {

  private void renderFarmStationAt(TileFarmStation tile, double x, double y, double z, float partialTickTime) {

    String toRender = tile.notification;
    if ("".equals(toRender) || Config.disableFarmNotification) {
      return;
    }

    glDisable(GL_LIGHTING);
    RenderUtil.drawBillboardedText(new Vector3f(x + 0.5, y + 1.5, z + 0.5), toRender, 0.25f);
    glEnable(GL_LIGHTING);

  }

  @Override
  public void renderTileEntityAt(TileFarmStation tile, double x, double y, double z, float partialTickTime, int b) {
    renderFarmStationAt(tile, x, y, z, partialTickTime);
  }
}
