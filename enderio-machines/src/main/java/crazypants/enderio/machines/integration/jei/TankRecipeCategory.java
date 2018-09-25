package crazypants.enderio.machines.integration.jei;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.fluid.Fluids;
import crazypants.enderio.base.xp.XpUtil;
import crazypants.enderio.machines.EnderIOMachines;
import crazypants.enderio.machines.config.config.PersonalConfig;
import crazypants.enderio.machines.machine.tank.ContainerTank;
import crazypants.enderio.machines.machine.tank.GuiTank;
import crazypants.enderio.machines.machine.tank.TileTank;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeCategory;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import static crazypants.enderio.machines.init.MachineObject.block_tank;

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

    @Override
    public void getIngredients(@Nonnull IIngredients ingredients) {
      if (itemInput != null) {
        ingredients.setInputs(ItemStack.class, Collections.singletonList(itemInput));
      }
      if (fluidInput != null) {
        ingredients.setInputs(FluidStack.class, Collections.singletonList(fluidInput));
      }
      if (itemOutput != null) {
        ingredients.setOutput(ItemStack.class, itemOutput);
      }
      if (fluidOutput != null) {
        ingredients.setOutput(FluidStack.class, fluidOutput);
      }
    }

  } // -------------------------------------

  public static void register(IModRegistry registry, IGuiHelper guiHelper) {
    // If all tank recipes are disabled, don't register the plugin
    if (!PersonalConfig.enableTankFluidInOutJEIRecipes.get() && !PersonalConfig.enableTankMendingJEIRecipes.get()) {
      return;
    }

    registry.addRecipeCategories(new TankRecipeCategory(guiHelper));
    registry.addRecipeCategoryCraftingItem(new ItemStack(block_tank.getBlockNN(), 1, 0), TankRecipeCategory.UID);
    registry.addRecipeCategoryCraftingItem(new ItemStack(block_tank.getBlockNN(), 1, 1), TankRecipeCategory.UID);
    registry.addRecipeClickArea(GuiTank.class, 155, 42, 16, 16, TankRecipeCategory.UID);

    long start = System.nanoTime();

    List<ItemStack> validItems = registry.getIngredientRegistry().getIngredients(ItemStack.class);

    List<TankRecipeWrapper> result = new ArrayList<TankRecipeWrapper>();

    if (PersonalConfig.enableTankFluidInOutJEIRecipes.get()) {
      Map<String, Fluid> fluids = FluidRegistry.getRegisteredFluids();

      for (ItemStack stack : validItems) {
        ItemStack drainedStack = stack.copy();
        IFluidHandlerItem fluidHandler = FluidUtil.getFluidHandler(drainedStack);
        if (fluidHandler != null) {
          FluidStack drain = fluidHandler.drain(16000, true);
          drainedStack = fluidHandler.getContainer();
          // Log.debug("Draining a " + stack + " gives " + fluidString(drain) + " and " + drainedStack);
          if (drain != null && drain.amount > 0) {
            // filled container
            result.add(new TankRecipeWrapper(null, drain, stack.copy(), drainedStack));
          } else {
            // empty container
            for (Fluid fluid : fluids.values()) {
              ItemStack filledStack = stack.copy();
              fluidHandler = FluidUtil.getFluidHandler(filledStack);
              if (fluidHandler != null) {
                int filled = fluidHandler.fill(new FluidStack(fluid, 16000), true);
                filledStack = fluidHandler.getContainer();
                if (filled > 0) {
                  // Log.debug("Filling a " + stack + " with " + fluidString(new FluidStack(fluid, filled)) + " gives " + filledStack);
                  result.add(new TankRecipeWrapper(new FluidStack(fluid, filled), null, stack.copy(), filledStack));
                }
              }
            }
          }
        }
      }
    }

    if (PersonalConfig.enableTankMendingJEIRecipes.get()) {
      // add mending recipes
      Map<Enchantment, Integer> enchMap = Collections.singletonMap(Enchantments.MENDING, 1);
      final int maxMendable = TileTank.xpToDurability(XpUtil.liquidToExperience(16000));
      for (ItemStack stack : validItems) {
        if (stack.isItemStackDamageable()) {
          ItemStack enchantedStack;
          if (EnchantmentHelper.getEnchantmentLevel(Enchantments.MENDING, stack) > 0) {
            enchantedStack = stack.copy();
          } else if (Enchantments.MENDING.canApply(stack)) {
            enchantedStack = stack.copy();
            EnchantmentHelper.setEnchantments(enchMap, enchantedStack);
          } else {
            continue;
          }

          ItemStack damagedStack = enchantedStack.copy();
          damagedStack.setItemDamage((damagedStack.getMaxDamage() * 3) / 4);
          int damageMendable = Math.min(maxMendable, damagedStack.getItemDamage());
          enchantedStack.setItemDamage(damagedStack.getItemDamage() - damageMendable);

          if (damagedStack.getItemDamage() != enchantedStack.getItemDamage()) {
            result.add(
                new TankRecipeWrapper(new FluidStack(Fluids.XP_JUICE.getFluid(), XpUtil.experienceToLiquid(TileTank.durabilityToXp(damageMendable))), null,
                    damagedStack, enchantedStack));
          }
        }
      }
    }

    long end = System.nanoTime();
    registry.addRecipes(result, UID);

    registry.getRecipeTransferRegistry().addRecipeTransferHandler(ContainerTank.class, TankRecipeCategory.UID, 0, 2, 3, 4 * 9);

    Log.info(String.format("TankRecipeCategory: Added %d tank recipes to JEI in %.3f seconds.", result.size(), (end - start) / 1000000000d));
  }

  @SuppressWarnings("unused")
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
    return block_tank.getBlock().getLocalizedName();
  }

  @Override
  public @Nonnull IDrawable getBackground() {
    return background;
  }

  @SuppressWarnings("null")
  @Override
  public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull TankRecipeWrapper recipeWrapper, @Nonnull IIngredients ingredients) {
    IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
    IGuiFluidStackGroup fluidStacks = recipeLayout.getFluidStacks();

    guiItemStacks.init(0, true, 44 - xOff - 1, 21 - yOff - 1);
    guiItemStacks.init(1, true, 116 - xOff - 1, 21 - yOff - 1);
    guiItemStacks.init(2, false, 44 - xOff - 1, 52 - yOff - 1);
    guiItemStacks.init(3, false, 116 - xOff - 1, 52 - yOff - 1);

    fluidStacks.init(0, false, 80 - xOff, 21 - yOff, 16, 47, 16000, true, null);

    List<List<ItemStack>> itemInputs = ingredients.getInputs(ItemStack.class);
    List<List<ItemStack>> itemOutputs = ingredients.getOutputs(ItemStack.class);
    List<List<FluidStack>> fluidInputs = ingredients.getInputs(FluidStack.class);
    List<ItemStack> inputIngredient = itemInputs.isEmpty() ? null : itemInputs.get(0);
    List<ItemStack> outputIngredient = itemOutputs.isEmpty() ? null : itemOutputs.get(0);
    if (fluidInputs.isEmpty()) {
      guiItemStacks.set(0, inputIngredient);
      guiItemStacks.set(2, outputIngredient);
      fluidStacks.set(0, recipeWrapper.fluidOutput);
    } else {
      guiItemStacks.set(1, inputIngredient);
      guiItemStacks.set(3, outputIngredient);
      fluidStacks.set(0, recipeWrapper.fluidInput);
    }
  }

  @Override
  public @Nonnull String getModName() {
    return EnderIOMachines.MODID;
  }

}
