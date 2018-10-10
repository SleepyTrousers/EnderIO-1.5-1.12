package crazypants.enderio.base.farming.registry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.api.farm.IFarmer;
import crazypants.enderio.api.farm.IFarmerJoe;
import crazypants.enderio.api.farm.IHarvestResult;
import crazypants.enderio.base.farming.farmers.TreeFarmer;
import crazypants.enderio.base.farming.registry.Registry.Callback;
import crazypants.enderio.util.Prep;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.EventPriority;

public class Commune implements IFarmerJoe {

  public static final @Nonnull Commune instance = new Commune();

  static @Nonnull NNList<ItemStack> disableTrees = new NNList<ItemStack>();

  private Commune() {
  }

  @Override
  public boolean canHarvest(@Nonnull IFarmer farm, @Nonnull BlockPos bc, @Nonnull IBlockState state) {
    return Registry.foreach(new Callback<Boolean>() {
      @Override
      public Boolean run(@Nonnull IFarmerJoe joe) {
        return joe.canHarvest(farm, bc, state) ? Boolean.TRUE : null;
      }
    }) != null;
  }

  @Override
  public IHarvestResult harvestBlock(@Nonnull IFarmer farm, @Nonnull BlockPos bc, @Nonnull IBlockState state) {
    return Registry.foreach(new Callback<IHarvestResult>() {
      @Override
      public IHarvestResult run(@Nonnull IFarmerJoe joe) {
        return !ignoreTreeHarvest(farm, bc, joe) && joe.canHarvest(farm, bc, state) ? joe.harvestBlock(farm, bc, state) : null;
      }
    });
  }

  @Override
  @Deprecated
  public boolean prepareBlock(@Nonnull IFarmer farm, @Nonnull BlockPos bc, @Nonnull IBlockState state) {
    return Registry.foreach(new Callback<Boolean>() {
      @Override
      public Boolean run(@Nonnull IFarmerJoe joe) {
        return joe.prepareBlock(farm, bc, state) ? Boolean.TRUE : null;
      }
    }) != null;
  }

  @Override
  public Result tryPrepareBlock(@Nonnull IFarmer farm, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
    final Result result = Registry.foreach(new Callback<Result>() {
      @Override
      public Result run(@Nonnull IFarmerJoe joe) {
        final Result result2 = joe.tryPrepareBlock(farm, pos, state);
        return result2 != Result.NEXT ? result2 : null;
      }
    });
    return result != null ? result : Result.NEXT;
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
      if (disableTreeStack.isItemEqual(stack)) {
        return true;
      }
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
  public Class<IFarmerJoe> getRegistryType() {
    return null;
  }

  @Override
  @Nonnull
  public EventPriority getPriority() {
    return EventPriority.HIGHEST;
  }

}
