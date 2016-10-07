package crazypants.enderio.machine.transceiver.render;

import javax.annotation.Nonnull;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.ManagedTESR;
import com.enderio.core.client.render.RenderUtil;

import crazypants.enderio.EnderIO;
import crazypants.enderio.machine.transceiver.BlockTransceiver;
import crazypants.enderio.machine.transceiver.TileTransceiver;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.ModObject.blockTransceiver;

@SideOnly(Side.CLIENT)
public class TransceiverRenderer extends ManagedTESR<TileTransceiver> {

  private static final float scale = 0.7f;

  public TransceiverRenderer() {
    super(blockTransceiver.getBlock());
  }

  @Override
  protected boolean shouldRender(@Nonnull TileTransceiver te, @Nonnull IBlockState blockState, int renderPass) {
    return te.isActive() && renderPass == 1;
  }

  @Override
  protected void renderTileEntity(@Nonnull TileTransceiver te, @Nonnull IBlockState blockState, float partialTicks, int destroyStage) {
    TextureAtlasSprite icon = ((BlockTransceiver) blockTransceiver.getBlock()).getPortalIcon();

    float time = Math.abs(50 - (EnderIO.proxy.getTickCount() % 100)) / 50f;
    float localScale = scale + 0.05f - time * 0.1f;
    float alpha = 0.7f + time * 0.25f;

    BoundingBox bb = BoundingBox.UNIT_CUBE.scale(localScale, localScale, localScale);

    GlStateManager.enableNormalize();
    GlStateManager.disableLighting();
    GlStateManager.color(1, 1, 1, alpha);
    RenderUtil.renderBoundingBox(bb, icon);
    GlStateManager.disableRescaleNormal();
  }

}
