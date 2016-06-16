package crazypants.enderio.machine.tank;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.RenderUtil;

import crazypants.enderio.render.HalfBakedQuad.HalfBakedList;
import crazypants.enderio.render.TankRenderHelper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TankFluidRenderer extends TileEntitySpecialRenderer<TileTank> {

  @Override
  public void renderTileEntityAt(TileTank te, double x, double y, double z, float partialTicks, int destroyStage) {

    if (te != null && MinecraftForgeClient.getRenderPass() == 1) {
      HalfBakedList buffer = TankRenderHelper.mkTank(te.tank, 0.45, 0.5, 15.5, false);
      if (buffer != null) {        
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x, (float) y, (float) z);
        buffer.render();
        GL11.glPopMatrix();
      }
    }
  }

}
