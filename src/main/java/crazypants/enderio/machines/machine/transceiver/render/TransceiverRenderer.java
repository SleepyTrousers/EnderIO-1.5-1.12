package crazypants.enderio.machines.machine.transceiver.render;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.client.render.ManagedTESR;
import com.enderio.core.client.render.RenderUtil;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.machines.machine.transceiver.BlockTransceiver;
import crazypants.enderio.machines.machine.transceiver.TileTransceiver;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.machines.init.MachineObject.block_transceiver;

import javax.annotation.Nonnull;

@SideOnly(Side.CLIENT)
public class TransceiverRenderer extends ManagedTESR<TileTransceiver> {

  private static final float scale = 0.7f;

  public TransceiverRenderer() {
    super(block_transceiver.getBlock());
  }

  @Override
  protected boolean shouldRender(@Nonnull TileTransceiver te, @Nonnull IBlockState blockState, int renderPass) {
    return te.isActive();
  }

  @Override
  protected void renderTileEntity(@Nonnull TileTransceiver te, @Nonnull IBlockState blockState, float partialTicks, int destroyStage) {
    TextureAtlasSprite icon = ((BlockTransceiver) block_transceiver.getBlock()).getPortalIcon();

    float time = Math.abs(50 - (EnderIO.proxy.getTickCount() % 100)) / 50f;
    float localScale = scale + 0.05f - time * 0.1f;
    float alpha = 0.7f + time * 0.25f;

    BoundingBox bb = BoundingBox.UNIT_CUBE.scale(localScale, localScale, localScale);

    GlStateManager.color(1, 1, 1, alpha);
    GlStateManager.enableNormalize();
    RenderUtil.renderBoundingBox(bb, icon);
    GlStateManager.disableNormalize();
  }

}
