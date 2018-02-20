package crazypants.enderio.base.material.material;

import javax.annotation.Nonnull;

import com.enderio.core.api.common.util.ITankAccess;
import com.enderio.core.common.util.FluidUtil;
import com.enderio.core.common.util.FluidUtil.FluidAndStackResult;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.OreDictionaryHelper;

import crazypants.enderio.base.fluid.Fluids;
import crazypants.enderio.util.Prep;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.crafting.IngredientNBT;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.oredict.OreIngredient;

public class NutritiousStickRecipe extends ShapelessRecipes { // sic! JEI won't work if we don't extend that

  public NutritiousStickRecipe() {
    super("", Prep.getEmpty(), NNList.emptyList());
  }

  private final @Nonnull NNList<Ingredient> dummyIngredients = new NNList<Ingredient>(new OreIngredient("stickWood"),
      new IngredientNBT(Fluids.NUTRIENT_DISTILLATION.getBucket()) {
      });

  @Override
  public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World worldIn) {

    boolean foundStick = false;
    boolean foundFluid = false;
    for (int i = 0; i < inv.getSizeInventory(); i++) {
      ItemStack stack = inv.getStackInSlot(i);
      if (Prep.isValid(stack)) {
        if (!foundStick && isStick(stack)) {
          foundStick = true;
          continue;
        }
        if (!foundFluid) {
          final FluidStack fluidStack = FluidUtil.tryDrainContainer(stack, new NutDistTank()).result.fluidStack;
          if (fluidStack != null && fluidStack.amount >= Fluid.BUCKET_VOLUME) {
            foundFluid = true;
            continue;
          }
        }
        return false;
      }
    }
    return foundStick && foundFluid;
  }

  private boolean isStick(@Nonnull ItemStack stack) {
    return OreDictionaryHelper.hasName(stack, "stickWood");
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
      final FluidStack fluidStack = fill.result.fluidStack;
      if (fluidStack != null && fluidStack.amount >= Fluid.BUCKET_VOLUME) {
        return fill.result.itemStack;
      }
    }
    return ForgeHooks.getContainerItem(in);
  }

  private static class NutDistTank implements ITankAccess {

    private FluidTank inputTank = new FluidTank(Fluids.NUTRIENT_DISTILLATION.getFluid(), 0, Fluid.BUCKET_VOLUME);

    @Override
    public FluidTank getInputTank(FluidStack forFluidType) {
      if (forFluidType == null || FluidUtil.areFluidsTheSame(Fluids.NUTRIENT_DISTILLATION.getFluid(), forFluidType.getFluid())) {
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
    return width * height >= 2;
  }

  @Override
  public @Nonnull NonNullList<Ingredient> getIngredients() {
    return dummyIngredients;
  }

  @Override
  public boolean isDynamic() {
    return true;
  }

}