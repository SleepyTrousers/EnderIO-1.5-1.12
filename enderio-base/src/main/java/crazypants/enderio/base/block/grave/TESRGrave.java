package crazypants.enderio.base.block.grave;

import javax.annotation.Nonnull;

import com.enderio.core.client.render.ManagedTESR;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySkullRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class TESRGrave extends ManagedTESR<TileGrave> {

  private static final @Nonnull ResourceLocation ITEM_TEXTURES = new ResourceLocation("textures/entity/skeleton/skeleton.png"); // TODO

  public TESRGrave(Block block) {
    super(block);
  }

  @Override
  protected void renderTileEntity(@Nonnull TileGrave te, @Nonnull IBlockState blockState, float partialTicks, int destroyStage) {
    TileEntitySkullRenderer.instance.render(te.getRenderDummy(), 0, 0, 0, partialTicks, destroyStage, 0);
  }

  @Override
  protected void renderItem() {
    GlStateManager.pushMatrix();
    GlStateManager.disableCull();
    bindTexture(ITEM_TEXTURES);
    TileEntitySkullRenderer.instance.renderSkull(0.0F, 0.0F, 0.0F, EnumFacing.UP, 180.0F, -1, null, -1, 0.0F);
    GlStateManager.enableCull();
    GlStateManager.popMatrix();
  }

}
