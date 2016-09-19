package crazypants.enderio.integration.jei;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.EnderIO;
import crazypants.enderio.Log;
import crazypants.enderio.item.darksteel.DarkSteelRecipeManager;
import crazypants.enderio.machine.tank.GuiTank;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry.ISubtypeInterpreter;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.BlankRecipeCategory;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class TankRecipeCategory extends BlankRecipeCategory<TankRecipeCategory.TankRecipeWrapper> {

  public static final @Nonnull String UID = "EIOTank";

  // ------------ Recipes

  public static class TankRecipeWrapper extends BlankRecipeWrapper {

    private final FluidStack fluidInput, fluidOutput;
    private final ItemStack itemInput, itemOutput;

    public TankRecipeWrapper(FluidStack fluidInput, FluidStack fluidOutput, ItemStack itemInput, ItemStack itemOutput) {
      this.fluidInput = fluidInput;
      this.fluidOutput = fluidOutput;
      this.itemInput = itemInput;
      this.itemOutput = itemOutput;
    }

    @Override
    public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
    }

    public void setInfoData(Map<Integer, ? extends IGuiIngredient<ItemStack>> ings) {
    }

    @SuppressWarnings("null")
    @Override
    public @Nonnull List<?> getInputs() {
      if (itemInput != null) {
        return Collections.singletonList(itemInput);
      }
      return Collections.emptyList();
    }

    @SuppressWarnings("null")
    @Override
    public @Nonnull List<FluidStack> getFluidInputs() {
      if (fluidInput != null) {
        return Collections.singletonList(fluidInput);
      }
      return Collections.emptyList();
    }

    @SuppressWarnings("null")
    @Override
    public @Nonnull List<?> getOutputs() {
      if (itemOutput != null) {
        return Collections.singletonList(itemOutput);
      }
      return Collections.emptyList();
    }

    @SuppressWarnings("null")
    @Override
    public @Nonnull List<FluidStack> getFluidOutputs() {
      if (fluidOutput != null) {
        return Collections.singletonList(fluidOutput);
      }
      return Collections.emptyList();
    }


  } // -------------------------------------

  public static void register(IModRegistry registry, IGuiHelper guiHelper) {

    registry.addRecipeCategories(new TankRecipeCategory(guiHelper));
    registry.addRecipeHandlers(new BaseRecipeHandler<TankRecipeWrapper>(TankRecipeWrapper.class, TankRecipeCategory.UID));
    registry.addRecipeCategoryCraftingItem(new ItemStack(EnderIO.blockTank, 1, 0), TankRecipeCategory.UID);
    registry.addRecipeCategoryCraftingItem(new ItemStack(EnderIO.blockTank, 1, 1), TankRecipeCategory.UID);
    registry.addRecipeClickArea(GuiTank.class, 155, 42, 16, 16, TankRecipeCategory.UID);

    long start = System.nanoTime();

    Map<String, Fluid> fluids = FluidRegistry.getRegisteredFluids();

    List<TankRecipeWrapper> result = new ArrayList<TankRecipeWrapper>();
    for (ItemStack stack : ItemHelper.getValidItems()) {
      ItemStack drainedStack = stack.copy();
      IFluidHandler fluidHandler = FluidUtil.getFluidHandler(drainedStack);
      if (fluidHandler != null) {
        FluidStack drain = fluidHandler.drain(16000, true);
        // Log.debug("Draining a " + stack + " gives " + fluidString(drain) + " and " + drainedStack);
        if (drain != null && drain.amount > 0) {
          // filled container
          if (drainedStack.stackSize <= 0) {
            drainedStack = null;
          }
          result.add(new TankRecipeWrapper(null, drain, stack.copy(), drainedStack));
        } else {
          // empty container
          for (Fluid fluid : fluids.values()) {
            ItemStack filledStack = stack.copy();
            fluidHandler = FluidUtil.getFluidHandler(filledStack);
            if (fluidHandler != null) {
              int filled = fluidHandler.fill(new FluidStack(fluid, 16000), true);
              if (filled > 0) {
                // Log.debug("Filling a " + stack + " with " + fluidString(new FluidStack(fluid, filled)) + " gives " + filledStack);
                result.add(new TankRecipeWrapper(new FluidStack(fluid, filled), null, stack.copy(), filledStack));
              }
            }
          }
        }
      }
    }

    long end = System.nanoTime();
    registry.addRecipes(result);

    // registry.getRecipeTransferRegistry().addRecipeTransferHandler(ContainerTank.class, TankRecipeCategory.UID, 0, 2, 3, 4 * 9);

    Log.info(String.format("TankRecipeCategory: Added %d tank recipes to JEI in %.3f seconds.", result.size(),
        (end - start) / 1000000000d));
  }

  private static String fluidString(FluidStack stack) {
    return stack == null ? "nothing" : stack.amount + "x" + stack.getUnlocalizedName();
  }

  // ------------ Category

  // Offsets from full size gui, makes it much easier to get the location
  // correct
  private int xOff = 15;
  private int yOff = 20;

  @Nonnull
  private final IDrawable background;

  public TankRecipeCategory(IGuiHelper guiHelper) {
    ResourceLocation backgroundLocation = EnderIO.proxy.getGuiTexture("tank");
    background = guiHelper.createDrawable(backgroundLocation, xOff, yOff, 146, 49);
  }

  @Override
  public @Nonnull String getUid() {
    return UID;
  }

  @SuppressWarnings("null")
  @Override
  public @Nonnull String getTitle() {
    return EnderIO.blockTank.getLocalizedName();
  }

  @Override
  public @Nonnull IDrawable getBackground() {
    return background;
  }

  @SuppressWarnings("null")
  @Override
  public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull TankRecipeWrapper recipeWrapper) {
    IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
    IGuiFluidStackGroup fluidStacks = recipeLayout.getFluidStacks();

    guiItemStacks.init(0, true, 44 - xOff - 1, 21 - yOff - 1);
    guiItemStacks.init(1, true, 116 - xOff - 1, 21 - yOff - 1);
    guiItemStacks.init(2, false, 44 - xOff - 1, 52 - yOff - 1);
    guiItemStacks.init(3, false, 116 - xOff - 1, 52 - yOff - 1);

    fluidStacks.init(0, false, 80 - xOff, 21 - yOff, 16, 47, 16000, true, null);

    if (recipeWrapper.fluidInput == null) {
      guiItemStacks.setFromRecipe(0, recipeWrapper.itemInput);
      guiItemStacks.setFromRecipe(2, recipeWrapper.itemOutput);
      fluidStacks.set(0, recipeWrapper.fluidOutput);
    } else {
      guiItemStacks.setFromRecipe(1, recipeWrapper.itemInput);
      guiItemStacks.setFromRecipe(3, recipeWrapper.itemOutput);
      fluidStacks.set(0, recipeWrapper.fluidInput);
    }
  }

  public static class DarkSteelUpgradeSubtypeInterpreter implements ISubtypeInterpreter {

    @Override
    @Nullable
    public String getSubtypeInfo(@Nonnull ItemStack itemStack) {
      return DarkSteelRecipeManager.instance.getUpgradesAsString(itemStack);
    }

  }

}
