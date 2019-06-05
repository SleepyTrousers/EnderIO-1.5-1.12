package crazypants.enderio.base.config.config;

import info.loenwind.autoconfig.factory.IValue;
import info.loenwind.autoconfig.factory.IValueFactory;

public final class FarmingConfig {

  public static final IValueFactory F = BaseConfig.F.section("farming");

  public static final IValue<Integer> treeHarvestRadius = F.make("harvestRadius", 7, //
      "Radius (in addition to farm area) for harvesting logs.").setRange(1, 64).sync();
  public static final IValue<Integer> treeHarvestHeight = F.make("harvestHeight", 30, //
      "Height (from initial block) for harvesting logs.").setRange(1, 255).sync();

  public static final IValue<Integer> rubbertreeHarvestRadius = F.make("harvestRadiusRubberTree", 7, //
      "Radius (in addition to farm area) for harvesting rubber trees.").setRange(1, 64).sync();
  public static final IValue<Integer> rubbertreeHarvestHeight = F.make("harvestHeightRubberTree", 30, //
      "Height (from initial block) for harvesting rubber trees.").setRange(1, 255).sync();

}
