package crazypants.enderio.machines.integration.jei;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.fluid.Fluids;
import crazypants.enderio.base.integration.jei.RecipeWrapperBase;
import crazypants.enderio.base.integration.jei.RecipeWrapperIMachineRecipe;
import crazypants.enderio.base.recipe.IMachineRecipe;
import crazypants.enderio.base.recipe.MachineRecipeRegistry;
import crazypants.enderio.base.recipe.RecipeLevel;
import crazypants.enderio.base.recipe.tank.TankMachineRecipe;
import crazypants.enderio.base.xp.XpUtil;
import crazypants.enderio.machines.EnderIOMachines;
import crazypants.enderio.machines.config.config.PersonalConfig;
import crazypants.enderio.machines.config.config.TankConfig;
import crazypants.enderio.machines.lang.Lang;
import crazypants.enderio.machines.machine.tank.ContainerTank;
import crazypants.enderio.machines.machine.tank.GuiTank;
import crazypants.enderio.machines.machine.tank.TileTank;
import crazypants.enderio.util.Prep;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ITooltipCallback;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeCategory;
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

  public static abstract class TankRecipeWrapper<E extends IMachineRecipe> extends RecipeWrapperIMachineRecipe<E> {

    public TankRecipeWrapper(E recipe) {
      super(recipe);
    }

    public abstract String getUUID();

  }

  public static class TankRecipeWrapperSimple extends TankRecipeWrapper<IMachineRecipe> {

    private final FluidStack fluidInput, fluidOutput;
    private final ItemStack itemInput, itemOutput;

    public TankRecipeWrapperSimple(FluidStack fluidInput, FluidStack fluidOutput, ItemStack itemInput, ItemStack itemOutput) {
      super(null);
      this.fluidInput = fluidInput;
      this.fluidOutput = fluidOutput;
      this.itemInput = itemInput;
      this.itemOutput = itemOutput;
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

    @Override
    public String getUUID() {
      return null;
    }

    @Override
    protected RecipeLevel getRecipeLevel() {
      return RecipeLevel.IGNORE;
    }
  }

  public static class TankRecipeWrapperRecipe extends TankRecipeWrapper<TankMachineRecipe> {

    public TankRecipeWrapperRecipe(@Nonnull TankMachineRecipe recipe) {
      super(recipe);
    }

    @Override
    public void getIngredients(@Nonnull IIngredients ingredients) {
      ingredients.setInput(ItemStack.class, recipe.getInput().getItemStacksRaw());
      FluidStack fluid = recipe.getFluid();
      NNList<FluidStack> fluids = new NNList<>();
      if (recipe.getLogic() == TankMachineRecipe.Logic.XPBOTTLE) {
        // 3 + rand.nextInt(5) + rand.nextInt(5)
        for (int i = 3; i <= 11; i++) {
          FluidStack copy = fluid.copy();
          copy.amount *= XpUtil.experienceToLiquid(i);
          fluids.add(copy);
        }
      } else {
        fluids.add(recipe.getLogic().convertFluidResult(recipe.isFilling(), recipe.getInput().getItemStack(), fluid, fluid, recipe.getOutput().getItemStack()));
      }
      if (recipe.isFilling()) {
        ingredients.setInputLists(FluidStack.class, new NNList<List<FluidStack>>(fluids));
      } else {
        ingredients.setOutputLists(FluidStack.class, new NNList<List<FluidStack>>(fluids));
      }
      ingredients.setOutput(ItemStack.class,
          recipe.getLogic().convertItemResult(recipe.isFilling(), recipe.getInput().getItemStack(), fluid, fluid, recipe.getOutput().getItemStack()));
    }

    @Override
    public String getUUID() {
      return recipe.getRecipeName();
    }

  }

  // -------------------------------------

  public static void register() {
    // If all tank recipes are disabled, don't register the plugin
    if (!PersonalConfig.enableTankFluidInOutJEIRecipes.get() && !PersonalConfig.enableTankMendingJEIRecipes.get()) {
      return;
    }

    RecipeWrapperBase.setLevelData(TankRecipeWrapperSimple.class, MachinesPlugin.iGuiHelper, 140 - xOff, 40 - yOff - 5, "textures/blocks/block_tank.png",
        "textures/blocks/block_tank.png");
    RecipeWrapperBase.setLevelData(TankRecipeWrapperRecipe.class, MachinesPlugin.iGuiHelper, 140 - xOff, 40 - yOff - 5, "textures/blocks/block_tank.png",
        "textures/blocks/block_tank.png");

    MachinesPlugin.iModRegistry.addRecipeCategories(new TankRecipeCategory(MachinesPlugin.iGuiHelper));
    MachinesPlugin.iModRegistry.addRecipeCategoryCraftingItem(new ItemStack(block_tank.getBlockNN(), 1, 0), TankRecipeCategory.UID);
    MachinesPlugin.iModRegistry.addRecipeCategoryCraftingItem(new ItemStack(block_tank.getBlockNN(), 1, 1), TankRecipeCategory.UID);
    MachinesPlugin.iModRegistry.addRecipeClickArea(GuiTank.class, 155, 42, 16, 16, TankRecipeCategory.UID);

    long start = System.nanoTime();

    List<ItemStack> validItems = MachinesPlugin.iModRegistry.getIngredientRegistry().getIngredients(ItemStack.class);

    List<TankRecipeWrapper<?>> result = new ArrayList<>();

    for (IMachineRecipe recipe : MachineRecipeRegistry.instance.getRecipesForMachine(MachineRecipeRegistry.TANK_EMPTYING).values()) {
      if (recipe instanceof TankMachineRecipe) {
        result.add(new TankRecipeWrapperRecipe((TankMachineRecipe) recipe));
      }
    }

    for (IMachineRecipe recipe : MachineRecipeRegistry.instance.getRecipesForMachine(MachineRecipeRegistry.TANK_FILLING).values()) {
      if (recipe instanceof TankMachineRecipe) {
        result.add(new TankRecipeWrapperRecipe((TankMachineRecipe) recipe));
      }
    }

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
            result.add(new TankRecipeWrapperSimple(null, drain, stack.copy(), drainedStack));
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
                  result.add(new TankRecipeWrapperSimple(new FluidStack(fluid, filled), null, stack.copy(), filledStack));
                }
              }
            }
          }
        }
      }
    }

    if (TankConfig.allowMending.get() && PersonalConfig.enableTankMendingJEIRecipes.get()) {
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

          if (damagedStack.getItemDamage() != enchantedStack.getItemDamage() && Prep.isValid(damagedStack) && Prep.isValid(enchantedStack)) {
            result
                .add(new TankRecipeWrapperSimple(new FluidStack(Fluids.XP_JUICE.getFluid(), XpUtil.experienceToLiquid(TileTank.durabilityToXp(damageMendable))),
                    null, damagedStack, enchantedStack));
          }
        }
      }
    }

    long end = System.nanoTime();
    MachinesPlugin.iModRegistry.addRecipes(result, UID);

    // TODO: Create transfer handler that knows about liquids
    MachinesPlugin.iModRegistry.getRecipeTransferRegistry().addRecipeTransferHandler(ContainerTank.class, TankRecipeCategory.UID, 0, 2, 3, 4 * 9);

    Log.info(String.format("TankRecipeCategory: Added %d tank recipes to JEI in %.3f seconds.", result.size(), (end - start) / 1000000000d));
  }

  @SuppressWarnings("unused")
  private static String fluidString(FluidStack stack) {
    return stack == null ? "nothing" : stack.amount + "x" + stack.getUnlocalizedName();
  }

  // ------------ Category

  // Offsets from full size gui, makes it much easier to get the location
  // correct
  private static int xOff = 15;
  private static int yOff = 20;

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
    List<List<FluidStack>> fluidOutputs = ingredients.getOutputs(FluidStack.class);
    List<ItemStack> inputIngredient = itemInputs.isEmpty() ? null : itemInputs.get(0);
    List<ItemStack> outputIngredient = itemOutputs.isEmpty() ? null : itemOutputs.get(0);
    if (fluidInputs.isEmpty()) {
      guiItemStacks.set(0, inputIngredient);
      guiItemStacks.set(2, outputIngredient);
      fluidStacks.set(0, fluidOutputs.get(0));
    } else {
      guiItemStacks.set(1, inputIngredient);
      guiItemStacks.set(3, outputIngredient);
      fluidStacks.set(0, fluidInputs.get(0));
    }

    fluidStacks.addTooltipCallback(new ITooltipCallback<FluidStack>() {
      @Override
      public void onTooltip(int slotIndex, boolean input, FluidStack ingredient, List<String> tooltip) {
        final String uuid = recipeWrapper.getUUID();
        if (uuid != null) {
          tooltip.add(Lang.JEI_RECIPE.get(uuid));
        }
      }
    });
  }

  @Override
  public @Nonnull String getModName() {
    return EnderIOMachines.MODID;
  }

}
