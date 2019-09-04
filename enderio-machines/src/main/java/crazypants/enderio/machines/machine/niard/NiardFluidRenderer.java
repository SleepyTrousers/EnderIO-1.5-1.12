package crazypants.enderio.machines.machine.niard;

import javax.annotation.Nonnull;

import com.enderio.core.client.render.ManagedTESR;

import crazypants.enderio.base.render.util.HalfBakedQuad.HalfBakedList;
import crazypants.enderio.base.render.util.TankRenderHelper;
import crazypants.enderio.machines.init.MachineObject;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class NiardFluidRenderer extends ManagedTESR<TileNiard> {

  public NiardFluidRenderer() {
    super(MachineObject.block_niard.getBlock());
  }

  @Override
  protected boolean shouldRender(@Nonnull TileNiard te, @Nonnull IBlockState blockState, int renderPass) {
    return !te.getInputTank().isEmpty();
  }

  @Override
  protected void renderTileEntity(@Nonnull TileNiard te, @Nonnull IBlockState blockState, float partialTicks, int destroyStage) {
    HalfBakedList buffer = TankRenderHelper.mkTank(te.getInputTank(), 0.55, 0.55, 15.45, true);
    if (buffer != null) {
      buffer.render();
    }
  }

}
