package crazypants.enderio.powertools.machine.gauge;

import java.util.Map.Entry;

import javax.annotation.Nonnull;

import com.enderio.core.client.render.ManagedTESR;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.powertools.machine.capbank.render.FillGaugeBakery;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import static crazypants.enderio.powertools.init.PowerToolObject.block_gauge;

public class TESRGauge extends ManagedTESR<TileGauge> {

  public TESRGauge() {
    super(block_gauge.getBlock());
  }

  @Override
  protected boolean shouldRender(@Nonnull TileGauge te, @Nonnull IBlockState blockState, int renderPass) {
    te.collectData();
    return te.data != null;
  }

  @Override
  protected void renderTileEntity(@Nonnull TileGauge te, @Nonnull IBlockState blockState, float partialTicks, int destroyStage) {
    RenderHelper.enableStandardItemLighting();
    World world = te.getWorld();

    boolean renderedSomething = false;
    for (Entry<EnumFacing, Float> entry : te.data.entrySet()) {
      final EnumFacing face = entry.getKey();
      final Float value = entry.getValue();
      if (face != null && value != null) {
        FillGaugeBakery bakery = new FillGaugeBakery(world, te.getPos().offset(NullHelper.first(face.getOpposite(), EnumFacing.DOWN)), face,
            BlockGauge.gaugeIcon.get(TextureAtlasSprite.class), value);
        if (bakery.canRender()) {
          GlStateManager.pushMatrix();
          GlStateManager.translate(-face.getFrontOffsetX(), -face.getFrontOffsetY(), -face.getFrontOffsetZ());
          bakery.render();
          GlStateManager.popMatrix();
          renderedSomething = true;
        }
      }
    }

    if (!renderedSomething) {
      double v = EnderIO.proxy.getTickCount() % 100 + partialTicks;
      if (v > 50) {
        v = 100 - v;
      }
      double ratio = v / 50d;
      for (EnumFacing face : EnumFacing.Plane.HORIZONTAL) {
        FillGaugeBakery bakery = new FillGaugeBakery(world, te.getPos().offset(face.getOpposite()), face, BlockGauge.gaugeIcon.get(TextureAtlasSprite.class),
            ratio);
        if (bakery.canRender()) {
          GlStateManager.pushMatrix();
          GlStateManager.translate(-face.getFrontOffsetX(), -face.getFrontOffsetY(), -face.getFrontOffsetZ());
          bakery.render();
          GlStateManager.popMatrix();
        }
      }
    }
  }

}
