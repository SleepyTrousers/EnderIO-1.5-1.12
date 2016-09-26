package crazypants.enderio.integration.jei;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import com.enderio.core.client.render.ColorUtil;

import crazypants.enderio.EnderIO;
import crazypants.enderio.Log;
import crazypants.enderio.fluid.FluidFuelRegister;
import crazypants.enderio.fluid.IFluidCoolant;
import crazypants.enderio.fluid.IFluidFuel;
import crazypants.enderio.machine.generator.combustion.GuiCombustionGenerator;
import crazypants.enderio.machine.generator.combustion.TileCombustionGenerator;
import crazypants.enderio.machine.power.PowerDisplayUtil;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeCategory;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class CombustionRecipeCategory extends BlankRecipeCategory<CombustionRecipeCategory.CombustionRecipeWrapper> {

  public static final @Nonnull String UID = "CombustionGenerator";

  // ------------ Recipes

  public static class CombustionRecipeWrapper extends BlankRecipeWrapper {

    private final FluidStack fluidCoolant, fluidFuel;
    private final float fluidCoolantPerTick, fluidFuelPerTick;
    private final int rfPerTick;

    private CombustionRecipeWrapper(FluidStack fluidCoolant, FluidStack fluidFuel, float fluidCoolantPerTick, float fluidFuelPerTick, int rfPerTick) {
      this.fluidCoolant = fluidCoolant;
      this.fluidFuel = fluidFuel;
      this.fluidCoolantPerTick = fluidCoolantPerTick;
      this.fluidFuelPerTick = fluidFuelPerTick;
      this.rfPerTick = rfPerTick;
    }

    public void setInfoData(Map<Integer, ? extends IGuiIngredient<ItemStack>> ings) {
    }

    @Override
    public void getIngredients(@Nonnull IIngredients ingredients) {
      ingredients.setInputs(FluidStack.class, Arrays.asList(fluidCoolant, fluidFuel));
    }

    @Override
    public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
      FontRenderer fr = minecraft.fontRendererObj;

      String txt = EnderIO.lang.localize("combustionGenerator.output") + " " + PowerDisplayUtil.formatPower(rfPerTick) + " " + PowerDisplayUtil.abrevation()
          + PowerDisplayUtil.perTickStr();
      int sw = fr.getStringWidth(txt);
      fr.drawStringWithShadow(txt, 176 / 2 - sw / 2 - xOff, fr.FONT_HEIGHT / 2, ColorUtil.getRGB(Color.WHITE));

      int y = 21 - yOff - 2;
      int x = 114 - xOff;
      txt = fluidCoolantPerTick + " " + EnderIO.lang.localize("power.tmb");
      sw = fr.getStringWidth(txt);
      fr.drawStringWithShadow(txt, x - sw / 2 + 7, y + fr.FONT_HEIGHT / 2 + 47, ColorUtil.getRGB(Color.WHITE));

      x = 48 - xOff;
      txt = fluidFuelPerTick + " " + EnderIO.lang.localize("power.tmb");
      sw = fr.getStringWidth(txt);
      fr.drawStringWithShadow(txt, x - sw / 2 + 7, y + fr.FONT_HEIGHT / 2 + 47, ColorUtil.getRGB(Color.WHITE));

      GlStateManager.color(1, 1, 1, 1);
    }

  } // -------------------------------------

  public static void register(IModRegistry registry, IGuiHelper guiHelper) {

    registry.addRecipeCategories(new CombustionRecipeCategory(guiHelper));
    registry.addRecipeHandlers(new BaseRecipeHandler<CombustionRecipeWrapper>(CombustionRecipeWrapper.class, CombustionRecipeCategory.UID));
    registry.addRecipeCategoryCraftingItem(new ItemStack(EnderIO.blockCombustionGenerator, 1, 0), CombustionRecipeCategory.UID);
    registry.addRecipeClickArea(GuiCombustionGenerator.class, 155, 42, 16, 16, CombustionRecipeCategory.UID);

    long start = System.nanoTime();

    Map<String, Fluid> fluids = FluidRegistry.getRegisteredFluids();

    List<CombustionRecipeWrapper> result = new ArrayList<CombustionRecipeWrapper>();

    for (Fluid fluid1 : fluids.values()) {
      IFluidCoolant coolant = FluidFuelRegister.instance.getCoolant(fluid1);
      if (coolant != null) {
        for (Fluid fluid2 : fluids.values()) {
          IFluidFuel fuel = FluidFuelRegister.instance.getFuel(fluid2);
          if (fuel != null) {
            result.add(new CombustionRecipeWrapper(new FluidStack(fluid1, 1000), new FluidStack(fluid2, 1000),
                TileCombustionGenerator.getNumTicksPerMbCoolant(coolant, fuel), TileCombustionGenerator.getNumTicksPerMbFuel(fuel), fuel.getPowerPerCycle()));
          }
        }
      }
    }

    long end = System.nanoTime();
    registry.addRecipes(result);

    Log.info(String.format("TankRecipeCategory: Added %d combustion generator recipes to JEI in %.3f seconds.", result.size(), (end - start) / 1000000000d));
  }

  // ------------ Category

  // Offsets from full size gui, makes it much easier to get the location
  // correct
  static int xOff = 25;
  static int yOff = 7;
  static int xSize = 136;

  @Nonnull
  private final IDrawable background;

  public CombustionRecipeCategory(IGuiHelper guiHelper) {
    ResourceLocation backgroundLocation = EnderIO.proxy.getGuiTexture("combustionGen");
    background = guiHelper.createDrawable(backgroundLocation, xOff, yOff, xSize, 70);
  }

  @Override
  public @Nonnull String getUid() {
    return UID;
  }

  @SuppressWarnings("null")
  @Override
  public @Nonnull String getTitle() {
    return EnderIO.blockCombustionGenerator.getLocalizedName();
  }

  @Override
  public @Nonnull IDrawable getBackground() {
    return background;
  }

  @SuppressWarnings("null")
  @Override
  public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull CombustionRecipeWrapper recipeWrapper, @Nonnull IIngredients ingredients) {
    IGuiFluidStackGroup fluidStacks = recipeLayout.getFluidStacks();

    fluidStacks.init(0, true, 114 - xOff, 21 - yOff, 15, 47, 1000, false, null);
    fluidStacks.init(1, true, 48 - xOff, 21 - yOff, 15, 47, 1000, false, null);

    fluidStacks.set(ingredients);
  }

}
