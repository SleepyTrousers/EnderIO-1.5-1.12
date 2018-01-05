package crazypants.enderio.base.farming.farmers;

import javax.annotation.Nonnull;

import crazypants.enderio.base.farming.IFarmer;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;

public interface IFarmerJoe extends IForgeRegistryEntry<IFarmerJoe> {

  boolean prepareBlock(@Nonnull IFarmer farm, @Nonnull BlockPos bc, @Nonnull Block block, @Nonnull IBlockState state);

  /**
   * 
   * @return true if this farmer wants to handle (==harvestBlock()) this location. Doesn't mean that it actually will harvest something, just that no other
   *         farmer will get the chance to do so.
   */
  boolean canHarvest(@Nonnull IFarmer farm, @Nonnull BlockPos bc, @Nonnull Block block, @Nonnull IBlockState state);

  boolean canPlant(@Nonnull ItemStack stack);

  IHarvestResult harvestBlock(@Nonnull IFarmer farm, @Nonnull BlockPos bc, @Nonnull Block block, @Nonnull IBlockState state);

}
