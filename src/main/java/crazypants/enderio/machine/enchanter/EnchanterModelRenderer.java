package crazypants.enderio.machine.enchanter;

import javax.annotation.Nonnull;

import com.enderio.core.client.render.ManagedTESR;
import com.enderio.core.client.render.RenderUtil;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.ModObject.blockEnchanter;

@SideOnly(Side.CLIENT)
public class EnchanterModelRenderer extends ManagedTESR<TileEnchanter> {

  public EnchanterModelRenderer() {
    super(blockEnchanter.getBlock());
  }

  private static final String TEXTURE = "enderio:textures/blocks/BookStand.png";

  private EnchanterModel model = new EnchanterModel();

  @Override
  protected void renderTileEntity(@Nonnull TileEnchanter te, @Nonnull IBlockState blockState, float partialTicks, int destroyStage) {
    renderModel(te.getFacing());
  }

  @Override
  protected void renderItem() {
    renderModel(EnumFacing.NORTH);
  }

  private void renderModel(EnumFacing facing) {

    GlStateManager.pushMatrix();

    GlStateManager.translate(0.5, 1.5, 0.5);
    GlStateManager.rotate(180, 1, 0, 0);

    GlStateManager.rotate(facing.getHorizontalIndex() * 90f, 0, 1, 0);

    RenderUtil.bindTexture(TEXTURE);
    model.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);

    GlStateManager.translate(-0.5, -1.5, -0.5);
    GlStateManager.popMatrix();
  }

}
