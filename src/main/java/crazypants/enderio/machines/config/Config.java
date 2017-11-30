package crazypants.enderio.machines.config;

import javax.annotation.Nonnull;

import crazypants.enderio.base.config.Config.Section;
import crazypants.enderio.base.config.ValueFactory;
import crazypants.enderio.base.config.ValueFactory.IValue;

public class Config {

  public static final @Nonnull Section sectionCapacitor = new Section("Capacitor Values", "capacitor");
  public static final @Nonnull Section sectionTest = new Section("Test Section", "test");
  public static final @Nonnull Section ENCHANTER = new Section("", "enchanter");

  static final ValueFactory F = new ValueFactory();

  public static final IValue<Integer> test = F.make(sectionTest, "bar", 123, "A test F");

  public static final IValue<Integer> enchanterBaseLevelCost = F.make(ENCHANTER, "enchanterBaseLevelCost", 2, //
      "Base level cost added to all recipes in the enchanter.");
  public static final IValue<Double> enchanterLevelCostFactor = F.make(ENCHANTER, "enchanterLevelCostFactor", 0.75, //
      "The final XP cost for an enchantment is multiplied by this value. To halve costs set to 0.5, to double them set it to 2.");
  public static final IValue<Double> enchanterLapisCostFactor = F.make(ENCHANTER, "enchanterLapisCostFactor", 3.0, //
      "The lapis cost is enchant level multiplied by this value.");

}
