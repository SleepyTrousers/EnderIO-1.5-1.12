package crazypants.enderio.material;

import org.apache.commons.lang3.ArrayUtils;

import com.enderio.core.api.common.util.ITankAccess;
import com.enderio.core.common.util.FluidUtil;
import com.enderio.core.common.util.FluidUtil.FluidAndStackResult;

import crazypants.enderio.EnderIO;
import crazypants.enderio.fluid.Fluids;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.oredict.OreDictionary;

public class NutritiousStickRecipe implements IRecipe {

  @Override
  public boolean matches(InventoryCrafting inv, World worldIn) {

    boolean foundStick = false;
    boolean foundFluid = false;
    for (int i = 0; i < inv.getSizeInventory() && (!foundStick || !foundFluid); i++) {
      ItemStack stack = inv.getStackInSlot(i);
      if (stack != null) {
        if (!foundStick) {
          foundStick = isStick(stack);
        }
        if (!foundFluid) {
          FluidAndStackResult fill = FluidUtil.tryDrainContainer(stack, new NutDistTank());
          if (fill.result.fluidStack != null && fill.result.fluidStack.amount >= Fluid.BUCKET_VOLUME) {
            foundFluid = true;
          }
        }

      }
    }
    return foundStick && foundFluid;
  }

  private boolean isStick(ItemStack stack) {
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
  public ItemStack getCraftingResult(InventoryCrafting inv) {
    return new ItemStack(EnderIO.itemMaterial, 1, Material.NUTRITIOUS_STICK.ordinal());
  }

  @Override
  public int getRecipeSize() {
    return 2;
  }

  @Override
  public ItemStack getRecipeOutput() {
    return new ItemStack(EnderIO.itemMaterial, 1, Material.NUTRITIOUS_STICK.ordinal());
  }

  @Override
  public ItemStack[] getRemainingItems(InventoryCrafting inv) {
    ItemStack[] result = new ItemStack[inv.getSizeInventory()];
    for (int slot = 0; slot < inv.getSizeInventory(); slot++) {
      result[slot] = getResult(inv.getStackInSlot(slot));
    }
    return result;
  }

  private ItemStack getResult(ItemStack in) {
    if (in != null) {
      FluidAndStackResult fill = FluidUtil.tryDrainContainer(in, new NutDistTank());
      if (fill.result.fluidStack != null && fill.result.fluidStack.amount >= Fluid.BUCKET_VOLUME) {
        return fill.result.itemStack;
      }
    }
    return null;
  }

  private static class NutDistTank implements ITankAccess {

    private FluidTank inputTank = new FluidTank(Fluids.fluidNutrientDistillation, 0, Fluid.BUCKET_VOLUME);

    @Override
    public FluidTank getInputTank(FluidStack forFluidType) {
      if (forFluidType == null || com.enderio.core.common.util.FluidUtil.areFluidsTheSame(Fluids.fluidNutrientDistillation, forFluidType.getFluid())) {
        return inputTank;
      }
      return null;
    }

    @Override
    public FluidTank[] getOutputTanks() {
      return new FluidTank[0];
    }

    @Override
    public void setTanksDirty() {
    }

  }
}