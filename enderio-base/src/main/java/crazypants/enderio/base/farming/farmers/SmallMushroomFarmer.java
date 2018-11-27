package crazypants.enderio.base.farming.farmers;

import javax.annotation.Nonnull;

import crazypants.enderio.api.farm.IFarmer;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class SmallMushroomFarmer extends PlantableFarmer {

  @Override
  public boolean canPlant(@Nonnull ItemStack stack) {
    return false;
  }

  @Override
  public boolean prepareBlock(@Nonnull IFarmer farm, @Nonnull BlockPos bc, @Nonnull IBlockState state) {
    return false;
  }

  @Override
  public boolean canHarvest(@Nonnull IFarmer farm, @Nonnull BlockPos bc, @Nonnull IBlockState state) {
    Block block = state.getBlock();
    if (block == Blocks.RED_MUSHROOM || block == Blocks.BROWN_MUSHROOM) {
      System.out.println("foo");
      Block block1 = farm.getBlockState(bc.down()).getBlock();
      // hardcoded check from net.minecraft.world.gen.feature.WorldGenBigMushroom.generate()
      return block1 != Blocks.DIRT && block1 != Blocks.GRASS && block1 != Blocks.MYCELIUM;
    }
    return false;
  }

  @Override
  protected boolean isPlantableForBlock(@Nonnull IFarmer farm, @Nonnull ItemStack stack, @Nonnull Block block) {
    // don't replant...
    return false;
  }

}
