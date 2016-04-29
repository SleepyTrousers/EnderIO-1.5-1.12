package crazypants.enderio.machine.gauge;

import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import cofh.api.energy.IEnergyHandler;
import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.capbank.render.FillGaugeBakery;

public class TESRGauge extends TileEntitySpecialRenderer<TileGauge> {

  public TESRGauge() {
  }

  @Override
  public void renderTileEntityAt(TileGauge te, double x, double y, double z, float partialTicks, int destroyStage) {

    GlStateManager.disableLighting();
    GlStateManager.enableLighting();

    if (Minecraft.isAmbientOcclusionEnabled()) {
      GlStateManager.shadeModel(GL11.GL_SMOOTH);
    } else {
      GlStateManager.shadeModel(GL11.GL_FLAT);
    }

    World world = te.getWorld();
    Map<EnumFacing, IEnergyHandler> sides = BlockGauge.getDisplays(world, te.getPos());
    if (!sides.isEmpty()) {
      for (Entry<EnumFacing, IEnergyHandler> side : sides.entrySet()) {
        IEnergyHandler eh = side.getValue();
        EnumFacing face = side.getKey().getOpposite();
        int energyStored = eh.getEnergyStored(face);
        int maxEnergyStored = eh.getMaxEnergyStored(face);
        float ratio = maxEnergyStored > 0 ? (float) energyStored / (float) maxEnergyStored : 0f;
        FillGaugeBakery bakery = new FillGaugeBakery(world, ((TileEntity) eh).getPos(), face, BlockGauge.gaugeIcon.get(TextureAtlasSprite.class), ratio);
        if (bakery.canRender()) {
          GL11.glPushMatrix();
          GL11.glTranslated(x - face.getFrontOffsetX(), y - face.getFrontOffsetY(), z - face.getFrontOffsetZ());
          bakery.render();
          GL11.glPopMatrix();
        }
      }
    } else {
      double v = EnderIO.proxy.getTickCount() % 100 + partialTicks;
      if (v > 50) {
        v = 100 - v;
      }
      double ratio = v / 50d;
      for (EnumFacing face : EnumFacing.Plane.HORIZONTAL) {
        FillGaugeBakery bakery = new FillGaugeBakery(world, te.getPos().offset(face.getOpposite()), face, BlockGauge.gaugeIcon.get(TextureAtlasSprite.class),
            ratio);
        if (bakery.canRender()) {
          GL11.glPushMatrix();
          GL11.glTranslated(x - face.getFrontOffsetX(), y - face.getFrontOffsetY(), z - face.getFrontOffsetZ());
          bakery.render();
          GL11.glPopMatrix();
        }
      }
    }
  }

}
