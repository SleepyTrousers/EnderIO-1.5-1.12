package crazypants.enderio.base.material.material;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.ArrayUtils;

import com.enderio.core.api.common.util.ITankAccess;
import com.enderio.core.common.util.FluidUtil;
import com.enderio.core.common.util.FluidUtil.FluidAndStackResult;

import crazypants.enderio.base.fluid.Fluids;
import crazypants.enderio.util.Prep;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class NutritiousStickRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

  @Override
  public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World worldIn) {

    boolean foundStick = false;
    boolean foundFluid = false;
    for (int i = 0; i < inv.getSizeInventory() && (!foundStick || !foundFluid); i++) {
      ItemStack stack = inv.getStackInSlot(i);
      if (Prep.isValid(stack)) {
        foundStick |= isStick(stack);
        if (!foundFluid) {
          final FluidStack fluidStack = FluidUtil.tryDrainContainer(stack, new NutDistTank()).result.fluidStack;
          if (fluidStack != null && fluidStack.amount >= Fluid.BUCKET_VOLUME) {
            foundFluid = true;
          }
        }

      }
    }
    return foundStick && foundFluid;
  }

  private boolean isStick(@Nonnull ItemStack stack) {
    int oreId = OreDictionary.getOreID("stickWood");
    int[] ids = OreDictionary.getOreIDs(stack);
    if (ids != null) {
      if (ArrayUtils.contains(ids, oreId)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public @Nonnull ItemStack getCraftingResult(@Nonnull InventoryCrafting inv) {
    return Material.NUTRITIOUS_STICK.getStack();
  }

  @Override
  public @Nonnull ItemStack getRecipeOutput() {
    return Material.NUTRITIOUS_STICK.getStack();
  }

  @Override
  public @Nonnull NonNullList<ItemStack> getRemainingItems(@Nonnull InventoryCrafting inv) {
    NonNullList<ItemStack> nonnulllist = NonNullList.<ItemStack> withSize(inv.getSizeInventory(), ItemStack.EMPTY);
    for (int i = 0; i < nonnulllist.size(); ++i) {
      nonnulllist.set(i, getResult(inv.getStackInSlot(i)));
    }
    return nonnulllist;
  }

  private @Nonnull ItemStack getResult(@Nonnull ItemStack in) {
    if (Prep.isValid(in)) {
      FluidAndStackResult fill = FluidUtil.tryDrainContainer(in, new NutDistTank());
      return fill.result.itemStack;
    }
    return Prep.getEmpty();
  }

  private static class NutDistTank implements ITankAccess {

    private FluidTank inputTank = new FluidTank(Fluids.ENDER_DISTILLATION.getFluid(), 0, Fluid.BUCKET_VOLUME);

    @Override
    public FluidTank getInputTank(FluidStack forFluidType) {
      if (forFluidType == null || FluidUtil.areFluidsTheSame(Fluids.ENDER_DISTILLATION.getFluid(), forFluidType.getFluid())) {
        return inputTank;
      }
      return null;
    }

    @Override
    public @Nonnull FluidTank[] getOutputTanks() {
      return new FluidTank[0];
    }

    @Override
    public void setTanksDirty() {
    }

  }

  @Override
  public boolean canFit(int width, int height) {
    return width >= 2 && height >= 2;
  }
}