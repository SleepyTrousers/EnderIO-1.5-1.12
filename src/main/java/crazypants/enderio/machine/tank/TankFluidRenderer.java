package crazypants.enderio.machine.tank;

import com.enderio.core.client.render.ManagedTESR;
import crazypants.enderio.render.util.HalfBakedQuad.HalfBakedList;
import crazypants.enderio.render.util.TankRenderHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

import static crazypants.enderio.ModObject.blockTank;

@SideOnly(Side.CLIENT)
public class TankFluidRenderer extends ManagedTESR<TileTank> {

  public TankFluidRenderer() {
    super(blockTank.getBlock());
  }

  @Override
  protected boolean shouldRender(@Nonnull TileTank te, @Nonnull IBlockState blockState, int renderPass) {
    return !te.tank.isEmpty();
  }

  @Override
  protected void renderTileEntity(@Nonnull TileTank te, @Nonnull IBlockState blockState, float partialTicks, int destroyStage) {
    HalfBakedList buffer = TankRenderHelper.mkTank(te.tank, 0.45, 0.5, 15.5, false);
    if (buffer != null) {
      buffer.render();
    }
  }

}
