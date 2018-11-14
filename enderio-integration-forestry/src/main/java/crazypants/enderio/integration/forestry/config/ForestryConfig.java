package crazypants.enderio.integration.forestry.config;

import info.loenwind.autoconfig.factory.IValue;
import info.loenwind.autoconfig.factory.IValueFactory;

public final class ForestryConfig {

  public static final IValueFactory F = Config.F.section("items.darksteel.upgrades.forestry");

  public static final IValue<Integer> apiaristArmorCost = F.make("apiaristArmorCost", 4, //
      "Number of levels required for the Apiarist Armor upgrade.").setRange(1, 99).sync();

  public static final IValue<Integer> naturalistEyeCost = F.make("naturalistEyeCost", 4, //
      "Number of levels required for the Naturalist Eye upgrade.").setRange(1, 99).sync();

}
