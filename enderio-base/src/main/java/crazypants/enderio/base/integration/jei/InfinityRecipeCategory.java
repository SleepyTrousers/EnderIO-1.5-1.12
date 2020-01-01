package crazypants.enderio.base.integration.jei;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.tuple.Pair;

import com.enderio.core.client.render.ColorUtil;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.Callback;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.config.config.InfinityConfig;
import crazypants.enderio.base.fluid.Fluids;
import crazypants.enderio.base.gui.BlockSceneRenderer;
import crazypants.enderio.base.item.coordselector.TelepadTarget;
import crazypants.enderio.base.lang.Lang;
import crazypants.enderio.base.material.material.Material;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.gui.recipes.RecipeLayout;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class InfinityRecipeCategory implements IRecipeCategory<InfinityRecipeCategory.InfinityRecipeWrapper> {

  public static class InfinityRecipeWrapper implements IRecipeWrapper {

    private final @Nonnull BlockSceneRenderer bsr;
    private int currentX = 0;
    private int currentY = 0;

    public InfinityRecipeWrapper(@Nonnull Block block1, @Nonnull Block block2) {
      this.bsr = new BlockSceneRenderer(
          new NNList<>(Pair.of(new BlockPos(0, 0, 0), block1.getDefaultState()), Pair.of(new BlockPos(0, 1, 0), block2.getDefaultState())));
    }

    @Override
    public void getIngredients(@Nonnull IIngredients ingredients) {
      ingredients.setOutput(ItemStack.class, Material.POWDER_INFINITY.getStack());
    }

    @Override
    public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
      int x = 15 + currentX;
      int y = 0 + currentY;
      int w = 26;
      int h = 50;

      GlStateManager.pushMatrix();
      bsr.drawScreen(x, y, w, h);
      GlStateManager.popMatrix();

      final String text = "%";
      int stringWidth = minecraft.fontRenderer.getStringWidth(text);
      minecraft.fontRenderer.drawString(text, 59 - stringWidth / 2, 26, 0xFFFFFF, false);

      if (!InfinityConfig.enableInAllDimensions.get()) {
        if (InfinityConfig.enableInDimensions.get().length == 1 && InfinityConfig.enableInDimensions.get()[0] == 0) {
          minecraft.fontRenderer.drawString(Lang.GUI_INFINTY_RECIPE_DIMENSIONS.get(), 45, 40, ColorUtil.getRGB(Color.GRAY));
        } else {
          minecraft.fontRenderer.drawString(Lang.GUI_INFINTY_RECIPE_DIMENSIONS_MULTI.get(), 45, 40, ColorUtil.getRGB(Color.GRAY));
        }
      }
    }

    @Override
    public @Nonnull List<String> getTooltipStrings(int mouseX, int mouseY) {
      if (mouseX >= 45 && mouseY >= 40 && mouseY < (40 + Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT) && !InfinityConfig.enableInAllDimensions.get()
          && !(InfinityConfig.enableInDimensions.get().length == 1 && InfinityConfig.enableInDimensions.get()[0] == 0)
          && mouseX < 45 + Minecraft.getMinecraft().fontRenderer.getStringWidth(Lang.GUI_INFINTY_RECIPE_DIMENSIONS_MULTI.get())) {
        List<String> result = new ArrayList<>();
        result.add(Lang.GUI_INFINTY_RECIPE_DIMENSIONS_THESE.get());
        result.add("");
        boolean inCurrent = false;
        for (int dim : InfinityConfig.enableInDimensions.get()) {
          if (Minecraft.getMinecraft().player.world.provider.getDimension() == dim) {
            result.add(Lang.GUI_INFINTY_RECIPE_DIMENSIONS_LIST_HERE.get(TelepadTarget.getDimenionName(dim)));
            inCurrent = true;
          } else {
            result.add(Lang.GUI_INFINTY_RECIPE_DIMENSIONS_LIST.get(TelepadTarget.getDimenionName(dim)));
          }
        }
        if (!inCurrent) {
          result.add("");
          result.add(Lang.GUI_INFINTY_RECIPE_DIMENSIONS_NOTHERE.get());
        }
        return result;
      }
      return IRecipeWrapper.super.getTooltipStrings(mouseX, mouseY);
    }

  }

  public static void registerExtras(IModRegistry registry) {
    final Boolean onFire = InfinityConfig.inWorldCraftingEnabled.get();
    final Boolean onWater = InfinityConfig.inWorldCraftingFireWaterEnabled.get();
    if (!onFire && !onWater && !InfinityConfig.bedrock.get().isEmpty()) {
      return;
    }

    InfinityConfig.bedrock.get().getItemStacks().apply(new Callback<ItemStack>() {
      @Override
      public void apply(@Nonnull ItemStack stack) {
        registry.addRecipeCatalyst(stack, InfinityRecipeCategory.UUID);
      }
    });
    if (onFire) {
      registry.addRecipeCatalyst(new ItemStack(Items.FLINT_AND_STEEL, 1, 0), InfinityRecipeCategory.UUID);
    }
    final Block fire_water = Fluids.FIRE_WATER.getFluid().getBlock();
    if (onWater && fire_water != null) {
      registry.addRecipeCatalyst(Fluids.FIRE_WATER.getBucket(), InfinityRecipeCategory.UUID);
    }

    long start = System.nanoTime();
    List<InfinityRecipeWrapper> result = new ArrayList<InfinityRecipeWrapper>();
    InfinityConfig.bedrock.get().getBlocks().apply(new Callback<Block>() {

      @Override
      public void apply(@Nonnull Block block) {
        if (onFire) {
          result.add(new InfinityRecipeWrapper(block, Blocks.FIRE));
        }
        if (onWater && fire_water != null) {
          result.add(new InfinityRecipeWrapper(block, fire_water));
        }
      }
    });
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
    background = guiHelper.createDrawable(backgroundLocation, 0, 10, 128, 50);
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

  @Override
  public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull InfinityRecipeWrapper recipeWrapper, @Nonnull IIngredients ingredients) {
    IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();
    itemStacks.init(0, false, 85, 20);
    itemStacks.set(ingredients);
    recipeWrapper.currentX = ((RecipeLayout) recipeLayout).getPosX();
    recipeWrapper.currentY = ((RecipeLayout) recipeLayout).getPosY();
  }

  @Override
  public @Nonnull String getModName() {
    return EnderIO.MODID;
  }

}
