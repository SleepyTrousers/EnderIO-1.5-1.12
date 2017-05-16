package crazypants.enderio.block;

import java.util.Random;

import javax.annotation.Nonnull;

import crazypants.enderio.IModObject;
import net.minecraft.block.BlockFire;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BlockColdFire extends BlockFire {

  public static BlockColdFire create(@Nonnull IModObject modObject) {
    BlockColdFire res = new BlockColdFire(modObject);
    res.initColdFire();
    return res;
  }

  private BlockColdFire(@Nonnull IModObject modObject) {
    setUnlocalizedName(modObject.getUnlocalisedName());
    setRegistryName(modObject.getUnlocalisedName());
    setTickRandomly(false);
    setHardness(0.0F);
    setLightLevel(1.0F);
  }

  protected void initColdFire() {// BlockFire already has a static init()
    GameRegistry.register(this);
  }

  @Override
  public void updateTick(@Nonnull World p_updateTick_1_, @Nonnull BlockPos p_updateTick_2_, @Nonnull IBlockState p_updateTick_3_,
      @Nonnull Random p_updateTick_4_) {
  }

}
