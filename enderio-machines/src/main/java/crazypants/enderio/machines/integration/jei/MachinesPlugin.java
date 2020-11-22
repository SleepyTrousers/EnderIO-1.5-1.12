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


  public static IModRegistry iModRegistry;
  public static IGuiHelper iGuiHelper;

  @Override
  public void registerCategories(@Nonnull IRecipeCategoryRegistration registry) {
    IJeiHelpers jeiHelpers = registry.getJeiHelpers();
    registry.addRecipeCategories(new PainterRecipeCategory(jeiHelpers));
  }

  @Override
  public void register(@Nonnull IModRegistry registry) {
    iModRegistry = registry;
    ProgressManager.ProgressBar bar = ProgressManager.push("Ender IO Machines", 18, true);
    iGuiHelper = registry.getJeiHelpers().getGuiHelper();

    bar.step("Alloy Smelter");
    AlloyRecipeCategory.register();
    bar.step("Combustion Generator");
    CombustionRecipeCategory.register();
    bar.step("Crafter");
    CrafterRecipeTransferHandler.register();
    bar.step("Enchanter");
    EnchanterRecipeCategory.register();
    bar.step("Painter");
    PainterRecipeCategory.register();
    bar.step("Sag Mill");
    SagMillRecipeCategory.register();
    bar.step("Grinding Balls");
    SagMillGrindingBallCategory.register();
    bar.step("Slice'n'Splice");
    SliceAndSpliceRecipeCategory.register();
    bar.step("Solar Panels");
    SolarPanelRecipeCategory.register();
    bar.step("Soul Binder");
    SoulBinderRecipeCategory.register();
    bar.step("Stirling Generator");
    StirlingRecipeCategory.register();
    bar.step("Tank");
    TankRecipeCategory.register();
    bar.step("The Vat");
    VatRecipeCategory.register();
    bar.step("Wired Charger");
    WiredChargerRecipeCategory.register();
    bar.step("Weather Obelisk");
    WeatherObeliskRecipeCategory.register();
    bar.step("Zombie Generator");
    ZombieGeneratorRecipeCategory.register();
    bar.step("Ender Generator");
    EnderGeneratorRecipeCategory.register();
    bar.step("Lava Generator");
    LavaGeneratorRecipeCategory.register();

    ProgressManager.pop(bar);
  }
}
