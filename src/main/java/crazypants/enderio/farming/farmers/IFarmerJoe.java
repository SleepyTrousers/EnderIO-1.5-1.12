package crazypants.enderio.machine.farm.farmers;

import javax.annotation.Nonnull;

import crazypants.enderio.machine.farm.TileFarmStation;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public interface IFarmerJoe {

  boolean prepareBlock(@Nonnull TileFarmStation farm, @Nonnull BlockPos bc, @Nonnull Block block, @Nonnull IBlockState state);

  /**
   * 
   * @return true if this farmer wants to handle (==harvestBlock()) this location. Doesn't mean that it actually will harvest something, just that no other
   *         farmer will get the chance to do so.
   */
  boolean canHarvest(@Nonnull TileFarmStation farm, @Nonnull BlockPos bc, @Nonnull Block block, @Nonnull IBlockState state);

  boolean canPlant(ItemStack stack);

  IHarvestResult harvestBlock(@Nonnull TileFarmStation farm, @Nonnull BlockPos bc, @Nonnull Block block, @Nonnull IBlockState state);

}
