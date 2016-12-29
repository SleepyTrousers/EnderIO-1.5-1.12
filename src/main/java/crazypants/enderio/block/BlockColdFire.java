package crazypants.enderio.block;

import java.util.Random;

import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import net.minecraft.block.BlockFire;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BlockColdFire extends BlockFire {

  public static BlockColdFire create() {
    BlockColdFire res = new BlockColdFire();
    res.initColdFire();
    return res;
  }

  private BlockColdFire() {
    setUnlocalizedName(ModObject.blockColdFire.getUnlocalisedName());
    setRegistryName(ModObject.blockColdFire.getUnlocalisedName());
    setTickRandomly(false);
    setHardness(0.0F);
    setLightLevel(1.0F);
  }

  protected void initColdFire() {// BlockFire already has a static init()
    GameRegistry.register(this);
  }

  @Override
  public void updateTick(World p_updateTick_1_, BlockPos p_updateTick_2_, IBlockState p_updateTick_3_, Random p_updateTick_4_) {
  }

}
