package crazypants.enderio.machines.config.config;

import crazypants.enderio.machines.config.Config;
import info.loenwind.autoconfig.factory.IValue;
import info.loenwind.autoconfig.factory.IValueFactory;

public class PersonalConfig {

  public static final IValueFactory F = Config.F.section("personal");

  public static final IValue<Boolean> enablePainterJEIRecipes = F.make("enablePainterJEI", true, //
      "Should the Painting Machine have a JEI recipe category?").sync();

  public static final IValue<Boolean> enableTankFluidInOutJEIRecipes = F.make("enableTankFluidInOutJEI", true, //
      "Should the Tank have JEI recipes for insert and extracting fluids?").sync();

  public static final IValue<Boolean> enableTankMendingJEIRecipes = F.make("enableTankMendingJEI", true, //
      "Should the Tank have JEI recipes for ending with XP?").sync();

  public static final IValue<Boolean> enableStirlingJEIRecipes = F.make("enableStirlingGenJEI", true, //
      "Should the Stirling Generator have JEI recipes?").sync();

  public static final IValue<Boolean> enableAlloySmelterFurnaceJEIRecipes = F.make("enableAlloySmelterFurnaceJEI", true, //
      "Should the Alloy Smelter have JEI recipes for Smelting?").sync();

  public static final IValue<Boolean> enableAlloySmelterAlloyingJEIRecipes = F.make("enableAlloySmelterAlloyingJEI", true, //
      "Should the Alloy Smelter have JEI recipes for Alloying?").sync();

  public static final IValue<Boolean> enableCombustionGenJEIRecipes = F.make("enableCombustionGenJEI", true, //
      "Should the Combustion Generator have JEI recipes?").sync();

  public static final IValue<Boolean> enableEnchanterJEIRecipes = F.make("enableEnchanterJEI", true, //
      "Should the Enchanter have JEI recipes?").sync();

  public static final IValue<Boolean> enableEnderGenJEIRecipes = F.make("enableEnderGenJEI", true, //
      "Should the Ender Generator have JEI recipes?").sync();

  public static final IValue<Boolean> enableGrindingBallJEIRecipes = F.make("enableGrindingBallsJEI", true, //
      "Should Grinding Balls stats have a JEI recipe category?").sync();

  public static final IValue<Boolean> enableSliceAndSpliceJEIRecipes = F.make("enableSliceAndSpliceJEI", true, //
      "Should the Slice and Splice have JEI recipes?").sync();

  public static final IValue<Boolean> enableSolarJEIRecipes = F.make("enableSolarJEI", true, //
      "Should Solar Panels have JEI recipes?").sync();

  public static final IValue<Boolean> enableLavaGeneratorRecipes = F.make("enableLavaGeneratorJEI", true, //
      "Should the Lava Generator have JEI recipes?").sync();

  public static final IValue<Boolean> enableSoulBinderJEIRecipes = F.make("enableSoulBinderJEI", true, //
      "Should the Soul Binder have JEI recipes?").sync();

  public static final IValue<Boolean> enableVatJEIRecipes = F.make("enableVatJEI", true, //
      "Should the Vat have JEI recipes?").sync();

  public static final IValue<Boolean> enableWeatherObeliskJEIRecipes = F.make("enableWeatherObeliskJEI", true, //
      "Should the Weather Obelisk have JEI recipes?").sync();

  public static final IValue<Boolean> enableWiredChargerJEIRecipes = F.make("enableWiredChargerJEI", true, //
      "Should the Wired Charger have JEI recipes?").sync();

  public static final IValue<Boolean> enableZombieGenJEIRecipes = F.make("enableZombieGenJEI", true, //
      "Should the Zombie Generator have JEI recipes?").sync();

}
