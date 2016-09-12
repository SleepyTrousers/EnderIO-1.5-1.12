package crazypants.enderio.machine.generator.zombie;

import javax.annotation.Nonnull;

import com.enderio.core.client.render.ManagedTESR;

import crazypants.enderio.EnderIO;
import crazypants.enderio.render.util.TankRenderHelper;
import crazypants.enderio.render.util.HalfBakedQuad.HalfBakedList;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ZombieGeneratorRenderer extends ManagedTESR<TileZombieGenerator> {

  public ZombieGeneratorRenderer() {
    super(EnderIO.blockZombieGenerator);
  }

  @Override
  protected boolean shouldRender(@Nonnull TileZombieGenerator te, @Nonnull IBlockState blockState, int renderPass) {
    return !te.tank.isEmpty();
  }

  @Override
  protected void renderTileEntity(@Nonnull TileZombieGenerator te, @Nonnull IBlockState blockState, float partialTicks, int destroyStage) {
    HalfBakedList buffer = TankRenderHelper.mkTank(te.tank, 2.51, 1, 14, false);
    if (buffer != null) {
      buffer.render();
    }
  }

}
