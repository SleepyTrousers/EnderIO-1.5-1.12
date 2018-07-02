package crazypants.enderio.machines.integration.jei;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import com.enderio.core.client.render.ColorUtil;
import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.capacitor.DefaultCapacitorData;
import crazypants.enderio.base.fluid.IFluidCoolant;
import crazypants.enderio.base.fluid.IFluidFuel;
import crazypants.enderio.base.integration.jei.energy.EnergyIngredient;
import crazypants.enderio.base.integration.jei.energy.EnergyIngredientRenderer;
import crazypants.enderio.base.lang.LangFluid;
import crazypants.enderio.machines.EnderIOMachines;
import crazypants.enderio.machines.capacitor.CapacitorKey;
import crazypants.enderio.machines.init.MachineObject;
import crazypants.enderio.machines.lang.Lang;
import crazypants.enderio.machines.machine.generator.combustion.CombustionMath;
import crazypants.enderio.machines.machine.generator.combustion.GuiCombustionGenerator;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.gui.IGuiIngredientGroup;
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
    private final CombustionMath mathMin, mathMax;

    private CombustionRecipeWrapper(FluidStack fluidCoolant, FluidStack fluidFuel, CombustionMath mathMin, CombustionMath mathMax) {
      this.fluidCoolant = fluidCoolant;
      this.fluidFuel = fluidFuel;
      this.mathMin = mathMin;
      this.mathMax = mathMax;
    }

    public void setInfoData(Map<Integer, ? extends IGuiIngredient<ItemStack>> ings) {
    }

    @Override
    public void getIngredients(@Nonnull IIngredients ingredients) {
      ingredients.setInputs(FluidStack.class, Arrays.asList(fluidCoolant, fluidFuel));
      ingredients.setOutputs(EnergyIngredient.class,
          new NNList<>(new EnergyIngredient(mathMin.getEnergyPerTick(), true), new EnergyIngredient(mathMax.getEnergyPerTick(), true)));
    }

    @Override
    public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
      FontRenderer fr = minecraft.fontRenderer;

      String txt = Lang.GUI_COMBGEN_OUTPUT.get("");
      int sw = fr.getStringWidth(txt);
      fr.drawStringWithShadow(txt, 89 - sw / 2 - xOff, 0 - yOff, ColorUtil.getRGB(Color.WHITE));
      txt = "-";
      sw = fr.getStringWidth(txt);
      fr.drawStringWithShadow("-", 89 - sw / 2 - xOff, 10 - yOff, ColorUtil.getRGB(Color.WHITE));

      int y = 21 - yOff - 2;
      int x = 114 - xOff;
      txt = mathMax.getTicksPerCoolant() + "-" + LangFluid.tMB(mathMin.getTicksPerCoolant());
      sw = fr.getStringWidth(txt);
      fr.drawStringWithShadow(txt, x - sw / 2 + 7, y + fr.FONT_HEIGHT / 2 + 47, ColorUtil.getRGB(Color.WHITE));

      x = 48 - xOff;
      txt = mathMax.getTicksPerFuel() + "-" + LangFluid.tMB(mathMin.getTicksPerFuel());
      sw = fr.getStringWidth(txt);
      fr.drawStringWithShadow(txt, x - sw / 2 + 7, y + fr.FONT_HEIGHT / 2 + 47, ColorUtil.getRGB(Color.WHITE));

      GlStateManager.color(1, 1, 1, 1);
    }

    @Override
    public @Nonnull List<String> getTooltipStrings(int mouseX, int mouseY) {
      if (mouseY < (20 - yOff) || mouseY > (21 - yOff + 47 + 1)) {
        return Lang.JEI_COMBGEN_RANGE.getLines();
      }
      return super.getTooltipStrings(mouseX, mouseY);
    }

  } // -------------------------------------

  public static void register(IModRegistry registry, IGuiHelper guiHelper) {

    registry.addRecipeCategories(new CombustionRecipeCategory(guiHelper));
    registry.addRecipeCategoryCraftingItem(new ItemStack(MachineObject.block_combustion_generator.getBlockNN(), 1, 0), CombustionRecipeCategory.UID);
    registry.addRecipeCategoryCraftingItem(new ItemStack(MachineObject.block_enhanced_combustion_generator.getBlockNN(), 1, 0), CombustionRecipeCategory.UID);
    registry.addRecipeClickArea(GuiCombustionGenerator.class, 155, 42, 16, 16, CombustionRecipeCategory.UID);

    long start = System.nanoTime();

    Map<String, Fluid> fluids = FluidRegistry.getRegisteredFluids();

    List<CombustionRecipeWrapper> result = new ArrayList<CombustionRecipeWrapper>();

    for (Fluid fluid1 : fluids.values()) {
      IFluidCoolant coolant = CombustionMath.toCoolant(fluid1);
      if (coolant != null) {
        for (Fluid fluid2 : fluids.values()) {
          IFluidFuel fuel = CombustionMath.toFuel(fluid2);
          if (fuel != null) {
            CombustionMath mathMin = new CombustionMath(coolant, fuel, CapacitorKey.COMBUSTION_POWER_GEN.getFloat(DefaultCapacitorData.BASIC_CAPACITOR),
                CapacitorKey.COMBUSTION_POWER_EFFICIENCY.get(DefaultCapacitorData.BASIC_CAPACITOR));
            CombustionMath mathmax = new CombustionMath(coolant, fuel, CapacitorKey.COMBUSTION_POWER_GEN.getFloat(DefaultCapacitorData.ENDER_CAPACITOR),
                CapacitorKey.ENHANCED_COMBUSTION_POWER_EFFICIENCY.get(DefaultCapacitorData.ENDER_CAPACITOR));
            result.add(new CombustionRecipeWrapper(new FluidStack(fluid1, 1000), new FluidStack(fluid2, 1000), mathMin, mathmax));
          }
        }
      }
    }

    long end = System.nanoTime();
    registry.addRecipes(result, UID);

    Log.info(
        String.format("CombustionRecipeCategory: Added %d combustion generator recipes to JEI in %.3f seconds.", result.size(), (end - start) / 1000000000d));
  }

  // ------------ Category

  // Offsets from full size gui, makes it much easier to get the location
  // correct
  static int xOff = 25 + 3;
  static int yOff = 7;
  static int xSize = 136 - 3;

  @Nonnull
  private final IDrawable background;

  public CombustionRecipeCategory(IGuiHelper guiHelper) {
    ResourceLocation backgroundLocation = EnderIO.proxy.getGuiTexture("combustion_gen");
    background = guiHelper.createDrawable(backgroundLocation, xOff, yOff, xSize, 70);
  }

  @Override
  public @Nonnull String getUid() {
    return UID;
  }

  @SuppressWarnings("null")
  @Override
  public @Nonnull String getTitle() {
    return MachineObject.block_combustion_generator.getBlock().getLocalizedName();
  }

  @Override
  public @Nonnull IDrawable getBackground() {
    return background;
  }

  @Override
  public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull CombustionRecipeWrapper recipeWrapper, @Nonnull IIngredients ingredients) {
    IGuiFluidStackGroup fluidStacks = recipeLayout.getFluidStacks();

    fluidStacks.init(0, true, 114 - xOff, 21 - yOff, 15, 47, 1000, false, null);
    fluidStacks.init(1, true, 48 - xOff, 21 - yOff, 15, 47, 1000, false, null);

    fluidStacks.set(ingredients);

    IGuiIngredientGroup<EnergyIngredient> group = recipeLayout.getIngredientsGroup(EnergyIngredient.class);
    group.init(2, false, EnergyIngredientRenderer.INSTANCE, 37 - xOff, 9 - yOff, 40, 10, 0, 0);
    group.init(3, false, EnergyIngredientRenderer.INSTANCE, 54 + 47 - xOff, 9 - yOff, 40, 10, 0, 0);
    group.set(ingredients);

  }

  @Override
  public @Nonnull String getModName() {
    return EnderIOMachines.MODID;
  }

}
