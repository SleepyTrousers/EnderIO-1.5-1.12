package crazypants.enderio.base.block.coldfire;

import java.util.Random;

import javax.annotation.Nonnull;

import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.render.IDefaultRenderers;
import net.minecraft.block.BlockFire;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockColdFire extends BlockFire implements IDefaultRenderers {

  public static BlockColdFire create(@Nonnull IModObject modObject) {
    return new BlockColdFire(modObject);
  }

  private BlockColdFire(@Nonnull IModObject modObject) {
    modObject.apply(this);
    setTickRandomly(false);
    setHardness(0.0F);
    setLightLevel(1.0F);
  }

  @Override
  public void updateTick(@Nonnull World p_updateTick_1_, @Nonnull BlockPos p_updateTick_2_, @Nonnull IBlockState p_updateTick_3_,
      @Nonnull Random p_updateTick_4_) {
  }

}
