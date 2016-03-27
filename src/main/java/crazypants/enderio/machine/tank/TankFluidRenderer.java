package crazypants.enderio.machine.tank;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.RenderUtil;

import crazypants.enderio.render.HalfBakedQuad.HalfBakedList;
import crazypants.enderio.render.TankRenderHelper;

@SideOnly(Side.CLIENT)
public class TankFluidRenderer extends TileEntitySpecialRenderer<TileTank> {

  @Override
  public void renderTileEntityAt(TileTank te, double x, double y, double z, float partialTicks, int destroyStage) {

    if (te != null && MinecraftForgeClient.getRenderPass() == 1) {
      HalfBakedList buffer = TankRenderHelper.mkTank(te.tank, 0.01, 0.01, 15.99, false);
      if (buffer != null) {
        RenderUtil.setupLightmapCoords(te.getPos(), te.getWorld());
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x, (float) y, (float) z);
        buffer.render();
        GL11.glPopMatrix();
      }
    }
  }

}
