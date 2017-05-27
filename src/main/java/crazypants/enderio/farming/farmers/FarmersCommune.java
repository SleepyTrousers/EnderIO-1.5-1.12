package crazypants.enderio.farming.farmers;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.farming.IFarmer;
import crazypants.util.Prep;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class FarmersCommune implements IFarmerJoe {

  public static final @Nonnull FarmersCommune instance = new FarmersCommune();
  private static @Nonnull NNList<ItemStack> disableTrees = new NNList<ItemStack>();

  public static void joinCommune(@Nonnull IFarmerJoe joe) {
    if (joe instanceof CustomSeedFarmer) {
      CustomSeedFarmer customSeedFarmer = (CustomSeedFarmer) joe;
      if (customSeedFarmer.doesDisableTreeFarm())
        disableTrees.add(customSeedFarmer.getSeeds());
    }
    instance.farmers.add(joe);
  }

  public static void leaveCommune(@Nonnull IFarmerJoe joe) {
    throw new UnsupportedOperationException("As if this would be implemented. The commune is for life!");
  }

  private final @Nonnull NNList<IFarmerJoe> farmers = new NNList<IFarmerJoe>();

  private FarmersCommune() {
  }

  @Override
  public boolean canHarvest(@Nonnull IFarmer farm, @Nonnull BlockPos bc, @Nonnull Block block, @Nonnull IBlockState meta) {
    for (IFarmerJoe joe : farmers) {
      if (joe.canHarvest(farm, bc, block, meta)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public IHarvestResult harvestBlock(@Nonnull IFarmer farm, @Nonnull BlockPos bc, @Nonnull Block block, @Nonnull IBlockState meta) {
    for (IFarmerJoe joe : farmers) {
      if (!ignoreTreeHarvest(farm, bc, joe) && joe.canHarvest(farm, bc, block, meta)) {
        return joe.harvestBlock(farm, bc, block, meta);
      }
    }
    return null;
  }

  @Override
  public boolean prepareBlock(@Nonnull IFarmer farm, @Nonnull BlockPos bc, @Nonnull Block block, @Nonnull IBlockState meta) {
    for (IFarmerJoe joe : farmers) {
      if (joe.prepareBlock(farm, bc, block, meta)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean canPlant(@Nonnull ItemStack stack) {
    for (IFarmerJoe joe : farmers) {
      if (joe.canPlant(stack)) {
        return true;
      }
    }
    return false;
  }

  private boolean ignoreTreeHarvest(@Nonnull IFarmer farm, @Nonnull BlockPos bc, IFarmerJoe joe) {
    if (!(joe instanceof TreeFarmer)) {
      return false;
    }
    ItemStack stack = farm.getSeedTypeInSuppliesFor(bc);
    if (Prep.isInvalid(stack)) {
      return false;
    }
    for (ItemStack disableTreeStack : disableTrees) {
      if (disableTreeStack.isItemEqual(stack))
        return true;
    }
    return false;
  }

}
