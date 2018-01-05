package crazypants.enderio.machines.integration.jei;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.integration.jei.RecipeWrapper;
import crazypants.enderio.base.integration.jei.energy.EnergyIngredient;
import crazypants.enderio.base.integration.jei.energy.EnergyIngredientRenderer;
import crazypants.enderio.base.recipe.IManyToOneRecipe;
import crazypants.enderio.base.recipe.IRecipe;
import crazypants.enderio.base.recipe.alloysmelter.AlloyRecipeManager;
import crazypants.enderio.machines.machine.alloy.ContainerAlloySmelter;
import crazypants.enderio.machines.machine.alloy.GuiAlloySmelter;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiIngredientGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeCategory;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import static crazypants.enderio.machines.init.MachineObject.block_alloy_smelter;
import static crazypants.enderio.machines.init.MachineObject.block_simple_alloy_smelter;
import static crazypants.enderio.machines.machine.alloy.ContainerAlloySmelter.FIRST_INVENTORY_SLOT;
import static crazypants.enderio.machines.machine.alloy.ContainerAlloySmelter.FIRST_RECIPE_SLOT;
import static crazypants.enderio.machines.machine.alloy.ContainerAlloySmelter.NUM_INVENTORY_SLOT;
import static crazypants.enderio.machines.machine.alloy.ContainerAlloySmelter.NUM_RECIPE_SLOT;

public class AlloyRecipeCategory extends BlankRecipeCategory<AlloyRecipeCategory.AlloyRecipe> {

  public static final @Nonnull String UID = "AlloySmelter";

  // ------------ Recipes

  public static class AlloyRecipe extends RecipeWrapper {
    public AlloyRecipe(IRecipe recipe) {
      super(recipe);
    }

    @Override
    public void getIngredients(@Nonnull IIngredients ingredients) {
      super.getIngredients(ingredients);
      ingredients.setInput(EnergyIngredient.class, new EnergyIngredient(recipe.getEnergyRequired()));
    }
  }

  public static void register(IModRegistry registry, IGuiHelper guiHelper) {

    registry.addRecipeCategories(new AlloyRecipeCategory(guiHelper));
    registry.handleRecipes(IRecipe.class, AlloyRecipe::new, AlloyRecipeCategory.UID);
    registry.addRecipeClickArea(GuiAlloySmelter.class, 155, 42, 16, 16, AlloyRecipeCategory.UID);
    registry.addRecipeCategoryCraftingItem(new ItemStack(block_alloy_smelter.getBlockNN()), AlloyRecipeCategory.UID, VanillaRecipeCategoryUid.SMELTING);
    registry.addRecipeCategoryCraftingItem(new ItemStack(block_simple_alloy_smelter.getBlockNN()), AlloyRecipeCategory.UID);

    long start = System.nanoTime();

    List<IRecipe> result = new ArrayList<IRecipe>();
    for (IManyToOneRecipe rec : AlloyRecipeManager.getInstance().getRecipes()) {
      if (!rec.isSynthetic()) {
        result.add(rec);
      }
    }
    result.addAll(AlloyRecipeManager.getInstance().getVanillaRecipe().getAllRecipes());

    long end = System.nanoTime();
    registry.addRecipes(result, UID);

    registry.getRecipeTransferRegistry().addRecipeTransferHandler(ContainerAlloySmelter.Normal.class, AlloyRecipeCategory.UID, FIRST_RECIPE_SLOT,
        NUM_RECIPE_SLOT, FIRST_INVENTORY_SLOT, NUM_INVENTORY_SLOT);
    registry.getRecipeTransferRegistry().addRecipeTransferHandler(ContainerAlloySmelter.Simple.class, AlloyRecipeCategory.UID, FIRST_RECIPE_SLOT,
        NUM_RECIPE_SLOT, FIRST_INVENTORY_SLOT - 1, NUM_INVENTORY_SLOT);
    registry.getRecipeTransferRegistry().addRecipeTransferHandler(ContainerAlloySmelter.Normal.class, VanillaRecipeCategoryUid.SMELTING, FIRST_RECIPE_SLOT,
        NUM_RECIPE_SLOT, FIRST_INVENTORY_SLOT, NUM_INVENTORY_SLOT);

    Log.info(String.format("AlloyRecipeCategory: Added %d alloy smelter recipes to JEI in %.3f seconds.", result.size(), (end - start) / 1000000000d));
  }

  // ------------ Category

  // Offsets from full size gui, makes it much easier to get the location correct
  private int xOff = 45;
  private int yOff = 3;
  private final @Nonnull IDrawable background;
  private final @Nonnull IDrawableAnimated flame;

  public AlloyRecipeCategory(IGuiHelper guiHelper) {
    ResourceLocation backgroundLocation = EnderIO.proxy.getGuiTexture("alloy_smelter");
    background = guiHelper.createDrawable(backgroundLocation, xOff, yOff, 82, 78);

    IDrawableStatic flameDrawable = guiHelper.createDrawable(backgroundLocation, 176, 0, 13, 13);
    flame = guiHelper.createAnimatedDrawable(flameDrawable, 200, IDrawableAnimated.StartDirection.BOTTOM, false);
  }

  @Override
  public @Nonnull String getUid() {
    return UID;
  }

  @Override
  public @Nonnull String getTitle() {
    return block_alloy_smelter.getBlockNN().getLocalizedName();
  }

  @Override
  public @Nonnull IDrawable getBackground() {
    return background;
  }

  @Override
  public void drawExtras(@Nonnull Minecraft minecraft) {
    flame.draw(minecraft, 56 - xOff, 36 - yOff);
    flame.draw(minecraft, 103 - xOff, 36 - yOff);
  }

  @Override
  public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull AlloyRecipeCategory.AlloyRecipe recipeWrapper, @Nonnull IIngredients ingredients) {
    IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
    IGuiIngredientGroup<EnergyIngredient> group = recipeLayout.getIngredientsGroup(EnergyIngredient.class);

    guiItemStacks.init(0, true, 53 - xOff, 16 - yOff);
    guiItemStacks.init(1, true, 78 - xOff, 6 - yOff);
    guiItemStacks.init(2, true, 102 - xOff, 16 - yOff);
    guiItemStacks.init(3, false, 78 - xOff, 57 - yOff);
    group.init(4, true, EnergyIngredientRenderer.INSTANCE, 108 - xOff, 55 - yOff, 50, 10, 0, 0);

    List<List<ItemStack>> inputs = ingredients.getInputs(ItemStack.class);
    for (int index = 0; index < inputs.size(); index++) {
      List<ItemStack> input = inputs.get(index);
      if (input != null) {
        guiItemStacks.set(index, input);
      }
    }
    List<List<ItemStack>> outputs = ingredients.getOutputs(ItemStack.class);
    guiItemStacks.set(3, outputs.get(0));

    group.set(ingredients);
  }
}
