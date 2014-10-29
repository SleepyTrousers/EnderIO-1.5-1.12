package crazypants.enderio.machine.farm;

import crazypants.enderio.config.Config;
import crazypants.render.RenderUtil;
import crazypants.vecmath.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import static org.lwjgl.opengl.GL11.*;

public class FarmingStationSpecialRenderer extends TileEntitySpecialRenderer{

  private void renderFarmStationAt(TileFarmStation tile, double x, double y, double z, float partialTickTime) {
    Minecraft mc = Minecraft.getMinecraft();
    FontRenderer fnt = mc.fontRenderer;
    Tessellator tessellator = Tessellator.instance;
    String toRender = tile.notification;

    if ("".equals(toRender) || Config.disableFarmNotification) {
      return;
    }
    
    glDisable(GL_LIGHTING);
    RenderUtil.drawBillboardedText(new Vector3f(x + 0.5, y + 1.5, z + 0.5), toRender, 0.25f);
    glEnable(GL_LIGHTING);

  }  
  
  @Override
  public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTickTime) {
    if (tile instanceof TileFarmStation) {
      renderFarmStationAt((TileFarmStation) tile, x, y, z, partialTickTime);
    }
  }
}
