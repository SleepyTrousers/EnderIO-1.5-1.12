package crazypants.enderio.base.integration.jei;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.tuple.Pair;

import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.config.config.InfinityConfig;
import crazypants.enderio.base.gui.BlockSceneRenderer;
import crazypants.enderio.base.material.material.Material;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeCategory;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import mezz.jei.gui.recipes.RecipeLayout;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class InfinityRecipeCategory extends BlankRecipeCategory<InfinityRecipeCategory.InfinityRecipeWrapper> {

  public static class InfinityRecipeWrapper extends BlankRecipeWrapper {

    @Override
    public void getIngredients(@Nonnull IIngredients ingredients) {
      ingredients.setOutput(ItemStack.class, Material.POWDER_INFINITY.getStack());
    }

  }

  public static void register(IModRegistry registry, IGuiHelper guiHelper) {
    registry.addRecipeCategories(new InfinityRecipeCategory(guiHelper));
    registry.addRecipeCategoryCraftingItem(new ItemStack(Blocks.BEDROCK, 1, 0), InfinityRecipeCategory.UUID);
    registry.addRecipeCategoryCraftingItem(new ItemStack(Items.FLINT_AND_STEEL, 1, 0), InfinityRecipeCategory.UUID);

    long start = System.nanoTime();
    List<InfinityRecipeWrapper> result = new ArrayList<InfinityRecipeWrapper>();
    if (InfinityConfig.infinityCraftingEnabled.get()) {
      result.add(new InfinityRecipeWrapper());
    }
    long end = System.nanoTime();
    registry.addRecipes(result, UUID);

    Log.info(String.format("InfinityRecipeCategory: Added %d infinity recipe(s) to JEI in %.3f seconds.", result.size(), (end - start) / 1000000000d));
  }

  @Nonnull
  private final IDrawable background;
  @Nonnull
  private static final String UUID = "infinityPowder";

  public InfinityRecipeCategory(IGuiHelper guiHelper) {
    ResourceLocation backgroundLocation = EnderIO.proxy.getGuiTexture("infinity");
    background = guiHelper.createDrawable(backgroundLocation, 0, 0, 128, 64);
  }

  @Override
  public @Nonnull String getUid() {
    return UUID;
  }

  @Override
  public @Nonnull String getTitle() {
    return Material.POWDER_INFINITY.getStack().getDisplayName();
  }

  @Override
  public @Nonnull IDrawable getBackground() {
    return background;
  }

  private BlockSceneRenderer bsr;
  private IRecipeLayout recipeLayout; // This only works as long as there's only one recipe in this category!

  @Override
  public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull InfinityRecipeWrapper recipeWrapper, @Nonnull IIngredients ingredients) {
    IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();
    itemStacks.init(0, false, 85, 30);
    itemStacks.set(ingredients);

    this.recipeLayout = recipeLayout;
    bsr = new BlockSceneRenderer(
        new NNList<>(Pair.of(new BlockPos(0, 0, 0), Blocks.BEDROCK.getDefaultState()), Pair.of(new BlockPos(0, 1, 0), Blocks.FIRE.getDefaultState())));
  }

  @Override
  public void drawExtras(@Nonnull Minecraft minecraft) {

    int x = ((RecipeLayout) recipeLayout).getPosX() + 15;
    int y = 20 + ((RecipeLayout) recipeLayout).getPosY();
    int w = 26;
    int h = 50;

    GlStateManager.pushMatrix();
    bsr.drawScreen(x, y, w, h);
    GlStateManager.popMatrix();

    final String text = "<" + (int) (InfinityConfig.infinityDropChance.get() * 100) + "%";
    int stringWidth = minecraft.fontRenderer.getStringWidth(text);
    minecraft.fontRenderer.drawString(text, 59 - stringWidth / 2, 36, 0xFFFFFF, false);
  }

}
