package crazypants.enderio.base.block.grave;

import javax.annotation.Nonnull;

import com.enderio.core.client.render.ManagedTESR;
import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.vecmath.Vector3f;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySkullRenderer;

public class TESRGrave extends ManagedTESR<TileGrave> {

  public TESRGrave(Block block) {
    super(block);
  }

  @Override
  protected void renderTileEntity(@Nonnull TileGrave te, @Nonnull IBlockState blockState, float partialTicks, int destroyStage) {
    TileEntitySkullRenderer.instance.render(te.getRenderDummy(), 0, 0, 0, partialTicks, destroyStage, 0);

    GlStateManager.disableDepth();
    RenderUtil.drawBillboardedText(new Vector3f(0.5, .75, 0.5), te.getOwner().getPlayerName(), 0.25f);
    GlStateManager.enableDepth();
  }

}
