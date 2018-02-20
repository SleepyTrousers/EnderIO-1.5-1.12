package crazypants.enderio.integration.forestry.config;

import crazypants.enderio.base.config.Config.Section;
import crazypants.enderio.base.config.SectionedValueFactory;
import crazypants.enderio.base.config.ValueFactory.IValue;

public final class ForestryConfig {

  public static final SectionedValueFactory F = new SectionedValueFactory(Config.F, new Section("", "items.darksteel.upgrades.forestry"));

  public static final IValue<Integer> apiaristArmorCost = F.make("apiaristArmorCost", 4, //
      "Number of levels required for the Apiarist Armor upgrade.").setRange(1, 99).sync();

  public static final IValue<Integer> naturalistEyeCost = F.make("naturalistEyeCost", 4, //
      "Number of levels required for the Naturalist Eye upgrade.").setRange(1, 99).sync();

}
