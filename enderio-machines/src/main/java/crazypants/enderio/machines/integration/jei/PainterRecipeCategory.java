package crazypants.enderio.machines.integration.jei;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.integration.jei.energy.EnergyIngredient;
import crazypants.enderio.base.integration.jei.energy.EnergyIngredientRenderer;
import crazypants.enderio.base.recipe.painter.AbstractPainterTemplate;
import crazypants.enderio.machines.EnderIOMachines;
import crazypants.enderio.machines.machine.painter.ContainerPainter;
import crazypants.enderio.machines.machine.painter.GuiPainter;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiIngredientGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import static crazypants.enderio.machines.init.MachineObject.block_painter;
import static crazypants.enderio.machines.machine.painter.ContainerPainter.FIRST_INVENTORY_SLOT;
import static crazypants.enderio.machines.machine.painter.ContainerPainter.FIRST_RECIPE_SLOT;
import static crazypants.enderio.machines.machine.painter.ContainerPainter.NUM_INVENTORY_SLOT;
import static crazypants.enderio.machines.machine.painter.ContainerPainter.NUM_RECIPE_SLOT;

public class PainterRecipeCategory implements IRecipeCategory<PainterRecipeCategory.PainterRecipeWrapper> {

  public static final @Nonnull String UID = "Painter";

  // ------------ Recipes

  public static class PainterRecipeWrapper implements IRecipeWrapper {

    final AbstractPainterTemplate<?> recipe;
    final @Nonnull ItemStack target;
    final @Nonnull List<ItemStack> paints;
    final @Nonnull List<ItemStack> results;

    public PainterRecipeWrapper(@Nonnull AbstractPainterTemplate<?> recipe, @Nonnull ItemStack target, @Nonnull List<ItemStack> paints,
        @Nonnull List<ItemStack> results) {
      this.recipe = recipe;
      this.target = target;
      this.paints = paints;
      this.results = results;
    }

    public PainterRecipeWrapper(@Nonnull AbstractPainterTemplate<?> recipe, @Nonnull ItemStack target, @Nonnull ItemStack paint, @Nonnull ItemStack result) {
      this.recipe = recipe;
      this.target = target;
      this.paints = Collections.singletonList(paint);
      this.results = Collections.singletonList(result);
    }

    @Override
    public void getIngredients(@Nonnull IIngredients ingredients) {
      List<List<ItemStack>> list = new ArrayList<>();
      list.add(Collections.singletonList(target));
      list.add(paints);
      ingredients.setInputLists(ItemStack.class, list);
      ingredients.setOutputs(ItemStack.class, results);
      ingredients.setInput(EnergyIngredient.class, new EnergyIngredient(recipe.getEnergyRequired(NNList.emptyList())));
    }
  }

  public static void register(IModRegistry registry) {
    registry.addRecipeClickArea(GuiPainter.class, 155, 42, 16, 16, PainterRecipeCategory.UID);
    registry.addRecipeCatalyst(new ItemStack(block_painter.getBlockNN()), PainterRecipeCategory.UID);

    registry.addRecipeRegistryPlugin(new PainterRegistryPlugin(registry));

    registry.getRecipeTransferRegistry().addRecipeTransferHandler(ContainerPainter.class, PainterRecipeCategory.UID, FIRST_RECIPE_SLOT, NUM_RECIPE_SLOT,
        FIRST_INVENTORY_SLOT, NUM_INVENTORY_SLOT);
  }

  // ------------ Category

  // Offsets from full size gui, makes it much easier to get the location correct
  private final static int xOff = 34;
  private final static int yOff = 28;

  private final @Nonnull IDrawable background;
  private final @Nonnull IDrawableAnimated arrow;

  public PainterRecipeCategory(IJeiHelpers jeiHelpers) {
    IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

    ResourceLocation backgroundLocation = EnderIO.proxy.getGuiTexture("painter");
    background = guiHelper.createDrawable(backgroundLocation, xOff, yOff, 120, 50);

    IDrawableStatic flameDrawable = guiHelper.createDrawable(backgroundLocation, 176, 14, 24, 16);
    arrow = guiHelper.createAnimatedDrawable(flameDrawable, 200, IDrawableAnimated.StartDirection.LEFT, false);
  }

  @Override
  public @Nonnull String getUid() {
    return UID;
  }

  @Override
  public @Nonnull String getTitle() {
    return block_painter.getBlockNN().getLocalizedName();
  }

  @Override
  public @Nonnull IDrawable getBackground() {
    return background;
  }

  @Override
  public void drawExtras(@Nonnull Minecraft minecraft) {
    arrow.draw(minecraft, 88 - xOff, 34 - yOff);
  }

  @Override
  public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull PainterRecipeCategory.PainterRecipeWrapper currentRecipe,
      @Nonnull IIngredients ingredients) {
    IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
    IGuiIngredientGroup<EnergyIngredient> group = recipeLayout.getIngredientsGroup(EnergyIngredient.class);

    guiItemStacks.init(0, true, 67 - xOff - 1, 34 - yOff - 1);
    guiItemStacks.init(1, true, 38 - xOff - 1, 34 - yOff - 1);
    guiItemStacks.init(2, false, 121 - xOff - 1, 34 - yOff - 1);
    group.init(3, true, EnergyIngredientRenderer.INSTANCE, 108 - xOff, 60 - yOff, 50, 10, 0, 0);

    guiItemStacks.set(0, currentRecipe.target);
    guiItemStacks.set(1, currentRecipe.paints);
    guiItemStacks.set(2, currentRecipe.results);
    group.set(ingredients);
  }

  @Override
  public @Nonnull String getModName() {
    return EnderIOMachines.MODID;
  }

}
