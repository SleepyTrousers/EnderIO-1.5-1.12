package crazypants.enderio.base.config.config;

import crazypants.enderio.base.config.Config.Section;
import crazypants.enderio.base.config.SectionedValueFactory;
import crazypants.enderio.base.config.ValueFactory.IValue;

public final class UpgradeConfig {

  public static final SectionedValueFactory F = new SectionedValueFactory(BaseConfig.F, new Section("", "upgrades"));

  public static final IValue<Integer> explosiveUpgradeCost = F.make("explosiveUpgradeCost", 8, //
      "Cost for the explosive upgrade in levels.").setRange(1, 99).sync();

  public static final IValue<Integer> explosiveUpgradeEnergyPerBlock = F.make("explosiveUpgradeEnergyPerBlock", 20, //
      "Extra energy the explosive upgrade uses to blow up blocks. This goes on top of the energy used to counteract durability loss.").setMin(0).sync();

  public static final IValue<Float> explosiveUpgradeDurabilityChance = F.make("explosiveUpgradeDurabilityChance", .3f, //
      "Chance that employing the explosive upgrade to blow up extra blocks costs the pickaxe durability.").setRange(0, 1).sync();

  public static final IValue<Boolean> explosiveUpgradeUnlimitedTargets = F
      .make("explosiveUpgradeUnlimitedTargets", false, //
          "Should the explosive upgrade blow up any kind of block the pickaxe can mine? If disabled, only a limited list of trash blocks will be blown up. "
              + "Enable this in modpacks that have a large number of modded stone or dirt in their worldgen.")
      .sync();

}
