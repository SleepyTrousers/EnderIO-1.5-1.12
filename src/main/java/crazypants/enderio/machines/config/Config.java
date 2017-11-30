package crazypants.enderio.machines.config;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import crazypants.enderio.base.config.Config.Section;
import crazypants.enderio.base.config.ValueFactory;
import crazypants.enderio.base.config.ValueFactory.IValue;

@ParametersAreNonnullByDefault // Not the right one, but eclipse knows only 3 null annotations anyway, so it's ok
public final class Config {

  public static final Section sectionCapacitor = new Section("Capacitor Values", "capacitor");
  public static final Section ENCHANTER = new Section("", "enchanter");
  public static final Section CLIENT = new Section("", "client");

  static final ValueFactory F = new ValueFactory();

  public static final IValue<Integer> enchanterBaseLevelCost = F.make(ENCHANTER, "enchanterBaseLevelCost", 2, //
      "Base level cost added to all recipes in the enchanter.");
  public static final IValue<Double> enchanterLevelCostFactor = F.make(ENCHANTER, "enchanterLevelCostFactor", 0.75, //
      "The final XP cost for an enchantment is multiplied by this value. To halve costs set to 0.5, to double them set it to 2.");
  public static final IValue<Double> enchanterLapisCostFactor = F.make(ENCHANTER, "enchanterLapisCostFactor", 3.0, //
      "The lapis cost is enchant level multiplied by this value.");

  public static final IValue<Boolean> jeiUseShortenedPainterRecipes = F.make(CLIENT, "jeiUseShortenedPainterRecipes", true, //
      "If true, only a handful of sample painter recipes will be shown in JEI. Enable this if you have timing problems starting a world or logging into a server.");

  public static final IValue<Boolean> machineSoundsEnabled = new IValue<Boolean>() {
    @Override
    public @Nonnull Boolean get() {
      return crazypants.enderio.base.config.Config.machineSoundsEnabled;
    }
  };

  public static final IValue<Float> machineSoundVolume = new IValue<Float>() {
    @Override
    public @Nonnull Float get() {
      return crazypants.enderio.base.config.Config.machineSoundVolume;
    }
  };

  //
}
