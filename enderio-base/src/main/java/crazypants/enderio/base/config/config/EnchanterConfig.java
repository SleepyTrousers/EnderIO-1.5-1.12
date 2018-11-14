package crazypants.enderio.base.config.config;

import info.loenwind.autoconfig.factory.IValue;
import info.loenwind.autoconfig.factory.IValueFactory;

public final class EnchanterConfig {

  public static final IValueFactory F = BaseConfig.F.section("enchanter");

  public static final IValue<Integer> baseLevelCost = F.make("baseLevelCost", 2, //
      "Base level cost added to all recipes in the enchanter.").setMin(0).sync();
  public static final IValue<Double> levelCostFactor = F.make("levelCostFactor", 0.75, //
      "The final XP cost for an enchantment is multiplied by this value. To halve costs set to 0.5, to double them set it to 2.").setMin(0).sync();
  public static final IValue<Double> lapisCostFactor = F.make("lapisCostFactor", 3.0, //
      "The lapis cost is enchant level multiplied by this value.").setMin(0).sync();

}
