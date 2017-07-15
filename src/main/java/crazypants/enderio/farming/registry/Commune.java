package crazypants.enderio.farming.registry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.farming.IFarmer;
import crazypants.enderio.farming.farmers.IFarmerJoe;
import crazypants.enderio.farming.farmers.IHarvestResult;
import crazypants.enderio.farming.farmers.TreeFarmer;
import crazypants.enderio.farming.registry.Registry.Callback;
import crazypants.util.Prep;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class Commune implements IFarmerJoe {

  public static final @Nonnull Commune instance = new Commune();

  static @Nonnull NNList<ItemStack> disableTrees = new NNList<ItemStack>();

  private Commune() {
  }

  @Override
  public boolean canHarvest(@Nonnull IFarmer farm, @Nonnull BlockPos bc, @Nonnull Block block, @Nonnull IBlockState meta) {
    return Registry.foreach(new Callback<Boolean>() {
      @Override
      public Boolean run(@Nonnull IFarmerJoe joe) {
        return joe.canHarvest(farm, bc, block, meta) ? Boolean.TRUE : null;
      }
    }) != null;
  }

  @Override
  public IHarvestResult harvestBlock(@Nonnull IFarmer farm, @Nonnull BlockPos bc, @Nonnull Block block, @Nonnull IBlockState meta) {
    return Registry.foreach(new Callback<IHarvestResult>() {
      @Override
      public IHarvestResult run(@Nonnull IFarmerJoe joe) {
        return !ignoreTreeHarvest(farm, bc, joe) && joe.canHarvest(farm, bc, block, meta) ? joe.harvestBlock(farm, bc, block, meta) : null;
      }
    });
  }

  @Override
  public boolean prepareBlock(@Nonnull IFarmer farm, @Nonnull BlockPos bc, @Nonnull Block block, @Nonnull IBlockState meta) {
    return Registry.foreach(new Callback<Boolean>() {
      @Override
      public Boolean run(@Nonnull IFarmerJoe joe) {
        return joe.prepareBlock(farm, bc, block, meta) ? Boolean.TRUE : null;
      }
    }) != null;
  }

  @Override
  public boolean canPlant(@Nonnull ItemStack stack) {
    return Registry.foreach(new Callback<Boolean>() {
      @Override
      public Boolean run(@Nonnull IFarmerJoe joe) {
        return joe.canPlant(stack) ? Boolean.TRUE : null;
      }
    }) != null;
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

  // not used here:

  @Override
  public IFarmerJoe setRegistryName(ResourceLocation name) {
    return null;
  }

  @Override
  @Nullable
  public ResourceLocation getRegistryName() {
    return null;
  }

  @Override
  public Class<? super IFarmerJoe> getRegistryType() {
    return null;
  }

}
