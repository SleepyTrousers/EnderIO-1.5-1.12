package crazypants.enderio.zoo.config;

import crazypants.enderio.base.config.factory.IValue;
import crazypants.enderio.base.config.factory.IValueFactory;

public final class ZooConfig {

  public static final IValueFactory F = Config.F.section("difficulty");

  public static final IValue<Integer> apiaristArmorCost = F.make("apiaristArmorCost", 4, //
      "Number of levels required for the Apiarist Armor upgrade.").setRange(1, 99).sync();

  public static final IValueFactory F0 = Config.F.section("mobs");
  public static final IValueFactory F1 = F0.section(".endrminy");

}
