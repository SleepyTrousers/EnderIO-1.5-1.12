package crazypants.enderio.machines.integration.jei;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.capacitor.DefaultCapacitorData;
import crazypants.enderio.base.fluid.Fluids;
import crazypants.enderio.base.integration.jei.energy.EnergyIngredient;
import crazypants.enderio.base.integration.jei.energy.EnergyIngredientRenderer;
import crazypants.enderio.machines.EnderIOMachines;
import crazypants.enderio.machines.capacitor.CapacitorKey;
import crazypants.enderio.machines.config.config.WeatherConfig;
import crazypants.enderio.machines.init.MachineObject;
import crazypants.enderio.machines.machine.obelisk.weather.ContainerWeatherObelisk;
import crazypants.enderio.machines.machine.obelisk.weather.GuiWeatherObelisk;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiIngredientGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeCategory;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

public class WeatherObeliskRecipeCategory extends BlankRecipeCategory<WeatherObeliskRecipeCategory.WeatherObeliskRecipeWrapper> {

  public static final @Nonnull String UID = "EIOWO";

  // ------------ Recipes

  public static class WeatherObeliskRecipeWrapper extends BlankRecipeWrapper {

    private final @Nonnull ItemStack itemInput;
    private final @Nonnull FluidStack fluidInput;
    private final @Nonnull EnergyIngredient energy;
    private final @Nonnull IDrawable weather;

    public WeatherObeliskRecipeWrapper(@Nonnull ItemStack itemInput, @Nonnull FluidStack fluidInput, int energy, @Nonnull String weather,
        @Nonnull IGuiHelper guiHelper) {
      this.itemInput = itemInput;
      this.fluidInput = fluidInput;
      this.energy = new EnergyIngredient(energy);
      ResourceLocation weatherLocation = new ResourceLocation(EnderIO.DOMAIN, "textures/gui/" + weather + ".png");
      this.weather = guiHelper.createDrawable(weatherLocation, 0, 0, 64, 64, 64, 64);
    }

    @Override
    public void getIngredients(@Nonnull IIngredients ingredients) {
      ingredients.setInput(ItemStack.class, itemInput);
      ingredients.setInput(FluidStack.class, fluidInput);
      ingredients.setInput(EnergyIngredient.class, energy);
    }

    @Override
    public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
      GlStateManager.enableBlend(); // Fix for GlState varying with mouse position (JEI bug)
      weather.draw(minecraft, 83, 4);
    }

  } // -------------------------------------

  public static void register(IModRegistry registry, @Nonnull IGuiHelper guiHelper) {

    registry.addRecipeCategories(new WeatherObeliskRecipeCategory(guiHelper));
    registry.addRecipeCategoryCraftingItem(new ItemStack(MachineObject.block_weather_obelisk.getBlockNN()), WeatherObeliskRecipeCategory.UID);
    registry.addRecipeClickArea(GuiWeatherObelisk.class, 155, 42, 16, 16, WeatherObeliskRecipeCategory.UID);

    long start = System.nanoTime();

    List<WeatherObeliskRecipeWrapper> result = new ArrayList<WeatherObeliskRecipeWrapper>();

    result.add(new WeatherObeliskRecipeWrapper(new ItemStack(Items.FIREWORKS),
        new FluidStack(Fluids.LIQUID_SUNSHINE.getFluid(), WeatherConfig.weatherObeliskClearFluid.get()),
        WeatherConfig.weatherObeliskClearFluid.get() / CapacitorKey.WEATHER_POWER_FLUID_USE.getBaseValue()
            * CapacitorKey.WEATHER_POWER_USE.getBaseValue(),
        "weather_sun", guiHelper));

    result.add(new WeatherObeliskRecipeWrapper(new ItemStack(Items.FIREWORKS),
        new FluidStack(Fluids.CLOUD_SEED.getFluid(), WeatherConfig.weatherObeliskRainFluid.get()),
        WeatherConfig.weatherObeliskRainFluid.get() / CapacitorKey.WEATHER_POWER_FLUID_USE.getBaseValue()
            * CapacitorKey.WEATHER_POWER_USE.getBaseValue(),
        "weather_rain", guiHelper));

    result.add(new WeatherObeliskRecipeWrapper(new ItemStack(Items.FIREWORKS),
        new FluidStack(Fluids.CLOUD_SEED_CONCENTRATED.getFluid(), WeatherConfig.weatherObeliskThunderFluid.get()),
        WeatherConfig.weatherObeliskThunderFluid.get() / CapacitorKey.WEATHER_POWER_FLUID_USE.getBaseValue()
            * CapacitorKey.WEATHER_POWER_USE.getBaseValue(),
        "weather_thunder", guiHelper));

    long end = System.nanoTime();
    registry.addRecipes(result, UID);

    registry.getRecipeTransferRegistry().addRecipeTransferHandler(ContainerWeatherObelisk.class, WeatherObeliskRecipeCategory.UID, 0, 1, 1, 4 * 9);

    Log.info(
        String.format("WeatherObeliskRecipeCategory: Added %d weather changing recipes to JEI in %.3f seconds.", result.size(), (end - start) / 1000000000d));
  }

  // ------------ Category

  private final @Nonnull IDrawable background;
  private final @Nonnull IDrawableAnimated flame;

  public WeatherObeliskRecipeCategory(IGuiHelper guiHelper) {
    ResourceLocation backgroundLocation = EnderIO.proxy.getGuiTexture("weather_obelisk");
    background = guiHelper.createDrawable(backgroundLocation, 19, 6, 150, 73);
    flame = guiHelper.createAnimatedDrawable(guiHelper.createDrawable(backgroundLocation, 176, 0, 14, 33), 200, IDrawableAnimated.StartDirection.BOTTOM, false);
  }

  @Override
  public @Nonnull String getUid() {
    return UID;
  }

  @Override
  public @Nonnull String getTitle() {
    return MachineObject.block_weather_obelisk.getBlockNN().getLocalizedName();
  }

  @Override
  public @Nonnull IDrawable getBackground() {
    return background;
  }

  @Override
  public void drawExtras(@Nonnull Minecraft minecraft) {
    flame.draw(minecraft, 62, 21);
  }

  @SuppressWarnings("null")
  @Override
  public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull WeatherObeliskRecipeWrapper recipeWrapper, @Nonnull IIngredients ingredients) {
    IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
    IGuiFluidStackGroup fluidStacks = recipeLayout.getFluidStacks();
    IGuiIngredientGroup<EnergyIngredient> group = recipeLayout.getIngredientsGroup(EnergyIngredient.class);

    guiItemStacks.init(0, true, 60, 4);
    fluidStacks.init(1, true, 3, 5, 16, 63, 8000, true, null);
    group.init(2, true, EnergyIngredientRenderer.INSTANCE, 27, 59, 60, 10, 0, 0);

    guiItemStacks.set(ingredients);
    fluidStacks.set(ingredients);
    group.set(ingredients);
  }

  @Override
  public @Nonnull String getModName() {
    return EnderIOMachines.MODID;
  }

}
