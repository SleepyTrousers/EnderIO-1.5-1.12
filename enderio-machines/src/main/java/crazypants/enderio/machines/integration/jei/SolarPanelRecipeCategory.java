package crazypants.enderio.machines.integration.jei;

import java.awt.Color;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.client.render.ColorUtil;
import com.enderio.core.common.util.NNList;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.integration.jei.energy.EnergyIngredient;
import crazypants.enderio.base.integration.jei.energy.EnergyIngredientRenderer;
import crazypants.enderio.machines.EnderIOMachines;
import crazypants.enderio.machines.config.config.PersonalConfig;
import crazypants.enderio.machines.init.MachineObject;
import crazypants.enderio.machines.lang.Lang;
import crazypants.enderio.machines.machine.solar.SolarType;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiIngredientGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeCategory;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class SolarPanelRecipeCategory extends BlankRecipeCategory<SolarPanelRecipeCategory.SolarPanelRecipeWrapper> {

  public static final @Nonnull String UID = "SolarPanel";

  // ------------ Recipes

  public static class SolarPanelRecipeWrapper extends BlankRecipeWrapper {

    private final @Nonnull IDrawable sun;

    public SolarPanelRecipeWrapper(@Nonnull IGuiHelper guiHelper) {
      ResourceLocation sunLocation = new ResourceLocation(EnderIO.DOMAIN, "textures/gui/weather_sun.png");
      this.sun = guiHelper.createDrawable(sunLocation, 0, 0, 32, 32, 32, 32);
    }

    @Override
    public void getIngredients(@Nonnull IIngredients ingredients) {
      ingredients.setInputs(ItemStack.class, new NNList<ItemStack>( //
          new ItemStack(MachineObject.block_solar_panel.getBlockNN(), 1, 0), //
          new ItemStack(MachineObject.block_solar_panel.getBlockNN(), 1, 1), //
          new ItemStack(MachineObject.block_solar_panel.getBlockNN(), 1, 2), //
          new ItemStack(MachineObject.block_solar_panel.getBlockNN(), 1, 3)));

      ingredients.setOutputs(EnergyIngredient.class, new NNList<>( //
          new EnergyIngredient(Math.round(SolarType.SIMPLE.getRfperTick()), true), //
          new EnergyIngredient(Math.round(SolarType.NORMAL.getRfperTick()), true), //
          new EnergyIngredient(Math.round(SolarType.ADVANCED.getRfperTick()), true), //
          new EnergyIngredient(Math.round(SolarType.VIBRANT.getRfperTick()), true)));
    }

    @Override
    public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {

      GlStateManager.enableBlend(); // Fix for GlState varying with mouse position (JEI bug)
      sun.draw(minecraft, 77 - 16, 0);

      FontRenderer fr = minecraft.fontRenderer;
      String txt = Lang.JEI_SOLAR_OUTPUT.get();
      int sw = fr.getStringWidth(txt);
      fr.drawStringWithShadow(txt, recipeWidth / 2 - sw / 2, 68, ColorUtil.getRGB(Color.WHITE));
      GlStateManager.color(1, 1, 1, 1);
    }

    @Override
    public @Nonnull List<String> getTooltipStrings(int mouseX, int mouseY) {
      if (mouseY >= 65) {
        return Lang.JEI_SOLAR_RANGE.getLines();
      }
      return super.getTooltipStrings(mouseX, mouseY);
    }
  }

  // -------------------------------------

  public static void register() {
    // Check JEI Recipes are enabled
    if (!PersonalConfig.enableSolarJEIRecipes.get()) {
      return;
    }

    MachinesPlugin.iModRegistry.addRecipeCategories(new SolarPanelRecipeCategory(MachinesPlugin.iGuiHelper));
    MachinesPlugin.iModRegistry.addRecipeCategoryCraftingItem(new ItemStack(MachineObject.block_solar_panel.getBlockNN(), 1, 3), SolarPanelRecipeCategory.UID);
    MachinesPlugin.iModRegistry.addRecipeCategoryCraftingItem(new ItemStack(MachineObject.block_solar_panel.getBlockNN(), 1, 2), SolarPanelRecipeCategory.UID);
    MachinesPlugin.iModRegistry.addRecipeCategoryCraftingItem(new ItemStack(MachineObject.block_solar_panel.getBlockNN(), 1, 1), SolarPanelRecipeCategory.UID);
    MachinesPlugin.iModRegistry.addRecipeCategoryCraftingItem(new ItemStack(MachineObject.block_solar_panel.getBlockNN(), 1, 0), SolarPanelRecipeCategory.UID);
    MachinesPlugin.iModRegistry.addRecipes(Collections.singletonList(new SolarPanelRecipeWrapper(MachinesPlugin.iGuiHelper)), UID);
  }

  // ------------ Category

  @Nonnull
  private final IDrawable background;

  public SolarPanelRecipeCategory(@Nonnull IGuiHelper guiHelper) {
    background = guiHelper.createBlankDrawable(160, 95);
  }

  @Override
  public @Nonnull String getUid() {
    return UID;
  }

  @SuppressWarnings("null")
  @Override
  public @Nonnull String getTitle() {
    return MachineObject.block_solar_panel.getBlock().getLocalizedName();
  }

  @Override
  public @Nonnull IDrawable getBackground() {
    return background;
  }

  @Override
  public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull SolarPanelRecipeWrapper recipeWrapper, @Nonnull IIngredients ingredients) {
    IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
    IGuiIngredientGroup<EnergyIngredient> group = recipeLayout.getIngredientsGroup(EnergyIngredient.class);

    int offset = 40 / 2 - 16 / 2; // center of text minus half-width of stack
    guiItemStacks.init(0, true, 0 * 40 + offset, 45);
    guiItemStacks.init(1, true, 1 * 40 + offset, 45);
    guiItemStacks.init(2, true, 2 * 40 + offset, 45);
    guiItemStacks.init(3, true, 3 * 40 + offset, 45);
    group.init(4, false, EnergyIngredientRenderer.INSTANCE, 0 * 40, 80, 40, 10, 0, 0);
    group.init(5, false, EnergyIngredientRenderer.INSTANCE, 1 * 40, 80, 40, 10, 0, 0);
    group.init(6, false, EnergyIngredientRenderer.INSTANCE, 2 * 40, 80, 40, 10, 0, 0);
    group.init(7, false, EnergyIngredientRenderer.INSTANCE, 3 * 40, 80, 40, 10, 0, 0);

    guiItemStacks.set(ingredients);
    group.set(ingredients);
  }

  @Override
  public @Nonnull String getModName() {
    return EnderIOMachines.MODID;
  }

}
