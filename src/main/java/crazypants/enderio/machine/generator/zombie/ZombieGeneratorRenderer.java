package crazypants.enderio.machine.generator.zombie;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.RenderUtil;

import crazypants.enderio.render.HalfBakedQuad.HalfBakedList;
import crazypants.enderio.render.TankRenderHelper;

@SideOnly(Side.CLIENT)
public class ZombieGeneratorRenderer extends TileEntitySpecialRenderer<TileZombieGenerator> {

  @Override
  public void renderTileEntityAt(TileZombieGenerator te, double x, double y, double z, float partialTicks, int destroyStage) {

    if (te != null && MinecraftForgeClient.getRenderPass() == 1) {
      HalfBakedList buffer = TankRenderHelper.mkTank(te.fuelTank, 2.51, 1, 14, false);
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
