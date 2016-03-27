package crazypants.enderio.machine.generator.zombie;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.RenderUtil;

import crazypants.enderio.machine.killera.KillerJoeRenderer;

@SideOnly(Side.CLIENT)
public class ZombieGeneratorRenderer extends TileEntitySpecialRenderer<TileZombieGenerator> {

  @Override
  public void renderTileEntityAt(TileZombieGenerator te, double x, double y, double z, float tick, int b) {

    if (te != null) {
      RenderUtil.setupLightmapCoords(te.getPos(), te.getWorld());
    }

    GL11.glPushMatrix();
    GL11.glTranslatef((float) x, (float) y, (float) z);
    if (MinecraftForgeClient.getRenderPass() == 1 && te != null) {
      KillerJoeRenderer.renderFluid(te.fuelTank);
    }
    GL11.glPopMatrix();
  }

}
