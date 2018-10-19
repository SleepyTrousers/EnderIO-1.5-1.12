package crazypants.enderio.machines.integration.jei;

import javax.annotation.Nonnull;

import crazypants.enderio.machines.integration.jei.sagmill.SagMillRecipeCategory;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraftforge.fml.common.ProgressManager;

@JEIPlugin
public class MachinesPlugin implements IModPlugin {

  @Override
  public void registerCategories(@Nonnull IRecipeCategoryRegistration registry) {
    IJeiHelpers jeiHelpers = registry.getJeiHelpers();
    registry.addRecipeCategories(new PainterRecipeCategory(jeiHelpers));
  }

  @Override
  public void register(@Nonnull IModRegistry registry) {
    ProgressManager.ProgressBar bar = ProgressManager.push("Ender IO Machines", 17, true);

    IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();

    bar.step("Alloy Smelter");
    AlloyRecipeCategory.register(registry, guiHelper);
    bar.step("Combustion Generator");
    CombustionRecipeCategory.register(registry, guiHelper);
    bar.step("Crafter");
    CrafterRecipeTransferHandler.register(registry);
    bar.step("Enchanter");
    EnchanterRecipeCategory.register(registry, guiHelper);
    bar.step("Painter");
    PainterRecipeCategory.register(registry);
    bar.step("Sag Mill");
    SagMillRecipeCategory.register(registry, guiHelper);
    bar.step("Grinding Balls");
    SagMillGrindingBallCategory.register(registry, guiHelper);
    bar.step("Slice'n'Splice");
    SliceAndSpliceRecipeCategory.register(registry, guiHelper);
    bar.step("Solar Panels");
    SolarPanelRecipeCategory.register(registry, guiHelper);
    bar.step("Soul Binder");
    SoulBinderRecipeCategory.register(registry, guiHelper);
    bar.step("Stirling Generator");
    StirlingRecipeCategory.register(registry, guiHelper);
    bar.step("Tank");
    TankRecipeCategory.register(registry, guiHelper);
    bar.step("The Vat");
    VatRecipeCategory.register(registry, guiHelper);
    bar.step("Wired Charger");
    WiredChargerRecipeCategory.register(registry, guiHelper);
    bar.step("Weather Obelisk");
    WeatherObeliskRecipeCategory.register(registry, guiHelper);
    bar.step("Zombie Generator");
    ZombieGeneratorRecipeCategory.register(registry, guiHelper);
    bar.step("Ender Generator");
    EnderGeneratorRecipeCategory.register(registry, guiHelper);

    ProgressManager.pop(bar);
  }
}
