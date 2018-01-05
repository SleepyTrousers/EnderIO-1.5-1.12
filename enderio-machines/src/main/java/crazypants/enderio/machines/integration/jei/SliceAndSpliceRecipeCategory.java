package crazypants.enderio.machines.integration.jei;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.integration.jei.RecipeWrapper;
import crazypants.enderio.base.integration.jei.energy.EnergyIngredient;
import crazypants.enderio.base.integration.jei.energy.EnergyIngredientRenderer;
import crazypants.enderio.base.recipe.IRecipe;
import crazypants.enderio.base.recipe.slicensplice.SliceAndSpliceRecipeManager;
import crazypants.enderio.machines.machine.slicensplice.ContainerSliceAndSplice;
import crazypants.enderio.machines.machine.slicensplice.GuiSliceAndSplice;
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
import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import static crazypants.enderio.machines.init.MachineObject.block_slice_and_splice;
import static crazypants.enderio.machines.machine.slicensplice.ContainerSliceAndSplice.FIRST_INVENTORY_SLOT;
import static crazypants.enderio.machines.machine.slicensplice.ContainerSliceAndSplice.FIRST_RECIPE_SLOT;
import static crazypants.enderio.machines.machine.slicensplice.ContainerSliceAndSplice.NUM_INVENTORY_SLOT;
import static crazypants.enderio.machines.machine.slicensplice.ContainerSliceAndSplice.NUM_RECIPE_SLOT;

public class SliceAndSpliceRecipeCategory extends BlankRecipeCategory<SliceAndSpliceRecipeCategory.SliceAndSpliceRecipe> {

  public static final @Nonnull String UID = "SliceNSPlice";

  // ------------ Recipes

  public static class SliceAndSpliceRecipe extends RecipeWrapper {
    public SliceAndSpliceRecipe(IRecipe recipe) {
      super(recipe);
    }

    @Override
    public void getIngredients(@Nonnull IIngredients ingredients) {
      super.getIngredients(ingredients);
      ingredients.setInput(EnergyIngredient.class, new EnergyIngredient(recipe.getEnergyRequired()));
    }
  }

  public static void register(IModRegistry registry, IGuiHelper guiHelper) {

    registry.addRecipeCategories(new SliceAndSpliceRecipeCategory(guiHelper));
    registry.handleRecipes(IRecipe.class, SliceAndSpliceRecipe::new, SliceAndSpliceRecipeCategory.UID);
    registry.addRecipeClickArea(GuiSliceAndSplice.class, 155, 42, 16, 16, SliceAndSpliceRecipeCategory.UID);
    registry.addRecipeCategoryCraftingItem(new ItemStack(block_slice_and_splice.getBlockNN()), SliceAndSpliceRecipeCategory.UID);

    registry.addRecipes(SliceAndSpliceRecipeManager.getInstance().getRecipes(), UID);

    registry.getRecipeTransferRegistry().addRecipeTransferHandler(ContainerSliceAndSplice.class, SliceAndSpliceRecipeCategory.UID, FIRST_RECIPE_SLOT,
        NUM_RECIPE_SLOT, FIRST_INVENTORY_SLOT, NUM_INVENTORY_SLOT);
  }

  // ------------ Category

  // Offsets from full size gui, makes it much easier to get the location correct
  private int xOff = 34;
  private int yOff = 10;

  @Nonnull
  private final IDrawable background;

  @Nonnull
  protected final IDrawableAnimated arrow;

  public SliceAndSpliceRecipeCategory(IGuiHelper guiHelper) {
    ResourceLocation backgroundLocation = EnderIO.proxy.getGuiTexture("slice_and_splice");
    background = guiHelper.createDrawable(backgroundLocation, xOff, yOff, 125, 70);

    IDrawableStatic flameDrawable = guiHelper.createDrawable(backgroundLocation, 177, 14, 22, 16);
    arrow = guiHelper.createAnimatedDrawable(flameDrawable, 200, IDrawableAnimated.StartDirection.LEFT, false);
  }

  @Override
  public @Nonnull String getUid() {
    return UID;
  }

  @Override
  public @Nonnull String getTitle() {
    return block_slice_and_splice.getBlockNN().getLocalizedName();
  }

  @Override
  public @Nonnull IDrawable getBackground() {
    return background;
  }

  @Override
  public void drawExtras(@Nonnull Minecraft minecraft) {
    arrow.draw(minecraft, 104 - xOff, 49 - yOff);
  }

  @Override
  public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull SliceAndSpliceRecipeCategory.SliceAndSpliceRecipe recipeWrapper,
      @Nonnull IIngredients ingredients) {

    IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

    guiItemStacks.init(0, false, 53 - xOff, 15 - yOff);
    guiItemStacks.init(1, false, 71 - xOff, 15 - yOff);
    guiItemStacks.init(2, true, 43 - xOff, 39 - yOff);
    guiItemStacks.init(3, true, 61 - xOff, 39 - yOff);
    guiItemStacks.init(4, true, 79 - xOff, 39 - yOff);
    guiItemStacks.init(5, true, 43 - xOff, 57 - yOff);
    guiItemStacks.init(6, true, 61 - xOff, 57 - yOff);
    guiItemStacks.init(7, true, 79 - xOff, 57 - yOff);
    guiItemStacks.init(8, false, 133 - xOff, 48 - yOff);

    guiItemStacks.set(0, getAxes());
    guiItemStacks.set(1, getShears());

    List<List<ItemStack>> inputs = ingredients.getInputs(ItemStack.class);
    int slot = 2;
    for (List<ItemStack> input : inputs) {
      if (input != null) {
        guiItemStacks.set(slot, input);
      }
      ++slot;
    }
    ItemStack output = ingredients.getOutputs(ItemStack.class).get(0).get(0);
    if (!output.isEmpty()) {
      guiItemStacks.set(8, output);
    }

    IGuiIngredientGroup<EnergyIngredient> group = recipeLayout.getIngredientsGroup(EnergyIngredient.class);
    group.init(9, true, EnergyIngredientRenderer.INSTANCE, 108 - xOff - 1, 72 - yOff - 1, 50, 10, 0, 0);
    group.set(ingredients);
  }

  private @Nonnull List<ItemStack> getAxes() {
    List<ItemStack> res = new ArrayList<ItemStack>();
    res.add(new ItemStack(Items.WOODEN_AXE));
    res.add(new ItemStack(Items.IRON_AXE));
    res.add(new ItemStack(Items.GOLDEN_AXE));
    res.add(new ItemStack(Items.DIAMOND_AXE));
    res.add(new ItemStack(ModObject.itemDarkSteelAxe.getItemNN()));
    return res;
  }

  private @Nonnull List<ItemStack> getShears() {
    List<ItemStack> res = new ArrayList<ItemStack>();
    res.add(new ItemStack(Items.SHEARS));
    res.add(new ItemStack(ModObject.itemDarkSteelShears.getItemNN()));
    return res;
  }

}
