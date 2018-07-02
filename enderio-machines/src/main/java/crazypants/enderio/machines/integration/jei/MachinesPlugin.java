package crazypants.enderio.machines.integration.jei;

import javax.annotation.Nonnull;

import crazypants.enderio.machines.integration.jei.sagmill.SagMillRecipeCategory;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;

@JEIPlugin
public class MachinesPlugin implements IModPlugin {

  @Override
  public void registerCategories(@Nonnull IRecipeCategoryRegistration registry) {
    IJeiHelpers jeiHelpers = registry.getJeiHelpers();
    registry.addRecipeCategories(new PainterRecipeCategory(jeiHelpers));
  }

  @Override
  public void register(@Nonnull IModRegistry registry) {

    IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();

    AlloyRecipeCategory.register(registry, guiHelper);
    CombustionRecipeCategory.register(registry, guiHelper);
    EnchanterRecipeCategory.register(registry, guiHelper);
    PainterRecipeCategory.register(registry);
    SagMillRecipeCategory.register(registry, guiHelper);
    SagMillGrindingBallCategory.register(registry, guiHelper);
    SliceAndSpliceRecipeCategory.register(registry, guiHelper);
    SolarPanelRecipeCategory.register(registry, guiHelper);
    SoulBinderRecipeCategory.register(registry, guiHelper);
    StirlingRecipeCategory.register(registry, guiHelper);
    TankRecipeCategory.register(registry, guiHelper);
    VatRecipeCategory.register(registry, guiHelper);
    WiredChargerRecipeCategory.register(registry, guiHelper);
    WeatherObeliskRecipeCategory.register(registry, guiHelper);
    ZombieGeneratorRecipeCategory.register(registry, guiHelper);
    EnderGeneratorRecipeCategory.register(registry, guiHelper);
  }
}
