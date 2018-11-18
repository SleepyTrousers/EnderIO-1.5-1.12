package crazypants.enderio.machines.integration.jei;

import java.awt.Color;
import java.util.Collections;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.tuple.Pair;

import com.enderio.core.client.render.ColorUtil;
import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.capacitor.DefaultCapacitorData;
import crazypants.enderio.base.gui.BlockSceneRenderer;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.integration.jei.energy.EnergyIngredient;
import crazypants.enderio.base.integration.jei.energy.EnergyIngredientRenderer;
import crazypants.enderio.base.lang.LangFluid;
import crazypants.enderio.base.render.property.EnumRenderMode;
import crazypants.enderio.base.render.property.EnumRenderPart;
import crazypants.enderio.machines.EnderIOMachines;
import crazypants.enderio.machines.capacitor.CapacitorKey;
import crazypants.enderio.machines.config.config.LavaGenConfig;
import crazypants.enderio.machines.config.config.PersonalConfig;
import crazypants.enderio.machines.init.MachineObject;
import crazypants.enderio.machines.lang.Lang;
import crazypants.enderio.machines.machine.generator.lava.TileLavaGenerator;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiIngredientGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeCategory;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import mezz.jei.gui.recipes.RecipeLayout;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class LavaGeneratorRecipeCategory extends BlankRecipeCategory<LavaGeneratorRecipeCategory.LavaGeneratorRecipeWrapper> {

  public static final @Nonnull String UID = "LavaGenerator";

  // ------------ Recipes

  public static class LavaGeneratorRecipeWrapper extends BlankRecipeWrapper {

    private final @Nonnull BlockSceneRenderer bsr;
    private int currentX = 0;
    private int currentY = 0;

    public LavaGeneratorRecipeWrapper(@Nonnull IGuiHelper guiHelper) {
      final IBlockState gen = MachineObject.block_lava_generator.getBlockNN().getDefaultState().withProperty(EnumRenderMode.RENDER, EnumRenderMode.FRONT_ON);
      final IBlockState chassis = ModObject.block_machine_base.getBlockNN().getDefaultState().withProperty(EnumRenderPart.SUB, EnumRenderPart.BODY);
      final IBlockState waterfull = Blocks.WATER.getDefaultState().withProperty(net.minecraft.block.BlockLiquid.LEVEL, 15);
      final IBlockState water = Blocks.WATER.getDefaultState().withProperty(net.minecraft.block.BlockLiquid.LEVEL, 13);
      final IBlockState air = Blocks.AIR.getDefaultState();
      this.bsr = new BlockSceneRenderer(new NNList<>(Pair.of(new BlockPos(1, 1, 1), chassis), Pair.of(new BlockPos(1, 1, 1), gen),
          Pair.of(new BlockPos(1, 0, 1), waterfull), Pair.of(new BlockPos(1, 1, 0), air), Pair.of(new BlockPos(0, 1, 1), water),
          Pair.of(new BlockPos(1, 1, 2), water), Pair.of(new BlockPos(2, 1, 1), water)));
    }

    @Override
    public void getIngredients(@Nonnull IIngredients ingredients) {
      ingredients.setInputs(FluidStack.class, new NNList<FluidStack>(new FluidStack(FluidRegistry.LAVA, 1000)));

      ingredients.setOutputs(EnergyIngredient.class, new NNList<>( //
          new EnergyIngredient(Math.round(TileLavaGenerator.getNominalPowerGenPerTick(DefaultCapacitorData.BASIC_CAPACITOR)), true), //
          new EnergyIngredient(Math.round(TileLavaGenerator.getNominalPowerGenPerTick(DefaultCapacitorData.ENDER_CAPACITOR)), true)));
    }

    @Override
    public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
      int x = 15 + currentX;
      int y = 0 + currentY;
      int w = 160 - 2 * 15;
      int h = 50;

      GlStateManager.pushMatrix();
      bsr.drawScreen(x, y, w, h);
      GlStateManager.popMatrix();

      FontRenderer fr = minecraft.fontRenderer;

      String txt = "-";
      int sw = fr.getStringWidth(txt);
      fr.drawStringWithShadow("-", 80 - sw / 2, 50, ColorUtil.getRGB(Color.WHITE));

      int burnTime = (LavaGenConfig.useVanillaBurnTime.get() ? 20000 : TileEntityFurnace.getItemBurnTime(new ItemStack(Items.LAVA_BUCKET)))
          / Fluid.BUCKET_VOLUME / CapacitorKey.LAVAGEN_POWER_FLUID_USE.get(DefaultCapacitorData.BASIC_CAPACITOR);

      txt = LangFluid.tMB(burnTime);
      fr.drawString(txt, 0, 40, ColorUtil.getRGB(Color.GRAY));

      int i = 0;
      for (String split : Lang.JEI_LAVAGEN_COOLING.get().split("\\s+")) {
        if (split != null) {
          sw = fr.getStringWidth(split);
          fr.drawString(split, 160 - sw, 8 * i++, ColorUtil.getRGB(Color.GRAY));
        }
      }

      txt = Lang.JEI_LAVAGEN_HEAT.get();
      sw = fr.getStringWidth(txt);
      fr.drawString(txt, 80 - sw / 2, 60, ColorUtil.getRGB(Color.GRAY));
    }

  }

  // -------------------------------------

  public static void register(@Nonnull IModRegistry registry, @Nonnull IGuiHelper guiHelper) {
    // Check JEI Recipes are enabled
    if (!PersonalConfig.enableLavaGeneratorRecipes.get()) {
      return;
    }

    registry.addRecipeCategories(new LavaGeneratorRecipeCategory(guiHelper));
    registry.addRecipeCategoryCraftingItem(new ItemStack(MachineObject.block_lava_generator.getBlockNN()), LavaGeneratorRecipeCategory.UID);
    registry.addRecipes(Collections.singletonList(new LavaGeneratorRecipeWrapper(guiHelper)), UID);
  }

  // ------------ Category

  @Nonnull
  private final IDrawable background;

  public LavaGeneratorRecipeCategory(@Nonnull IGuiHelper guiHelper) {
    background = guiHelper.createBlankDrawable(160, 70);
  }

  @Override
  public @Nonnull String getUid() {
    return UID;
  }

  @SuppressWarnings("null")
  @Override
  public @Nonnull String getTitle() {
    return MachineObject.block_lava_generator.getBlock().getLocalizedName();
  }

  @Override
  public @Nonnull IDrawable getBackground() {
    return background;
  }

  @Override
  public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull LavaGeneratorRecipeWrapper recipeWrapper, @Nonnull IIngredients ingredients) {
    IGuiFluidStackGroup fluidStacks = recipeLayout.getFluidStacks();
    IGuiIngredientGroup<EnergyIngredient> group = recipeLayout.getIngredientsGroup(EnergyIngredient.class);

    fluidStacks.init(0, true, 10, 22);
    group.init(1, false, EnergyIngredientRenderer.INSTANCE, 1 * 40 - 10, 50, 40, 10, 0, 0);
    group.init(2, false, EnergyIngredientRenderer.INSTANCE, 2 * 40 + 10, 50, 40, 10, 0, 0);

    fluidStacks.set(ingredients);
    group.set(ingredients);

    recipeWrapper.currentX = ((RecipeLayout) recipeLayout).getPosX();
    recipeWrapper.currentY = ((RecipeLayout) recipeLayout).getPosY();
  }

  @Override
  public @Nonnull String getModName() {
    return EnderIOMachines.MODID;
  }

}
